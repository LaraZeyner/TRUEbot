package de.zahrie.trues.api.coverage.match.log;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Created by Lara on 17.02.2023 for TRUEbot
 */
@Getter
@RequiredArgsConstructor
@ToString
public enum MatchLogAction {
  CHANGE_SCORE(null, false, "Ergebnis geändert"),
  CHANGE_STATUS(EventStatus.LINEUP_SUBMIT, true, "Status geändert"),
  CHANGE_TIME(EventStatus.SCHEDULING_SUGGEST, false, "Matchzeit geändert"),
  CREATE(EventStatus.CREATED, false, "Spiel erstellt"),
  DISQUALIFY(EventStatus.PLAYED, false, "Team disqualifiziert"),
  HOSTING_REQUEST(EventStatus.HOSTING_REQUEST, false, null),
  HOSTING_SUBMIT(EventStatus.HOSTING_REQUEST, false, null),
  LINEUP_FAIL(EventStatus.PLAYED, false, "Lineup fehlerhaft"),
  LINEUP_MISSING(EventStatus.PLAYED, false, null),
  LINEUP_NOTREADY(EventStatus.PLAYED, false, "Lineup fehlerhaft"),
  LINEUP_PLAYER_READY(null, false, null),
  LINEUP_SUBMIT(EventStatus.LINEUP_SUBMIT, false, "Lineup eingetragen"),
  PLAYED(EventStatus.PLAYED, false, "Spiel beendet"),
  REPORT(EventStatus.SCORE_REPORT, false, "Ergebnis gemeldet"),
  SCHEDULING_AUTOCONFIRM(EventStatus.SCHEDULING_CONFIRM, false, "Termin bestätigt"),
  SCHEDULING_CONFIRM(EventStatus.SCHEDULING_CONFIRM, false, "Termin bestätigt"),
  SCHEDULING_EXPIRED(EventStatus.CREATED, false, "Vorschlag ausgelaufen"),
  SCHEDULING_SUGGEST(EventStatus.SCHEDULING_SUGGEST, false, "neuer Vorschlag"),
  SCORE_REPORT(EventStatus.SCORE_REPORT, false, "Ergebnis gemeldet"),
  TEAM_ADDED(null, false, null);

  private final EventStatus status;
  private final boolean force;
  private final String output;
}
