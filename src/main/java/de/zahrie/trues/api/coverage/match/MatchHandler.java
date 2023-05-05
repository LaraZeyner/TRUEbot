package de.zahrie.trues.api.coverage.match;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import de.zahrie.trues.api.coverage.lineup.LineupManager;
import de.zahrie.trues.api.coverage.match.log.EventStatus;
import de.zahrie.trues.api.coverage.match.log.MatchLog;
import de.zahrie.trues.api.coverage.match.log.MatchLogAction;
import de.zahrie.trues.api.coverage.match.model.PRMMatch;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.api.datatypes.calendar.DateTimeUtils;
import de.zahrie.trues.api.datatypes.collections.SortedList;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.io.request.HTML;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import lombok.extern.java.Log;

@Getter
@Log
@ExtensionMethod(StringUtils.class)
public class MatchHandler extends MatchModel implements Serializable {
  @Serial
  private static final long serialVersionUID = -2773996422897802404L;

  @Builder
  @SuppressWarnings("unused")
  public MatchHandler(HTML html, String url, PRMMatch match, List<HTML> logs, List<PRMTeam> teams) {
    super(html, url, match, logs, teams);
  }

  public void update() {
    updateResult();
    updateMatchtime();
    updateTeams();
    if (this.teams.stream().anyMatch(team -> team.getOrgaTeam() != null)) {
      final boolean updated = updateLogs();
      if (updated) {
        match.updateStatus(determineStatus());
      }
    }
    if (!match.getStatus().equals(EventStatus.PLAYED)) {
      LineupManager.getMatch(match).update();
    }
    match.update();
  }

  private void updateMatchtime() {
    final int epochSeconds = html.find("span", "tztime").getAttribute("data-time").intValue();
    final LocalDateTime dateTime = DateTimeUtils.fromEpoch(epochSeconds);
    match.updateStart(dateTime);
  }

  public void updateResult() {
    final String result = html.find("span", "league-match-result").text();
    if (result != null && !result.isEmpty()) {
      match.updateResult(result);
    }
  }

  public void updateTeams() {
    for (int i = 0; i < Math.min(2, teams.size()); i++) {
      final PRMTeam selected = teams.get(i);
      match.addParticipator(selected, i == 0);
    }
  }

  private boolean updateLogs() {
    boolean updated = false;
    Collections.reverse(logs);
    for (HTML html : logs) {
      final List<HTML> cells = html.findAll("td");
      final int epochSeconds = html.find("span", "itime ").getAttribute("data-time").intValue();
      final LocalDateTime dateTime = DateTimeUtils.fromEpoch(epochSeconds);
      final String userWithTeam = cells.get(1).text();
      final var action = MatchLogAction.valueOf(cells.get(2).text().upper());
      final String details = cells.get(3).text();
      updated = match.get().updateLogs(dateTime, userWithTeam, action, details) || updated;
    }
    return updated;
  }

  private EventStatus determineStatus() {
    if (!match.isRunning()) return EventStatus.PLAYED;
    EventStatus status = EventStatus.CREATED;
    boolean expired = false;
    for (MatchLog log : new SortedList<>(match.getLogs()).reverse()) {
      final EventStatus eventStatus = log.getAction().getStatus();
      if (eventStatus == null) continue;

      if (log.getAction().equals(MatchLogAction.SCHEDULING_EXPIRED)) expired = true;
      if ((eventStatus.ordinal() > status.ordinal() || log.getAction().isForce()) &&
          !(expired && status.equals(EventStatus.SCHEDULING_SUGGEST))) status = eventStatus;
    }
    return status;
  }
}
