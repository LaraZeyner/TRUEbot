package de.zahrie.trues.discord.command.models;

import java.time.LocalDateTime;

import de.zahrie.trues.api.community.application.Application;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.io.SelectionQueries;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "add", descripion = "Ein Bewerbungsgespräch führen", perm = @Perm(PermissionRole.MANAGEMENT), options = {
    @Option(name = "bewerbung", description = "Bewerbungsdings", completion = SelectionQueries.PENDING_APPLICATIONS),
    @Option(name = "zeitpunkt", description = "Zeitpunkt")
})
@ExtensionMethod({StringUtils.class})
public class TryoutAddCommand extends SlashCommand {
  @Override
  @Msg(value = "Der Termin wurde gespeichert.")
  public boolean execute(SlashCommandInteractionEvent event) {
    final String applicationString = find("bewerbung").string();
    final int applicationId = applicationString.before(".").intValue();
    final LocalDateTime dateTime = find("zeitpunkt").time();
    if (dateTime == null) return reply("Der Zeitpunkt konnte nicht umgewandelt werden.");

    final Application application = new Query<>(Application.class).entity(applicationId);
    application.getUser().schedule(dateTime, getInvoker());
    return sendMessage();
  }
}
