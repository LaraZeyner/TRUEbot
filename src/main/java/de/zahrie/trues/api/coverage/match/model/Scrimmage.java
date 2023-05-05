package de.zahrie.trues.api.coverage.match.model;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Arrays;

import de.zahrie.trues.api.coverage.match.MatchResult;
import de.zahrie.trues.api.coverage.match.log.EventStatus;
import de.zahrie.trues.api.coverage.match.log.MatchLog;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.playday.PlaydayFactory;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;

@Table(value = "coverage", department = "scrimmage")
public class Scrimmage extends Match implements Entity<Scrimmage> {
  @Serial
  private static final long serialVersionUID = -6736012840442317674L;

  public Scrimmage(LocalDateTime start) {
    this(PlaydayFactory.current(), MatchFormat.TWO_GAMES, start, (short) 0, EventStatus.CREATED, "keine Infos", true, MatchResult.ZERO);
  }

  public Scrimmage(Playday playday, MatchFormat format, LocalDateTime start, short rateOffset, EventStatus status, String lastMessage, boolean active, MatchResult result) {
    super(playday, format, start, rateOffset, status, lastMessage, active, result);
  }

  private Scrimmage(int id, Playday playday, MatchFormat format, LocalDateTime start, short rateOffset, EventStatus status, String lastMessage, boolean active, MatchResult result) {
    super(playday, format, start, rateOffset, status, lastMessage, active, result);
    this.id = id;
  }

  public static Scrimmage get(Object[] objects) {
    final var match = new Scrimmage(
        (int) objects[0],
        new Query<Playday>().entity(objects[1]),
        new SQLEnum<MatchFormat>().of(objects[2]),
        (LocalDateTime) objects[3],
        (short) objects[4],
        new SQLEnum<EventStatus>().of(objects[5]),
        (String) objects[6],
        (boolean) objects[7],
        MatchResult.fromResultString((String) objects[8], new SQLEnum<MatchFormat>().of(objects[2]))
    );
    match.participators[0] = new Query<Participator>().where("coverage", match).and("first", true).entity();
    match.participators[1] = new Query<Participator>().where("coverage", match).and("first", false).entity();
    match.getLogs().addAll(new Query<MatchLog>().where("coverage", match).entityList());
    return match;
  }

  @Override
  public Scrimmage create() {
    return new Query<Scrimmage>()
        .col("matchday", playday).col("coverage_format", format).col("coverage_start", start).col("rate_offset", rateOffset).col("status", status).col("last_message", lastMessage).col("active", active).col("result", result)
        .insert(this, match -> {
          match.getLogs().addAll(determineLog());
          Arrays.stream(participators).forEach(Participator::create);
          return true;
        });
  }

  @Override
  public String getTypeString() {
    return "Scrimmage";
  }

  public String display() {
    return getId() + " | " + TimeFormat.DEFAULT.of(getStart()) + " | " + getHomeName() + " vs. " + getGuestName();
  }
}
