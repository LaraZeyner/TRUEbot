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
  change_score(null, false),
  change_status(EventStatus.lineup_submit, true),
  change_time(EventStatus.scheduling_suggest, false),
  create(EventStatus.created, false),
  disqualify(EventStatus.played, false),
  hosting_request(EventStatus.hosting_request, false),
  hosting_submit(EventStatus.hosting_request, false),
  lineup_fail(EventStatus.played, false),
  lineup_missing(EventStatus.played, false),
  lineup_notready(EventStatus.played, false),
  lineup_player_ready(null, false),
  lineup_submit(EventStatus.lineup_submit, false),
  played(EventStatus.played, false),
  report(EventStatus.score_report, false),
  scheduling_autoconfirm(EventStatus.scheduling_confirm, false),
  scheduling_confirm(EventStatus.scheduling_confirm, false),
  scheduling_suggest(EventStatus.scheduling_suggest, false),
  scheduling_expired(EventStatus.created, false),
  score_report(EventStatus.score_report, false),
  team_added(null, false);

  private final EventStatus status;
  private final boolean force;

}
