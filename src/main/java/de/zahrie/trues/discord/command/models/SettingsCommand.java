package de.zahrie.trues.discord.command.models;

import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.discord.Settings;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "settings", descripion = "Einstellungen", perm = @Perm(PermissionRole.EVERYONE), options = {
    @Option(name = "typ", description = "Spiel auswählen", choices = {"Benachrichtigung", "Geburtstag", "Riot Account"}),
    @Option(name = "value", description = "Wert eintragen"),
    @Option(name = "user", description = "Nutzer", required = false, type = OptionType.USER)
})
public class SettingsCommand extends SlashCommand {
  @Override
  @Msg(value = "Du bist nicht auf dem Server.", error = "Der Nutzer ist nicht auf dem Server.")
  public boolean execute(SlashCommandInteractionEvent event) {
    final var game = find("typ").string();
    final var value = find("value").string();
    final OptionMapping userMapping = event.getOption("user");

    final DiscordUser invoker = getInvoker();
    if (invoker == null) return sendMessage();
    final DiscordUser target = determineTarget(userMapping);
    if (target == null) return errorMessage();
    return switch (game) {
      case "Benachrichtigung" -> reply(Settings.RegistrationAction.NOTIFY.getAction().apply(target, value));
      case "Geburtstag" -> reply(Settings.RegistrationAction.BDAY.getAction().apply(target, value));
      case "Riot Account" -> reply(Settings.RegistrationAction.LOL_NAME.getAction().apply(target, value));
      default -> false;
    };
  }

  private DiscordUser determineTarget(OptionMapping userMapping) {
    if (userMapping == null || !getInvoker().isAbove(DiscordGroup.SUBSTITUDE)) {
      return getInvoker();
    }
    final Member target = userMapping.getAsMember();
    return target == null ? null : DiscordUserFactory.getDiscordUser(target);
  }
}
