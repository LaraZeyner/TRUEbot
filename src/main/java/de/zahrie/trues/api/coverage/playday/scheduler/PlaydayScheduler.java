package de.zahrie.trues.api.coverage.playday.scheduler;

import de.zahrie.trues.api.coverage.playday.config.PlaydayRange;
import de.zahrie.trues.api.coverage.playday.config.SchedulingRange;
import de.zahrie.trues.util.util.Time;

public record PlaydayScheduler(PlaydayRange playday, Time defaultTime, SchedulingRange scheduling) { }
