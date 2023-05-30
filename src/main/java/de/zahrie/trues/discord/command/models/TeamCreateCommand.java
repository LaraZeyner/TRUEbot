
package de.zahrie.trues.discord.command.models;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.orgateam.OrgaTeamFactory;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "create", descripion = "Team erstellen", perm = @Perm(PermissionRole.MANAGEMENT), options = {
    @Option(name = "name", description = "Name des Teams (ohne 'TRUEsports ')"),
    @Option(name = "kurz", description = "Abkürzung", required = false),
    @Option(name = "id", description = "PRM TeamId (sofern vorhanden)", required = false, type = OptionType.INTEGER)
})
@ExtensionMethod(StringUtils.class)
public class TeamCreateCommand extends SlashCommand {
  @Override
  @Msg(value = "Das Team wurde erfolgreich erstellt.", error = "Das Team exisitiert bereits. Wähle einen anderen Namen oder gib eine Abkürzung an.")
  public boolean execute(SlashCommandInteractionEvent event) {
    String name = find("name").string();
    if (name.startsWith("TRUEsports ")) name = name.replace("TRUEsports ", "");
    if (name.startsWith("TRUE ")) name = name.replace("TRUE ", "");
    final String abbreviation = find("kurz").string();
    final Integer id = find("id").integer();
    final OrgaTeam orgaTeam = OrgaTeamFactory.create(name, abbreviation, id);
    return orgaTeam != null ? sendMessage() : errorMessage();
  }
}
