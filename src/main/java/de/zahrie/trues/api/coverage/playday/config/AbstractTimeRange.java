package de.zahrie.trues.api.coverage.playday.config;

import de.zahrie.trues.api.datatypes.calendar.Time;

/**
 * Created by Lara on 28.02.2023 for TRUEbot
 */
public interface AbstractTimeRange {
  Time start();
  Time end();
}
