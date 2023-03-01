package de.zahrie.trues.api.coverage.playday.config;

import de.zahrie.trues.util.util.Clock;

/**
 * Created by Lara on 27.02.2023 for TRUEbot
 */
public record RelativeTimeRange(TimeOffset start, TimeOffset end) {

  public RelativeTimeRange(int day, Clock start, int minutes) {
    this(new TimeOffset(day, start), new TimeOffset(day, start.add(minutes)));
  }

  public RelativeTimeRange(int day, Clock start, Clock end) {
    this(new TimeOffset(day, start), new TimeOffset(day, end));
  }

  public RelativeTimeRange(int day, Clock start, int duration, Clock end) {
    this(new TimeOffset(day, start), new TimeOffset(duration, end));
  }

}
