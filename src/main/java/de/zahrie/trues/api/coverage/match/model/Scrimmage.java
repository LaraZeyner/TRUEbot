package de.zahrie.trues.api.coverage.match.model;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import de.zahrie.trues.api.coverage.match.MatchResult;
import de.zahrie.trues.api.coverage.match.log.EventStatus;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.playday.PlaydayFactory;
import de.zahrie.trues.api.database.connector.SQLUtils;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import lombok.experimental.ExtensionMethod;

@Table(value = "coverage", department = "scrimmage")
@ExtensionMethod(SQLUtils.class)
public class Scrimmage extends Match implements Entity<Scrimmage> {
  @Serial
  private static final long serialVersionUID = -6736012840442317674L;

  public Scrimmage(LocalDateTime start) {
    this(PlaydayFactory.current(), MatchFormat.TWO_GAMES, start, (short) 0, EventStatus.CREATED, "keine Infos", true, MatchResult.ZERO.toString());
  }

  public Scrimmage(Playday playday, MatchFormat format, LocalDateTime start, short rateOffset, EventStatus status, String lastMessage, boolean active, String result) {
    super(playday, format, start, rateOffset, status, lastMessage, active, result);
  }

  private Scrimmage(int id, Playday playday, MatchFormat format, LocalDateTime start, short rateOffset, EventStatus status, String lastMessage, boolean active, String result) {
    super(playday, format, start, rateOffset, status, lastMessage, active, result);
    this.id = id;
  }

  public static Scrimmage get(List<Object> objects) {
    final MatchFormat format = new SQLEnum<>(MatchFormat.class).of(objects.get(3));
    final int id = (int) objects.get(0);
    return new Scrimmage(
        id,
        new Query<>(Playday.class).entity(objects.get(2)),
        format,
        (LocalDateTime) objects.get(4),
        objects.get(5).shortValue(),
        new SQLEnum<>(EventStatus.class).of(objects.get(6)),
        (String) objects.get(7),
        (boolean) objects.get(8),
        (String) objects.get(9)
    );
  }

  @Override
  public Scrimmage create() {
    return new Query<>(Scrimmage.class)
        .col("matchday", playday).col("coverage_format", format).col("coverage_start", start).col("rate_offset", rateOffset)
        .col("status", status).col("last_message", lastMessage).col("active", active).col("result", result)
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
