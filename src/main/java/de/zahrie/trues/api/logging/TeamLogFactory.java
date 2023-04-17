package de.zahrie.trues.api.logging;


import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.api.database.Database;

public class TeamLogFactory {
  public static void create(DiscordUser invoker, DiscordUser target, String details, TeamLog.TeamLogAction action, OrgaTeam team) {
    final TeamLog teamLog = new TeamLog(invoker, target, details, action, team);
    Database.insertAndCommit(teamLog);
    Nunu.getInstance().getGuild().getTextChannelsByName("\uD83D\uDCBE︱team-log", true).stream()
        .findFirst().ifPresent(textChannel -> textChannel.sendMessage(details).queue());
  }
}
