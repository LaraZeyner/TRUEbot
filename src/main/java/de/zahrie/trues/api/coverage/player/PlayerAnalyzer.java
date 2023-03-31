package de.zahrie.trues.api.coverage.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.team.model.Standing;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.discord.builder.embed.EmbedFieldBuilder;
import de.zahrie.trues.api.riot.matchhistory.champion.Champion;
import de.zahrie.trues.api.riot.matchhistory.performance.Lane;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.discord.scouting.Scouting;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

public record PlayerAnalyzer(Player player, Scouting.ScoutingGameType type, Team team, int days) {

  public List<MessageEmbed.Field> analyzePicks(Lane lane) {
    final List<MessageEmbed.Field> fields = new ArrayList<>();
    fields.add(getPlayerHeadField(lane));
    final var data = new EmbedFieldBuilder<>(handlePicks())
        .add("Champion", PlayerAnalyzer.PlayerAnalyzerData::getChampionString)
        .add("Competitive", PlayerAnalyzer.PlayerAnalyzerData::getCompetitiveString)
        .add("Alle Games", PlayerAnalyzer.PlayerAnalyzerData::getMatchmadeString);
    fields.addAll(data.build());
    return fields;
  }

  public List<MessageEmbed.Field> analyzeMatchups(Lane lane) {
    final List<MessageEmbed.Field> fields = new ArrayList<>();
    fields.add(getPlayerHeadField(lane));
    final var data = new EmbedFieldBuilder<>(handleMatchups().stream().sorted().toList())
        .add("Matchup", PlayerMatchupData::getChampionString)
        .add("Games", playerMatchupData -> String.valueOf(playerMatchupData.standing().getGames()))
        .add("Winrate", PlayerMatchupData::getWinrate);
    fields.addAll(data.build());
    return fields;
  }

  private MessageEmbed.Field getPlayerHeadField(Lane lane) {
    return new MessageEmbed.Field(lane.getDisplayName() + ": " + player.getSummonerName() + "(" + getGames() + " Games - " + player.getElo() + ")", "KDA: TBD - Gold: TBD - Damage: TBD - CS/VS: TBD", false);
    //TODO (Abgie) 30.03.2023: Playerstats
  }

  private List<PlayerMatchupData> handleMatchups() {
    final List<Object[]> matchupList = getData("Matchups");
    return matchupList.stream().map(objects -> new PlayerMatchupData((Champion) objects[0],
        new Standing((int) ((int) objects[1] * (double) objects[2]), (int) ((int) objects[1] * (1 - ((double) objects[2])))))).toList();
  }

  private List<PlayerAnalyzerData> handlePicks() {
    final var time = new Time(days * -1);
    List<Object[]> presentPicks = getData("Presence");
    if (presentPicks.isEmpty())
      presentPicks = Database.Find.findObjectList(new String[]{"player", "start"}, new Object[]{player, time}, "Player.getPresenceMatchmade");
    final List<Object[]> picksList = getData("Picks");
    final Map<Champion, Integer> pickMap = picksList.stream().collect(Collectors.toMap(objects -> (Champion) objects[0], objects -> (Integer) objects[1], (a, b) -> b));
    final List<Object[]> mmList = Database.Find.findObjectList(new String[]{"player", "start"}, new Object[]{player, time}, "Player.getGamesMatchmade");
    final Map<Champion, Integer> mmMap = mmList.stream().collect(Collectors.toMap(objects -> (Champion) objects[0], objects -> (Integer) objects[1], (a, b) -> b));
    final List<Object[]> winsList = Database.Find.findObjectList(new String[]{"player", "start"}, new Object[]{player, time}, "Player.getWinsMatchmade");
    final Map<Champion, Integer> winsMap = winsList.stream().collect(Collectors.toMap(objects -> (Champion) objects[0], objects -> (Integer) objects[1], (a, b) -> b));

    final List<Object[]> games = getData("Games");
    final Integer amountOfGames = (Integer) games.get(0)[0];
    final Map<Champion, PlayerAnalyzerData> data = new HashMap<>();
    for (final Object[] presentPick : presentPicks) {
      final Champion champion = (Champion) presentPick[0];
      final int occurrences = (int) presentPick[1];
      final int picks = pickMap.get(champion);
      final int mmGames = mmMap.get(champion);
      final int wins = winsMap.get(champion);
      if (picks > 0 || mmGames >= 10) {
        data.put(champion, new PlayerAnalyzerData(champion, occurrences * 1. / amountOfGames, picks, mmGames, wins));
      }
    }
    for (Object[] objects : picksList) {
      final Champion champion = (Champion) objects[0];
      final int amount = (int) objects[1];
      if (!data.containsKey(champion) && amount >= 10) {
        data.put(champion, new PlayerAnalyzerData(champion, 0, 0, amount, winsMap.get(champion)));
      }
    }
    return data.values().stream().toList();
  }

  public int getGames() {
    final List<Object[]> games = getData("Games");
    return (int) games.get(0)[0];
  }

  private List<Object[]> getData(String key) {
    final var time = new Time(days * -1);
    return switch (type) {
      case PRM_ONLY ->
          Database.Find.findObjectList(new String[]{"player", "start"}, new Object[]{player, time}, "Player.get" + key + "PRM");
      case PRM_CLASH ->
          Database.Find.findObjectList(new String[]{"player", "start"}, new Object[]{player, time}, "Player.get" + key + "PRMClash");
      case TEAM_GAMES ->
          Database.Find.findObjectList(new String[]{"player", "start", "team"}, new Object[]{player, time, team}, "Player.get" + key + "TeamGames");
      case MATCHMADE ->
          Database.Find.findObjectList(new String[]{"player", "start"}, new Object[]{player, time}, "Player.get" + key + "Matchmade");
    };
  }

  public record PlayerAnalyzerData(Champion champion, double presence, int competitiveGames, int matchMadeGames, int matchMadeWins) {
    public String getChampionString() {
      return champion.getName();
    }

    public String getCompetitiveString() {
      return Math.round(presence * 100) + "% - " + competitiveGames;
    }

    public String getMatchmadeString() {
      return matchMadeGames + " - " + Math.round(matchMadeWins * 100.0 / matchMadeGames) + "%";
    }
  }

  public record PlayerMatchupData(Champion champion, Standing standing) implements Comparable<PlayerMatchupData> {
    public String getChampionString() {
      return champion.getName();
    }

    public String getWinrate() {
      return standing.getWinrate().toString();
    }

    @Override
    public int compareTo(@NotNull PlayerMatchupData o) {
      return standing.compareTo(o.standing);
    }
  }
}
