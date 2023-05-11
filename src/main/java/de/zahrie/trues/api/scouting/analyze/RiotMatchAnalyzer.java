package de.zahrie.trues.api.scouting.analyze;

import java.util.Arrays;

import com.merakianalytics.orianna.types.common.Map;
import com.merakianalytics.orianna.types.core.match.Match;
import de.zahrie.trues.api.riot.Side;
import de.zahrie.trues.api.riot.game.Game;
import de.zahrie.trues.api.riot.game.GameType;
import de.zahrie.trues.api.riot.game.MatchUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;

@Data
@RequiredArgsConstructor
@ExtensionMethod(MatchUtils.class)
public class RiotMatchAnalyzer {
  private final Match match;
  private Game game;
  private boolean requiresSelection;

  public Game analyze() {
    if (match.getParticipants().size() != 10 || !match.getMap().equals(Map.SUMMONERS_RIFT)) return null;

    this.game = createGame();
    final MatchSideAnalyzer blueSide = new MatchSideAnalyzer(match, game, Side.BLUE);
    final MatchSideAnalyzer redSide = new MatchSideAnalyzer(match, game, Side.RED);
    this.requiresSelection = requiresSelection(blueSide, redSide);
    handleSide(blueSide);
    handleSide(redSide);
    return game;
  }

  private boolean requiresSelection(MatchSideAnalyzer... sides) {
    final GameType queue = match.getGameQueue();
    if (queue.equals(GameType.CUSTOM) || queue.equals(GameType.TOURNAMENT)) return true;

    final int max = Arrays.stream(sides).map(side -> side.getValidParticipants().size()).max(Integer::compareTo).orElse(0);
    return max > 2 || (queue.equals(GameType.CLASH) && max > 0);
  }

  private void handleSide(MatchSideAnalyzer analyzer) {
    if (requiresSelection && !game.hasSelections()) analyzer.analyzeSelections();
    if (analyzer.getValidParticipants().isEmpty()) return;

    analyzer.analyze();
  }

  private Game createGame() {
    final int durationInSeconds = (int) Math.round(match.getDuration().getMillis() / 1000.);
    return new Game(match.getMatchId(), match.getCreation(), durationInSeconds, match.getGameQueue()).create();
  }
}
