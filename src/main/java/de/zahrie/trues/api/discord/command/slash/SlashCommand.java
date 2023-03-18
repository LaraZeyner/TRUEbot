package de.zahrie.trues.api.discord.command.slash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.zahrie.trues.api.datatypes.symbol.Chain;
import de.zahrie.trues.api.discord.command.PermissionCheck;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.Replyer;
import de.zahrie.trues.util.util.Util;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public abstract class SlashCommand extends Replyer {
  private String description;
  private PermissionCheck permissionCheck;
  private List<OptionData> options = new ArrayList<>();
  private List<SlashCommand> subCommands = List.of();
  private List<AutoCompletion> completions = new ArrayList<>();
  private boolean defered = false;

  public SlashCommand() {
    super(SlashCommandInteractionEvent.class);
    final Command annotation = getClass().asSubclass(this.getClass()).getAnnotation(Command.class);
    if (annotation == null) {
      return;
    }

    this.name = annotation.name();
    this.description = annotation.descripion();
    this.permissionCheck = new PermissionCheck(annotation.perm());

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

  public SlashCommand(SlashCommand... commands) {
    this();
    this.subCommands = List.of(commands);
  }

  public abstract boolean execute(SlashCommandInteractionEvent event);

  public void handleAutoCompletion(CommandAutoCompleteInteractionEvent event) {
    completions.stream().filter(completion -> completion.optionName().equals(event.getFocusedOption().getName()))
        .findFirst().ifPresent(completion -> event.replyChoices(completion.getData()).queue());
  }

  public void handleCommand(SlashCommandInteractionEvent event) {
    handleCommand(event.getFullCommandName(), event);
  }

  private void handleCommand(String fullCommandName, SlashCommandInteractionEvent event) {
    if (fullCommandName.startsWith(name)) {
      if (!defered) {
        final var annotation = getMessage();
        event.deferReply(annotation == null || annotation.ephemeral()).queue(); // TODO ephemeral
        defered = true;
      }

      if (!permissionCheck.check(event.getMember())) {
        reply("Dir fehlen die nötigen Rechte.");
      } else {
        this.event = event;
        this.execute(event);
      }
    }

    if (fullCommandName.split(" ").length == 1) {
      return;
    }

    subCommands.stream().filter(subCommand -> subCommand.getName().equals(fullCommandName.split(" ")[1])).findFirst()
        .ifPresent(validSubCommand -> validSubCommand.handleCommand(fullCommandName.substring(fullCommandName.indexOf(" ")), event));

    if (!end) {
      reply("Internal Error", true);
    }
  }

  public SlashCommandData commandData() {
    final var subCmds = subCommands.stream().filter(subCommand -> subCommand.getSubCommands().isEmpty()).toList();
    final var subCmdGroups = subCommands.stream().filter(subCommand -> !subCommand.getSubCommands().isEmpty()).toList();
    for (var option : options) {
      final boolean hasAutoComplete = completions.stream().anyMatch(autoCompletion -> autoCompletion.optionName().equals(option.getName()));
      option.setAutoComplete(hasAutoComplete);
    }
    return Commands.slash(name, description)
        .setGuildOnly(true)
        .addOptions(options)
        .setDefaultPermissions(DefaultMemberPermissions.DISABLED) //TODO (Abgie) 04.03.2023: remove later
        .addSubcommands(subCmds.stream().map(SlashCommand::getSubCommand).toList())
        .addSubcommandGroups(subCmdGroups.stream().map(SlashCommand::getSubCommandGroup).toList());
  }

  private SubcommandData getSubCommand() {
    final String commandName = name.contains(" ") ? Chain.of(name).between(" ", null, -1).toString() : name;
    return new SubcommandData(commandName, description).addOptions(this.options);
  }

  private SubcommandGroupData getSubCommandGroup() {
    final String commandName = name.contains(" ") ? Chain.of(name).between(" ", null, -1).toString() : name;
    return new SubcommandGroupData(commandName, description).addSubcommands(subCommands.stream().map(SlashCommand::getSubCommand).toList());
  }

  public OptionMapping get(String key) {
    return Util.nonNull(((SlashCommandInteractionEvent)event).getOption(key));
  }

  public <T extends Enum<T>> T get(String key, Class<T> clazz) {
    final OptionMapping optionMapping = get(key);
    return optionMapping == null ? null : Chain.of(optionMapping.getAsString()).toEnum(clazz);
  }
}
