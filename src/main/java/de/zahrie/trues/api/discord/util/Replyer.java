package de.zahrie.trues.api.discord.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.api.discord.builder.EmbedWrapper;
import de.zahrie.trues.api.discord.builder.InfoPanelBuilder;
import de.zahrie.trues.api.discord.builder.embed.CustomEmbedData;
import de.zahrie.trues.api.discord.builder.modal.ModalHandler;
import de.zahrie.trues.api.discord.builder.queryCustomizer.CustomQuery;
import de.zahrie.trues.api.discord.command.context.UseView;
import de.zahrie.trues.api.discord.command.slash.annotations.Embed;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.util.Const;
import lombok.Data;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

@Data
@ExtensionMethod(StringUtils.class)
public abstract class Replyer {
  private final Class<? extends IReplyCallback> clazz;
  private final List<CustomEmbedData> customEmbedData = new ArrayList<>();
  protected String name;
  protected IReplyCallback event;
  protected boolean end = false;

  protected void addEmbedData(String key, List<Object[]> data) {
    customEmbedData.add(new CustomEmbedData(key, data));
  }

  protected void addEmbedData(String key, Object[]... data) {
    customEmbedData.add(new CustomEmbedData(key, Arrays.stream(data).toList()));
  }

  protected boolean reply(String message) {
    return reply(message, true);
  }

  protected boolean reply(String message, boolean ephemeral) {
    try {
      event.reply(message).setEphemeral(ephemeral).queue();
      end = true;
      return true;
    } catch (RejectedExecutionException exception) {
      return false;
    }
  }

  protected Member getInvokingMember() {
    if (event == null) {
      reply("Internal Error");
      return null;
    }
    final Member invoker = event.getMember();
    if (invoker == null) {
      reply("Du existierst nicht");
    }
    return invoker;
  }

  protected DiscordUser getInvoker() {
    return getInvokingMember() == null ? null : DiscordUserFactory.getDiscordUser(getInvokingMember());
  }

  protected Msg getMessage() {
    try {
      return getClass().asSubclass(this.getClass())
          .getMethod("execute", clazz)
          .getAnnotation(Msg.class);
    } catch (NoSuchMethodException ignored) {
      reply("Internal Error", true);
    }
    return null;
  }

  protected boolean send(boolean condition, Object... data) {
    return condition ? sendMessage(data) : errorMessage(data);
  }

  protected boolean sendMessage(Object... data) {
    final var annotation = getMessage();
    return annotation != null && performMsg(false, annotation, data);
  }

  protected boolean errorMessage(Object... data) {
    final var annotation = getMessage();
    return annotation != null && performMsg(true, annotation, data);
  }

  private boolean performMsg(boolean error, Msg annotation, Object[] data) {
    String output = error ? annotation.error() : annotation.value();
    if (annotation.embeds().length == 0) {
      return reply(output.format(data), annotation.ephemeral());
    }
    final List<EmbedWrapper> wrappers = new ArrayList<>();
    for (Embed embed : annotation.embeds()) {
      if (!embed.value().equals("")) {
        output = embed.value();
      }
      final String title = output.format(data);
      final String description = embed.description().format(output.count("{}"), data);
      final var builder = new InfoPanelBuilder(title, description, Arrays.stream(embed.queries()).map(CustomQuery::fromDBQuery).toList(), customEmbedData);
      wrappers.add(builder.build());
    }

    final List<String> wrapperChains = new ArrayList<>();
    StringBuilder out = new StringBuilder();
    for (EmbedWrapper wrapper : wrappers) {
      for (String t : wrapper.merge()) {
        if (out.length() + t.length() > Const.DISCORD_MESSAGE_MAX_CHARACTERS) {
          wrapperChains.add(out.toString());
          out = new StringBuilder(t);
        } else {
          out.append(t);
        }
        out.append("\n\n\n\n");
      }
    }

    out.append("zuletzt aktualisiert ").append(TimeFormat.DEFAULT.now());
    wrapperChains.add(out.toString());

    final List<MessageEmbed> wrapperEmbeds = wrappers.stream().flatMap(wrapper -> wrapper.getEmbeds().stream()).toList();

    ReplyCallbackAction message;
    if (!wrapperChains.isEmpty()) {

      message = event.reply(wrapperChains.get(0));
      if (!wrapperEmbeds.isEmpty()) {
        message = message.setEmbeds(wrapperEmbeds);
      }
      if (wrapperChains.size() > 1 && !annotation.ephemeral()) {
        for (int i = 1; i < wrapperChains.size(); i++) {
          final String msg = wrapperChains.get(i);
          ((SlashCommandInteractionEvent) event).getChannel().sendMessage(msg).queue();

        }
      }
    } else if (!wrapperEmbeds.isEmpty()) {
      message = event.replyEmbeds(wrapperEmbeds);
    } else {
      message = event.reply("no Data");
    }

    message = message.setEphemeral(annotation.ephemeral());
    message.queue();
    end = true;
    return true;
  }

  protected boolean sendModal() {
    return sendModal(false, 1);
  }

  protected boolean sendModal(int index) {
    return sendModal(false, index);
  }

  protected boolean sendModal(boolean someBool) {
    return sendModal(someBool, 1);
  }

  protected boolean sendModal(boolean someBool, int index) {
    if (event == null) {
      return false;
    }
    final UseView view = getModalView();
    if (view == null) {
      return false;
    }
    final String modalId = view.value()[index];
    final Modal modal = new ModalHandler.Find(getInvoker(), getTarget()).getModal(modalId, someBool);
    if (modal == null) {
      return false;
    }
    if (!(event instanceof GenericCommandInteractionEvent)) {
      return false;
    }
    ((GenericCommandInteractionEvent) event).replyModal(modal).queue();
    end = true;
    return true;
  }

  private UseView getModalView() {
    try {
      return getClass().asSubclass(this.getClass())
          .getMethod("execute", clazz)
          .getAnnotation(UseView.class);
    } catch (NoSuchMethodException ignored) {  }
    return null;
  }

  protected Member getTargetMember() {
    if (event == null) {
      reply("Internal Error");

    } else if (event instanceof UserContextInteractionEvent) {
      final Member invoker = ((UserContextInteractionEvent) event).getTargetMember();
      if (invoker == null) {
        reply("Du existierst nicht");
      }
      return invoker;
    }
    return null;
  }

  protected DiscordUser getTarget() {
    return getTargetMember() == null ? null : DiscordUserFactory.getDiscordUser(getTargetMember());
  }
}
