package de.zahrie.trues.api.coverage.playday.config;

import de.zahrie.trues.api.datatypes.calendar.Time;

public interface AbstractTimeRange {
  Time start();
  Time end();
}
