package de.zahrie.trues.discord.command.models.tryout;

import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.datatypes.symbol.StringExtention;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "custom", descripion = "Ein Bewerbungsgespräch führen", perm = @Perm(PermissionRole.TEAM_BUILDING), options = {
    @Option(name = "nutzer", description = "Bewerbungsdings", type = OptionType.USER),
    @Option(name = "zeitpunkt", description = "Zeitpunkt")
})
@ExtensionMethod({StringExtention.class, DiscordUserFactory.class})
public class TryoutCustomCommand extends SlashCommand {
  @Override
  @Msg(value = "Der Termin wurde gespeichert.", error = "Der Nutzer wurde nicht gefunden.")
  public boolean execute(SlashCommandInteractionEvent event) {
    final Member member = find("nutzer").member();
    if (member == null) return errorMessage();
    final DiscordUser discordUser = member.getDiscordUser();
    final String timeString = find("zeitpunkt").string();
    final Time time = timeString.getTime();
    discordUser.schedule(time, getInvoker());
    return sendMessage();
  }
}
