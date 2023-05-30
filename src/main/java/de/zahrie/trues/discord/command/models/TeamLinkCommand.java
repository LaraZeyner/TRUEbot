
package de.zahrie.trues.discord.command.models;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.team.TeamFactory;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
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
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "link", descripion = "Team erstellen", perm = @Perm(PermissionRole.MANAGEMENT), options = {
    @Option(name = "team", description = "Name des Teams (ohne 'TRUE ')", completion = SelectionQueries.ORGA_TEAMS),
    @Option(name = "id", description = "PRM TeamId (sofern vorhanden)", type = OptionType.INTEGER)
})
@ExtensionMethod(StringUtils.class)
public class TeamLinkCommand extends SlashCommand {
  @Override
  @Msg(value = "Das Team wurde erfolgreich erstellt.", error = "Das Team konnte nicht gefunden werden.")
  public boolean execute(SlashCommandInteractionEvent event) {
    final String name = find("name").string().replace("TRUE ", "");
    final Integer id = find("id").integer();
    final OrgaTeam orgaTeam = OrgaTeam.fromName(name);
    if (orgaTeam == null) return errorMessage();

    final PRMTeam team = TeamFactory.getTeam(id);
    if (team == null) return errorMessage();

    orgaTeam.setTeam(team);
    return sendMessage();
  }
}
