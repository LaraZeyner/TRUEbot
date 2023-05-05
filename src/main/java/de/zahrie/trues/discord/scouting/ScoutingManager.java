package de.zahrie.trues.discord.scouting;

import java.util.HashMap;
import java.util.Map;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.player.PlayerAnalyzer;
import de.zahrie.trues.api.coverage.player.model.PlayerBase;
import de.zahrie.trues.api.coverage.team.model.TeamBase;
import de.zahrie.trues.api.riot.matchhistory.champion.Champion;
import de.zahrie.trues.api.riot.matchhistory.performance.Lane;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import de.zahrie.trues.util.Util;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.jetbrains.annotations.Nullable;

public class ScoutingManager {
  private static final Map<OrgaTeam, Scouting> scoutings = new HashMap<>();

  public static Map<OrgaTeam, Scouting> getScoutings() {
    return scoutings;
  }

  @Nullable
  public static Scouting forTeam(OrgaTeam team) {
    return scoutings.getOrDefault(team, null);
  }

  public static void updateThread(ThreadChannel threadChannel) {
    scoutings.values().stream().filter(scouting -> scouting.thread() != null).filter(scouting -> scouting.thread().equals(threadChannel)).findFirst().ifPresent(Scouting::update);
  }

  public static void addForTeam(OrgaTeam orgaTeam, Participator participator, Match match) {
    Scouting scouting = forTeam(orgaTeam);
    if (scouting == null || !scouting.match().equals(match)) {
      scouting = new Scouting(orgaTeam, participator, match);
      scoutings.put(scouting.orgaTeam(), scouting);
    }
    scouting.update();
  }

  public static void custom(TeamBase team, IReplyCallback event, Scouting.ScoutingType type) {
    custom(team, event, type, null, 365, 1);
  }

  public static void custom(TeamBase team, IReplyCallback event, Scouting.ScoutingType type, ScoutingGameType gameType, Integer days) {
    custom(team, event, type, gameType, days, 1);
  }

  public static void custom(TeamBase team, IReplyCallback event, Scouting.ScoutingType type, ScoutingGameType gameType, Integer days, Integer page) {
    new Scouting(null, new Participator(null, false, team), null, null).sendCustom(event, type, gameType, days, page);
  }

  public static void handlePlayerHistory(IReplyCallback event, @NonNull PlayerBase player, @Nullable Champion champion, @NonNull ScoutingGameType gameType, @Nullable Lane lane) {
    final String championOutput = Util.avoidNull(champion, "alle", Champion::getName);
    final String laneOutput = Util.avoidNull(lane, "alle", Lane::getDisplayName);
    final EmbedBuilder builder = new EmbedBuilder()
        .setTitle("Matchhistory von " + player.getSummonerName())
        .setDescription("Gametyp: **" + gameType.getDisplayName() + "**\nChampion: **" + championOutput + "**\nLane: **" + laneOutput + "**");
    new PlayerAnalyzer(player, gameType, player.getTeam(), 1000).analyzeGamesWith(champion, lane).forEach(builder::addField);
    event.replyEmbeds(builder.build()).queue();
  }
}
