package de.zahrie.trues.discord.command.models;

import java.util.Objects;

import de.zahrie.trues.api.coverage.player.PlayerFactory;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.datatypes.symbol.Chain;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.api.discord.member.DiscordMember;
import de.zahrie.trues.api.discord.member.DiscordMemberFactory;
import de.zahrie.trues.database.Database;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "register", descripion = "Account registrieren", perm = @Perm(PermissionRole.EVERYONE), options = {
    @Option(name = "typ", description = "Spiel auswählen", choices = {"Geburtstag", "Riot Account"}),
    @Option(name = "value", description = "Wert eintragen"),
    @Option(name = "user", description = "Nutzer", required = false, type = OptionType.USER)
})
public class RegisterCommand extends SlashCommand {
  @Override
  @Msg(value = "**{}** wurde mit dem Namen **{}** registriert.", error = "Der Name **{}** konnte nicht gefunden werden.")
  public boolean execute(SlashCommandInteractionEvent event) {
    final var game = Objects.requireNonNull(event.getOption("typ")).getAsString();
    final var value = Objects.requireNonNull(event.getOption("value")).getAsString();
    final OptionMapping userMapping = event.getOption("user");

    final DiscordMember invoker = getInvoker();
    if (invoker == null) return reply("Du bist nicht auf dem Server.");
    final DiscordMember target = determineTarget(userMapping);
    if (target == null) return reply("Der Nutzer ist nicht auf dem Server.");
    return switch (game) {
      case "Geburtstag" -> handleBirthday(value, invoker);
      case "Riot Account" -> handleRiotAccount(value, target);
      default -> false;
    };
  }

  private boolean handleBirthday(String value, DiscordMember member) {
    if (value.matches("^\\d{2}.\\d{2}.")) {
      final Time time = TimeFormat.DAY.of(Chain.of(value));
      member.setBirthday(time.getDay());
      Database.save(member);
      return reply(String.format("Dein Geburtstag wurde für den %s eingetragen", value));
    }
    return false;
  }

  private boolean handleRiotAccount(String username, DiscordMember member) {
    final Player player = PlayerFactory.getPlayer(username);
    if (player == null) return errorMessage(username);
    if (player.getMember() != null) return reply("Der Account wurde bereits verknüpft.");
    player.setMember(member);
    return sendMessage(member.getMember().getNickname(), username);
  }

  private DiscordMember determineTarget(OptionMapping userMapping) {
    if (userMapping == null || !getInvoker().isAbove(DiscordGroup.ACCEPTED)) {
      return getInvoker();
    }
    final Member target = userMapping.getAsMember();
    return target == null ? null : DiscordMemberFactory.getMember(target);
  }
}
