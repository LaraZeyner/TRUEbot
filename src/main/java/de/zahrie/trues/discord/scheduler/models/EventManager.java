package de.zahrie.trues.discord.scheduler.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import de.zahrie.trues.api.calendar.event.Round;
import de.zahrie.trues.api.calendar.event.RoundParticipator;
import de.zahrie.trues.api.database.query.Condition;
import de.zahrie.trues.api.database.query.JoinQuery;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.scheduler.Schedule;
import de.zahrie.trues.api.scheduler.ScheduledTask;

@Schedule
public class EventManager extends ScheduledTask {
  @Override
  public void execute() {
    final List<Round> rounds = new Query<>(RoundParticipator.class)
        .join(new JoinQuery<>(RoundParticipator.class, Round.class).col("event_round"))
        .where(Condition.isNull("team_index")).convertList(Round.class);
    rounds.stream().filter(round -> round.getTimestamp().toLocalDate().equals(LocalDate.now()))
        .filter(round -> !round.getStartTime().isBefore(LocalTime.now()))
        .forEach(Round::shuffle);
  }

  @Override
  protected String name() {
    return "EventManager";
  }
}
