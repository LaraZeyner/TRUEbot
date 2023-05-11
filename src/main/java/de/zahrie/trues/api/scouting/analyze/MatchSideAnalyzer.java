package de.zahrie.trues.api.scouting.analyze;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.merakianalytics.orianna.types.core.match.Match;
import com.merakianalytics.orianna.types.core.match.Participant;
import com.merakianalytics.orianna.types.core.match.ParticipantStats;
import com.merakianalytics.orianna.types.core.searchable.SearchableList;
import de.zahrie.trues.api.coverage.player.PlayerFactory;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.riot.KDA;
import de.zahrie.trues.api.riot.Side;
import de.zahrie.trues.api.riot.champion.Champion;
import de.zahrie.trues.api.riot.game.Game;
import de.zahrie.trues.api.riot.game.GameType;
import de.zahrie.trues.api.riot.game.MatchUtils;
import de.zahrie.trues.api.riot.game.Selection;
import de.zahrie.trues.api.riot.performance.Lane;
import de.zahrie.trues.api.riot.performance.Matchup;
import de.zahrie.trues.api.riot.performance.ParticipantUtils;
import de.zahrie.trues.api.riot.performance.Performance;
import de.zahrie.trues.api.riot.performance.TeamPerf;
import de.zahrie.trues.api.riot.TeamExtension;
import lombok.Data;
import lombok.experimental.ExtensionMethod;

@Data
@ExtensionMethod(TeamExtension.class)
public class MatchSideAnalyzer {
  private final Match match;
  private final Game game;
  private final Side side;
  private final com.merakianalytics.orianna.types.core.match.Team team;
  private final List<Participant> validParticipants;

  public MatchSideAnalyzer(Match match, Game game, Side side) {
    this.match = match;
    this.game = game;
    this.side = side;
    this.team = side.getTeam(match);
    this.validParticipants = new TeamParticipantsAnalyzer(match, team).analyze();
  }

  public TeamPerf analyze() {
    final SearchableList<Participant> participants = team.getParticipants();
    final KDA kda = KDA.sum(participants.stream().map(KDA::fromParticipant).collect(Collectors.toSet()));
    final int gold = participants.stream().mapToInt(part -> part.getStats().getGoldEarned()).sum();
    final int damage = participants.stream().map(Participant::getStats).mapToInt(ParticipantStats::getDamageDealt).sum();
    final int vision = participants.stream().map(Participant::getStats).mapToInt(ParticipantStats::getVisionScore).sum();
    final int creeps = participants.stream().map(Participant::getStats).mapToInt(ParticipantStats::getCreepScore).sum();
    final TeamPerf.Objectives objectives = new TeamPerf.Objectives(team.getTowerKills(), team.getDragonKills(), team.getInhibitorKills(), team.getRiftHeraldKills(), team.getBaronKills());
    final var teamPerformance = new TeamPerf(game, side, team.isWinner(), kda, gold, damage, vision, creeps, objectives).create();
    handleTeamOfTeamPerf(teamPerformance);
    validParticipants.forEach(participant -> new ParticipantsAnalyzer(match, teamPerformance, team, participant).analyze());
    return teamPerformance;
  }

  private void handleTeamOfTeamPerf(TeamPerf teamPerformance) {
    if (teamPerformance.getTeam() == null) {
      final Map<Team, Long> teams = team.getParticipants().stream().map(p -> PlayerFactory.findPlayer(p.getSummoner().getPuuid()))
          .filter(Objects::nonNull)
          .map(Player::getTeam)
          .filter(Objects::nonNull)
          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
      final Map.Entry<Team, Long> maxEntry = Collections.max(teams.entrySet(), Map.Entry.comparingByValue());
      if (maxEntry.getValue() > 2) {
        final Team prmTeam = maxEntry.getKey();
        if (prmTeam.getOrgaTeam() != null) game.setOrgaGame(true);
        teamPerformance.setTeam(prmTeam);
      }
    }
  }

  public void analyzeSelections() {
    final com.merakianalytics.orianna.types.core.match.Team team = side.getTeam(match);
    handleSelectionType(team.getBanned(), Selection.SelectionType.BAN);
    handleSelectionType(team.getPicks(), Selection.SelectionType.PICK);
  }

  private void handleSelectionType(List<Champion> champs, Selection.SelectionType type) {
    IntStream.range(0, champs.size()).forEach(i -> new Selection(game, side, type, (byte) (i + 1), champs.get(i)).create());
  }

  public record TeamParticipantsAnalyzer(Match match, com.merakianalytics.orianna.types.core.match.Team team) {
    public List<Participant> analyze() {
      if (MatchUtils.getGameQueue(match).equals(GameType.TOURNAMENT) || MatchUtils.getGameQueue(match).equals(GameType.CUSTOM)) {
        return team.getParticipants();
      }

      final List<Participant> players = team.getParticipants().stream()
          .filter(matchParticipant -> PlayerFactory.findPlayer(matchParticipant.getSummoner().getPuuid()) != null).toList();
      return MatchUtils.getGameQueue(match).equals(GameType.CLASH) && !players.isEmpty() ? team.getParticipants() : players;
    }
  }

  public static record ParticipantsAnalyzer(Match match, TeamPerf teamPerformance, com.merakianalytics.orianna.types.core.match.Team team, Participant participant) {
    public Performance analyze() {
      final Player player = PlayerFactory.getPlayerFromPuuid(participant.getSummoner().getPuuid());
      final ParticipantStats stats = participant.getStats();
      final Lane lane = ParticipantUtils.getPlayedLane(participant);
      final Champion selectedChampion = ParticipantUtils.getSelectedChampion(participant);
      final Participant opponent = ParticipantUtils.getOpponent(participant, match);
      final Champion opposingChampion = opponent == null ? null : ParticipantUtils.getSelectedChampion(opponent);
      final Matchup matchup = new Matchup(selectedChampion, opposingChampion);
      final KDA kda = KDA.fromParticipant(participant);
      return new Performance(teamPerformance, player, lane, matchup, kda, stats.getGoldEarned(), stats.getDamageDealt(), stats.getVisionScore(), stats.getCreepScore()).create();
    }
  }
}
