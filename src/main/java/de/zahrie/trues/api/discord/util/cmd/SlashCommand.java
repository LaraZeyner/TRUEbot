package de.zahrie.trues.api.discord.util.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.zahrie.trues.api.discord.builder.InfoPanelBuilder;
import de.zahrie.trues.api.discord.util.cmd.annotations.Command;
import de.zahrie.trues.api.discord.util.cmd.annotations.Embed;
import de.zahrie.trues.api.discord.util.cmd.annotations.Msg;
import de.zahrie.trues.api.discord.util.cmd.annotations.Option;
import de.zahrie.trues.api.discord.builder.EmbedWrapper;
import de.zahrie.trues.util.Const;
import de.zahrie.trues.util.Time;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;

/**
 * Created by Lara on 10.02.2023 for TRUEbot
 */
@Getter
@Setter
public abstract class SlashCommand {
  private String name;
  private String description;
  private List<OptionData> options = new ArrayList<>();
  private List<SlashCommand> subCommands = new ArrayList<>();
  private List<AutoCompletion> completions = new ArrayList<>();
  private boolean defered = false;

  public SlashCommand() {
    final Command annotation = getClass().asSubclass(this.getClass()).getAnnotation(Command.class);
    if (annotation == null) {
      return;
    }

    this.name = annotation.name();
    this.description = annotation.descripion();

    for (Option option : annotation.options()) {
      final boolean hasCompletion = !option.completion().isEmpty();
      final var optionData = new OptionData(option.type(), option.name(), option.description(), option.required(), hasCompletion);
      Arrays.stream(option.choices()).forEach(choice -> optionData.addChoice(choice, choice));
      this.options.add(optionData);
      if (hasCompletion) {
        final var completion = new AutoCompletion(option.name(), option.completion());
        this.completions.add(completion);
      }
    }
  }

  public abstract void onCommand(SlashCommandInteractionEvent event);

  protected void send(SlashCommandInteractionEvent event, Object... data) {
    final var annotation = getMessage();

    if (annotation == null) {
      event.getHook().sendMessage("Internal Error").setEphemeral(true).queue();
      return;
    }

    final String text = String.format(annotation.value(), data);
    if (annotation.embeds().length == 0) {
      event.getHook().sendMessage(text).setEphemeral(annotation.ephemeral()).queue();
      return;
    }

    final List<EmbedWrapper> wrappers = new ArrayList<>();
    for (Embed embed : annotation.embeds()) {
      final String title = embed.value().equals("") ? text : embed.value();
      final var builder = new InfoPanelBuilder(title, embed.description(), embed.queries());
      wrappers.add(builder.build());
    }

    final List<String> wrapperStrings = new ArrayList<>();
    StringBuilder out = new StringBuilder();
    for (EmbedWrapper wrapper : wrappers) {
      for (String t : wrapper.merge()) {
          if (out.length() + t.length() > Const.DISCORD_MESSAGE_MAX_CHARACTERS) {
            wrapperStrings.add(out.toString());
            out = new StringBuilder(t);
          } else {
            out.append(t);
          }
        out.append("\n\n\n\n");
      }
    }
    String output = out.toString();
    output += "zuletzt aktualisiert " + Time.DEFAULT.now();
    wrapperStrings.add(output);

    final List<MessageEmbed> wrapperEmbeds = wrappers.stream().flatMap(wrapper -> wrapper.getEmbeds().stream()).toList();

    WebhookMessageCreateAction<Message> message;
    if (!wrapperStrings.isEmpty()) {
      message = event.getHook().sendMessage(wrapperStrings.get(0));
      if (!wrapperEmbeds.isEmpty()) {
        message = message.setEmbeds(wrapperEmbeds);
      }
      if (wrapperStrings.size() > 1 && !annotation.ephemeral()) {
        for (int i = 1; i < wrapperStrings.size(); i++) {
          final String msg = wrapperStrings.get(i);
          event.getChannel().sendMessage(msg).queue();
        }
      }
    } else if (!wrapperEmbeds.isEmpty()) {
      message = event.getHook().sendMessageEmbeds(wrapperEmbeds);
    } else {
      message = event.getHook().sendMessage("no Data");
    }

    message = message.setEphemeral(annotation.ephemeral());
    message.queue();
  }

  public void handleAutoCompletion(CommandAutoCompleteInteractionEvent event) {
    for (AutoCompletion completion : completions) {
      if (completion.optionName().equals(event.getFocusedOption().getName())) {
        event.replyChoices(completion.getData()).queue();
        break;
      }
    }
  }

  public void handleCommand(SlashCommandInteractionEvent event) {
    handleCommand(event.getFullCommandName(), event);
  }

  private Msg getMessage() {
    try {
      return getClass().asSubclass(this.getClass())
          .getMethod("onCommand", SlashCommandInteractionEvent.class)
          .getAnnotation(Msg.class);
    } catch (NoSuchMethodException ignored) {}
    return null;
  }

  private void handleCommand(String fullCommandName, SlashCommandInteractionEvent event) {
    if (fullCommandName.startsWith(this.name)) {
      if (!this.defered) {
        final var annotation = getMessage();
        event.deferReply(annotation == null || annotation.ephemeral()).queue(); // TODO ephemeral
        this.defered = true;
      }

      this.onCommand(event);
    }

    if (fullCommandName.split(" ").length == 1) {
      return;
    }

    this.subCommands.stream().filter(subCommand -> subCommand.getName().equals(fullCommandName.split(" ")[1])).findFirst()
        .ifPresent(validSubCommand -> validSubCommand.handleCommand(fullCommandName.substring(fullCommandName.indexOf(" ")), event));
  }

  public SlashCommandData commandData() {
    final var subCmds = this.subCommands.stream().filter(subCommand -> subCommand.getSubCommands().isEmpty()).toList();
    final var subCmdGroups = this.subCommands.stream().filter(subCommand -> !subCommand.getSubCommands().isEmpty()).toList();
    for (var option : options) {
      boolean hasAutoComplete = completions.stream().anyMatch(autoCompletion -> autoCompletion.optionName().equals(option.getName()));
        option.setAutoComplete(hasAutoComplete);
    }
    return Commands.slash(this.name, this.description)
        .setGuildOnly(true)
        .addOptions(options)
        .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
        .addSubcommands(subCmds.stream().map(SlashCommand::getSubCommand).toList())
        .addSubcommandGroups(subCmdGroups.stream().map(SlashCommand::getSubCommandGroup).toList());
  }

  private SubcommandData getSubCommand() {
    return new SubcommandData(this.name, this.description)
        .addOptions(this.options);
  }

  private SubcommandGroupData getSubCommandGroup() {
    return new SubcommandGroupData(this.name, this.description)
        .addSubcommands(this.subCommands.stream().map(SlashCommand::getSubCommand).toList());
  }
}
