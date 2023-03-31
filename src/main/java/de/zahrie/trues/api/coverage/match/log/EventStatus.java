package de.zahrie.trues.api.coverage.match.log;

import lombok.ToString;

@ToString
public enum EventStatus {
  QUESTION,
  CREATED,
  SCHEDULING_SUGGEST,
  SCHEDULING_CONFIRM,
  LINEUP_SUBMIT,
  HOSTING_REQUEST,
  SCORE_REPORT,
  PLAYED
}
