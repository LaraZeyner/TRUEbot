package de.zahrie.trues.api.discord;

import java.util.ArrayList;
import java.util.List;

import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.datatypes.symbol.Chain;
import de.zahrie.trues.api.discord.builder.EmbedWrapper;
import de.zahrie.trues.api.discord.builder.InfoPanelBuilder;
import de.zahrie.trues.api.discord.builder.modal.ModalHandler;
import de.zahrie.trues.api.discord.command.context.UseView;
import de.zahrie.trues.api.discord.command.slash.annotations.Embed;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.member.DiscordMember;
import de.zahrie.trues.api.discord.member.DiscordMemberFactory;
import de.zahrie.trues.util.Const;
import lombok.Data;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.slf4j.helpers.MessageFormatter;

@Data
public abstract class Replyer {
  private final Class<? extends IReplyCallback> clazz;
  protected String name;
  protected IReplyCallback event;
  protected boolean end = false;

  protected boolean reply(String message) {
    reply(message, true);
    return true;
  }

  protected boolean reply(String message, boolean ephemeral) {
    event.reply(message).setEphemeral(ephemeral).queue();
    end = true;
    return true;
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

  protected DiscordMember getInvoker() {
    return getInvokingMember() == null ? null : DiscordMemberFactory.getMember(getInvokingMember());
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
    final String output = error ? annotation.error() : annotation.value();
    final String text = data.length == 0 ? output : MessageFormatter.format(output, data).getMessage();
    if (annotation.embeds().length == 0) {
      return reply(text, annotation.ephemeral());
    }
    final List<EmbedWrapper> wrappers = new ArrayList<>();
    for (Embed embed : annotation.embeds()) {
      final String title = embed.value().equals("") ? text : embed.value();
      final var builder = new InfoPanelBuilder(title, embed.description(), embed.queries());
      wrappers.add(builder.build());
    }

    final List<Chain> wrapperChains = new ArrayList<>();
    Chain out = Chain.of();
    for (EmbedWrapper wrapper : wrappers) {
      for (Chain t : wrapper.merge()) {
        if (out.length() + t.length() > Const.DISCORD_MESSAGE_MAX_CHARACTERS) {
          wrapperChains.add(out);
          out = t;
        } else {
          out = out.add(t);
        }
        out = out.add("\n\n\n\n");
      }
    }

    out = out.add("zuletzt aktualisiert ")
        .add(Time.of().chain(TimeFormat.DEFAULT));
    wrapperChains.add(out);

    final List<MessageEmbed> wrapperEmbeds = wrappers.stream().flatMap(wrapper -> wrapper.getEmbeds().stream()).toList();

    ReplyCallbackAction message;
    if (!wrapperChains.isEmpty()) {

      message = event.reply(wrapperChains.get(0).toString());
      if (!wrapperEmbeds.isEmpty()) {
        message = message.setEmbeds(wrapperEmbeds);
      }
      if (wrapperChains.size() > 1 && !annotation.ephemeral()) {
        for (int i = 1; i < wrapperChains.size(); i++) {
          final Chain msg = wrapperChains.get(i);
          ((SlashCommandInteractionEvent) event).getChannel().sendMessage(msg.toString()).queue();

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

  protected void sendModal() {
    sendModal(false);
  }

  protected boolean sendModal(boolean someBool) {
    if (event == null) {
      return false;
    }
    final UseView view = getModalView();
    if (view == null) {
      return false;
    }
    final String modalId = getModalView().value();
    final Modal modal = new ModalHandler.Find(getInvoker(), getTarget()).getModal(modalId, someBool);
    if (modal != null) {
      if (event instanceof GenericCommandInteractionEvent) {
        ((GenericCommandInteractionEvent) event).replyModal(modal).queue();
        end = true;
        return true;
      }
    }
    return false;
  }

  private UseView getModalView() {
    try {
      return getClass().asSubclass(this.getClass())
          .getMethod("execute", UserContextInteractionEvent.class)
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

  protected DiscordMember getTarget() {
    return getTargetMember() == null ? null : DiscordMemberFactory.getMember(getTargetMember());
  }
}
