package de.zahrie.trues.api.riot.analyze;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.merakianalytics.orianna.types.core.match.Match;
import com.merakianalytics.orianna.types.core.match.Participant;
import com.merakianalytics.orianna.types.core.match.Team;
import de.zahrie.trues.api.coverage.player.PlayerFactory;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.api.riot.matchhistory.champion.Champion;
import de.zahrie.trues.api.riot.matchhistory.game.Game;
import de.zahrie.trues.api.riot.matchhistory.performance.ParticipantExtension;
import de.zahrie.trues.api.riot.matchhistory.selection.Selection;
import de.zahrie.trues.api.riot.matchhistory.selection.SelectionType;
import de.zahrie.trues.api.riot.matchhistory.teamperformance.TeamExtension;
import de.zahrie.trues.api.riot.matchhistory.teamperformance.TeamPerf;
import de.zahrie.trues.api.riot.matchhistory.teamperformance.TeamPerfFactory;
import de.zahrie.trues.database.Database;
import lombok.Data;
import lombok.experimental.ExtensionMethod;

@Data
@ExtensionMethod({TeamExtension.class, ParticipantExtension.class})
public class MatchSideAnalyzer {
  private final Match match;
  private final Game game;
  private final boolean alreadyInserted;
  private final boolean blueSide;
  private final Team team;
  private final List<Participant> validParticipants;

  public MatchSideAnalyzer(Match match, Game game, boolean alreadyInserted, boolean blueSide) {
    this.match = match;
    this.game = game;
    this.alreadyInserted = alreadyInserted;
    this.blueSide = blueSide;
    this.team = blueSide ? match.getBlueTeam() : match.getRedTeam();
    this.validParticipants = new TeamParticipantsAnalyzer(match, team).analyze();
  }

  public TeamPerf analyze() {
    TeamPerf teamPerformance = TeamPerfFactory.getTeamPerfBySide(game, blueSide);
    if (teamPerformance == null) {
      final int kills = team.getParticipants().stream().map(part -> part.getKDA().getKills()).mapToInt(Short::shortValue).sum();
      final int deaths = team.getParticipants().stream().map(part -> part.getKDA().getDeaths()).mapToInt(Short::shortValue).sum();
      final int assists = team.getParticipants().stream().map(part -> part.getKDA().getAssists()).mapToInt(Short::shortValue).sum();
      final int gold = team.getParticipants().stream().map(part -> part.getStats().getGoldEarned()).mapToInt(Integer::intValue).sum();
      final int damage = team.getParticipants().stream().map(part -> part.getStats().getDamageDealt()).mapToInt(Integer::intValue).sum();
      final int vision = team.getParticipants().stream().map(part -> part.getStats().getVisionScore()).mapToInt(Integer::intValue).sum();
      final int creeps = team.getParticipants().stream().map(part -> part.getStats().getCreepScore()).mapToInt(Integer::intValue).sum();
      teamPerformance = new TeamPerf(game, blueSide, team.isWinner(), kills, deaths, assists, gold, damage, vision, creeps, (short) team.getTowerKills(), (short) team.getDragonKills(), (short) team.getInhibitorKills(), (short) team.getRiftHeraldKills(), (short) team.getBaronKills());
    }
    handlePrmTeam(teamPerformance);
    final TeamPerf finalTeamPerformance = teamPerformance;
    validParticipants.stream().map(participant -> new ParticipantsAnalyzer(match, finalTeamPerformance, team, participant).analyze())
        .filter(Objects::nonNull).forEach(Database::save);
    return teamPerformance;
  }

  private void handlePrmTeam(TeamPerf teamPerformance) {
    if (teamPerformance.getPrmTeam() == null) {
      final Map<PrimeTeam, Long> teams = team.getParticipants().stream().map(p -> PlayerFactory.findPlayer(p.getSummoner().getPuuid()))
          .filter(Objects::nonNull)
          .map(Player::getTeam)
          .filter(Objects::nonNull)
          .map(t -> (PrimeTeam) t)
          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
      final Map.Entry<PrimeTeam, Long> maxEntry = Collections.max(teams.entrySet(), Map.Entry.comparingByValue());
      if (maxEntry.getValue() > 2) {
        final PrimeTeam prmTeam = maxEntry.getKey();
        if (prmTeam.getOrgaTeam() != null) {
          game.setOrgagame(true);
        }
        teamPerformance.setPrmTeam(prmTeam);
      }
    }
  }

  public void analyzeSelections() {
    final Team team = blueSide ? match.getBlueTeam() : match.getRedTeam();
    handleSelectionType(game, blueSide, team.getBanned(), SelectionType.BAN);
    handleSelectionType(game, blueSide, team.getPicks(), SelectionType.PICK);
  }

  private void handleSelectionType(Game game, boolean first, List<Champion> collection, SelectionType type) {
    for (int i = 0; i < collection.size(); i++) {
      final Champion champion = collection.get(i);
      final Selection selection = new Selection(game, first, (byte) (i + 1), type, champion);
      Database.save(selection);
    }
  }
}
