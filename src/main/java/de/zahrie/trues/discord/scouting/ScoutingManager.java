package de.zahrie.trues.discord.scouting;

import java.util.HashMap;
import java.util.Map;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.team.model.Team;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class ScoutingManager {
  private static final Map<OrgaTeam, Scouting> scoutings = new HashMap<>();

  public static Scouting forTeam(OrgaTeam team) {
    return scoutings.getOrDefault(team, null);
  }

  public static void updateThread(ThreadChannel threadChannel) {
    scoutings.values().stream().filter(scouting -> scouting.thread().equals(threadChannel)).findFirst().ifPresent(Scouting::update);
  }

  public static void addForTeam(OrgaTeam orgaTeam, Participator participator, Match match) {
    Scouting scouting = forTeam(orgaTeam);
    if (scouting == null || !scouting.match().equals(match)) {
      scouting = new Scouting(orgaTeam, participator, match);
      scoutings.put(scouting.orgaTeam(), scouting);
    }
    scouting.update();
  }

  public static void custom(Team team, IReplyCallback event, Scouting.ScoutingType type) {
    custom(team, event, type, null, 365, 1);
  }

  public static void custom(Team team, IReplyCallback event, Scouting.ScoutingType type, Scouting.ScoutingGameType gameType, Integer days) {
    custom(team, event, type, gameType, days, 1);
  }

  public static void custom(Team team, IReplyCallback event, Scouting.ScoutingType type, Scouting.ScoutingGameType gameType, Integer days, Integer page) {
    new Scouting(null, new Participator(false, team), null, null).sendCustom(event, type, gameType, days, page);
  }
}
