package de.zahrie.trues.api.coverage.match.log;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum EventStatus {
  question(0),
  created(1),
  scheduling_suggest(2),
  scheduling_confirm(3),
  lineup_submit(4),
  hosting_request(5),
  score_report(6),
  played(7);

  private final int order;
}
