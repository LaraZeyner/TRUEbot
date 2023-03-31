package de.zahrie.trues.discord.command.models.tryout;

import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.datatypes.symbol.StringExtention;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.api.community.application.Application;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "add", descripion = "Ein Bewerbungsgespräch führen", perm = @Perm(PermissionRole.TEAM_BUILDING), options = {
    @Option(name = "bewerbung", description = "Bewerbungsdings", completion = "Application.pending"),
    @Option(name = "zeitpunkt", description = "Zeitpunkt")
})
@ExtensionMethod({StringExtention.class})
public class TryoutAddCommand extends SlashCommand {
  @Override
  @Msg(value = "Der Termin wurde gespeichert.")
  public boolean execute(SlashCommandInteractionEvent event) {
    final String applicationString = find("bewerbung").string();
    final int applicationId = applicationString.between(null, ".").intValue();
    final String timeString = find("zeitpunkt").string();
    final Time time = timeString.getTime();
    final Application application = Database.Find.find(Application.class, applicationId);
    application.getUser().schedule(time, getInvoker());
    return sendMessage();
  }
}
