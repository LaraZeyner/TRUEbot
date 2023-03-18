package de.zahrie.trues.api.riot.analyze;

import java.util.Arrays;

import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.riot.matchhistory.game.Game;
import de.zahrie.trues.api.riot.matchhistory.game.GameType;
import de.zahrie.trues.api.riot.matchhistory.teamperformance.TeamPerf;
import de.zahrie.trues.api.riot.xayah.types.common.Map;
import de.zahrie.trues.api.riot.xayah.types.core.match.Match;
import de.zahrie.trues.database.Database;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MatchAnalyzer {
  private final Match match;
  private final boolean alreadyInserted;
  private Game game;
  private boolean requiresSelection;

  public MatchAnalyzer(Match match) {
    this(match, Database.Find.find(Game.class, match.getMatchId()) != null);
  }

  public Game analyze() {
    if (match.getParticipants().size() != 10 || !match.getMap().equals(Map.SUMMONERS_RIFT)) {
      return null;
    }

    this.game = createGame();
    final MatchSideAnalyzer blueSide = new MatchSideAnalyzer(match, game, alreadyInserted, true);
    final MatchSideAnalyzer redSide = new MatchSideAnalyzer(match, game, alreadyInserted, false);
    this.requiresSelection = requiresSelection(blueSide, redSide);
    handleSide(blueSide);
    handleSide(redSide);
    return game;
  }

  private boolean requiresSelection(MatchSideAnalyzer... sides) {
    final GameType queue = match.getGameQueue();
    if (queue.equals(GameType.CUSTOM) || queue.equals(GameType.TOURNAMENT)) {
      return true;
    }
    final int max = Arrays.stream(sides).map(side -> side.getValidParticipants().size()).max(Integer::compareTo).orElse(0);
    return max > 2 || (queue.equals(GameType.CLASH) && max > 0);
  }

  private void handleSide(MatchSideAnalyzer analyzer) {
    if (requiresSelection && !game.hasSelections()) {
      analyzer.analyzeSelections();
    }

    if (analyzer.getValidParticipants().isEmpty()) {
      return;
    }
    final TeamPerf side = analyzer.analyze();
    Database.save(side);
  }

  private Game createGame() {
    final Time creationTime = match.getCreationTime();
    final GameType gametype = match.getGameQueue();
    final int durationInSeconds = (int) Math.round(match.getDuration().getMillis() / 1000.);
    final String platform = match.getPlatform().getTag();
    final long id = match.getCoreData().getId();
    final String matchId = platform + "_" + id;
    return new Game(matchId, creationTime, durationInSeconds, gametype);
  }
}
