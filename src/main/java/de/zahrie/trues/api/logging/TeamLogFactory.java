package de.zahrie.trues.api.logging;


import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.util.Nunu;

public class TeamLogFactory {
  public static void create(DiscordUser invoker, DiscordUser target, String details, TeamLog.TeamLogAction action, OrgaTeam team) {
    new TeamLog(invoker, target, details, action, team).forceCreate();
    Nunu.getInstance().getGuild().getTextChannelsByName("\uD83D\uDCBE︱team-log", true).stream()
        .findFirst().ifPresent(textChannel -> textChannel.sendMessage(details).queue());
  }
}
