package de.zahrie.trues.api.gamesports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.zahrie.trues.models.coverage.match.MatchFactory;
import de.zahrie.trues.models.coverage.match.PrimeMatch;
import de.zahrie.trues.util.Loader;
import de.zahrie.trues.util.io.request.HTML;
import de.zahrie.trues.util.io.request.URLType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Lara on 14.02.2023 for TRUEbot
 */
@Getter
public class MatchLoader extends GamesportsLoader implements Loader {
  private final PrimeMatch match;

  public MatchLoader(@NotNull PrimeMatch match) {
    super(URLType.MATCH, match.getId());
    this.match = match;
  }

  public MatchLoader(int matchId) {
    super(URLType.MATCH, matchId);
    this.match = MatchFactory.getEvent(matchId);
  }

  public void load() {
    final String result = html
        .findByClass("span", "league-match-result")
        .text();

    final String matchTimeEpoch = html.findById("div", "league-match-time").getAttribute("data-time");
    final Date matchTime = new Date(Long.parseLong(matchTimeEpoch) * 1000);
    final List<Integer> teamIds = html.findAllByClass("div", "content-match-head-team-top").stream()
        .map(team -> team.getAttribute("href"))
        .map(TeamLoader::idFromURL)
        .toList();

    final List<String> logEntries = new ArrayList<>();
    final List<HTML> findAllByClass = html.findAllByClass("td", null);
    for (int i = 1; i < findAllByClass.size(); i++) {
      final HTML data = findAllByClass.get(i);
      final HTML span = data.findByClass("span", "table-cell-container");
      final String s = (i - 1) % 4 == 0 ? span.getAttribute("data-time") : span.text();
      logEntries.add(s);
    }
  }
}
