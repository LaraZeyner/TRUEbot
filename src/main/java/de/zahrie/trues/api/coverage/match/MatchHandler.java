package de.zahrie.trues.api.coverage.match;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import de.zahrie.trues.api.coverage.lineup.LineupManager;
import de.zahrie.trues.api.coverage.match.log.EventStatus;
import de.zahrie.trues.api.coverage.match.log.MatchLog;
import de.zahrie.trues.api.coverage.match.log.MatchLogAction;
import de.zahrie.trues.api.coverage.match.model.PrimeMatch;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.datatypes.symbol.Chain;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.util.io.request.HTML;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by Lara on 15.02.2023 for TRUEbot
 */
@Getter
public class MatchHandler extends MatchModel implements Serializable {
  @Serial
  private static final long serialVersionUID = -2773996422897802404L;

  @Builder
  public MatchHandler(HTML html, String url, PrimeMatch match, List<HTML> logs, List<PrimeTeam> teams) {
    //TODO (Abgie) 01.03.2023: never used
    super(html, url, match, logs, teams);
  }

  public void update() {
    updateResult();
    updateMatchtime();
    updateTeams();
    if (this.teams.stream().anyMatch(team -> team.getOrgaTeam() != null)) {
      final boolean updated = updateLogs();
      if (updated) {
        match.setStatus(determineStatus());
      }
    }
    if (!match.getStatus().equals(EventStatus.played)) {
      LineupManager.getMatch(match).update();
    }
    Database.save(match);
  }

  private void updateMatchtime() {
    final Integer epoch = html.find("span", "tztime").getAttribute("data-time").intValue();
    if (epoch != null) {
      final Time time = Time.fromEpoch(epoch);
      match.setStart(time);
    }
  }

  public void updateResult() {
    final Chain result = html.find("span", "league-match-result").text();
    if (result != null && !result.isEmpty()) {
      match.setResult(result.toString());
    }
  }

  public void updateTeams() {
    final List<Participator> participators = new LinkedList<>(Arrays.asList(match.getHome(), match.getGuest()));
    for (int i = 0; i < teams.size(); i++) {
      final boolean isHome = i == 0;
      final PrimeTeam selected = teams.get(i);
      if (participators.get(i) == null) {
        participators.set(i, new Participator(isHome, selected));
      }
    }
    match.addParticipators(participators.get(0), participators.get(1));
  }

  private boolean updateLogs() {
    boolean updated = false;
    Collections.reverse(logs);
    for (final HTML html : logs) {
      final List<HTML> cells = html.findAll("td");
      final Time timestamp = determineTimestamp(cells.get(0));
      final Chain userWithTeam = cells.get(1).text();
      final var action = MatchLogAction.valueOf(cells.get(2).text().toString());
      final String details = cells.get(3).text().toString();
      updated = match.get().updateLogs(timestamp, userWithTeam, action, details) || updated;
    }
    return updated;
  }

  private EventStatus determineStatus() {
    if (!match.getResult().equals("-:-")) {
      return EventStatus.played;
    }
    EventStatus status = EventStatus.created;
    boolean expired = false;
    for (final MatchLog log : match.getLogs().stream().sorted(Comparator.comparing(MatchLog::getTimestamp).reversed()).toList()) {
      final EventStatus eventStatus = log.getAction().getStatus();
      if (eventStatus == null) {
        continue;
      }
      if (log.getAction().equals(MatchLogAction.scheduling_expired)) {
        expired = true;
      }
      if ((eventStatus.getOrder() > status.getOrder() || log.getAction().isForce()) &&
          (!expired || !status.equals(EventStatus.scheduling_suggest))) {
        status = eventStatus;
      }

    }
    return status;
  }

  private Time determineTimestamp(HTML html) {
    final Integer stamp = html.find("span", "itime ").getAttribute("data-time").intValue();
    if (stamp != null) {
      return Time.fromEpoch(stamp);
    }
    return null;
  }

}
