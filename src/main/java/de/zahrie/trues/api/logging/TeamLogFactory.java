package de.zahrie.trues.api.logging;


import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.util.Const;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class TeamLogFactory {
  private static final TextChannel LOG_CHANNEL = Nunu.getInstance().getClient().getTextChannelById(Const.TEAM_LOGGING_CHANNEL);

  public static void create(DiscordUser invoker, DiscordUser target, String details, TeamLog.TeamLogAction action, OrgaTeam team) {
    new TeamLog(invoker, target, details, action, team).forceCreate();
    if (LOG_CHANNEL == null) throw new NullPointerException("Team-Log Channel wurde gelöscht");
    LOG_CHANNEL.sendMessage(details).queue();
  }
}
