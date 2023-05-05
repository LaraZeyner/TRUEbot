package de.zahrie.trues.api.riot.analyze;

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
import com.merakianalytics.orianna.types.core.match.Team;
import com.merakianalytics.orianna.types.core.searchable.SearchableList;
import de.zahrie.trues.api.coverage.player.PlayerFactory;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.team.model.TeamBase;
import de.zahrie.trues.api.riot.matchhistory.KDA;
import de.zahrie.trues.api.riot.matchhistory.Side;
import de.zahrie.trues.api.riot.matchhistory.champion.Champion;
import de.zahrie.trues.api.riot.matchhistory.game.Game;
import de.zahrie.trues.api.riot.matchhistory.game.Selection;
import de.zahrie.trues.api.riot.matchhistory.performance.TeamPerf;
import de.zahrie.trues.api.riot.matchhistory.teamperformance.TeamExtension;
import lombok.Data;
import lombok.experimental.ExtensionMethod;

@Data
@ExtensionMethod(TeamExtension.class)
public class MatchSideAnalyzer {
  private final Match match;
  private final Game game;
  private final Side side;
  private final Team team;
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
      final Map<TeamBase, Long> teams = team.getParticipants().stream().map(p -> PlayerFactory.findPlayer(p.getSummoner().getPuuid()))
          .filter(Objects::nonNull)
          .map(Player::getTeam)
          .filter(Objects::nonNull)
          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
      final Map.Entry<TeamBase, Long> maxEntry = Collections.max(teams.entrySet(), Map.Entry.comparingByValue());
      if (maxEntry.getValue() > 2) {
        final TeamBase prmTeam = maxEntry.getKey();
        if (prmTeam.getOrgaTeam() != null) game.setOrgaGame(true);
        teamPerformance.setTeam(prmTeam);
      }
    }
  }

  public void analyzeSelections() {
    final Team team = side.getTeam(match);
    handleSelectionType(team.getBanned(), Selection.SelectionType.BAN);
    handleSelectionType(team.getPicks(), Selection.SelectionType.PICK);
  }

  private void handleSelectionType(List<Champion> champs, Selection.SelectionType type) {
    IntStream.range(0, champs.size()).forEach(i -> new Selection(game, side, type, (byte) (i + 1), champs.get(i)).create());
  }
}
