package de.zahrie.trues.api.coverage.match.model;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

import de.zahrie.trues.api.coverage.league.model.LeagueImpl;
import de.zahrie.trues.api.coverage.match.MatchResult;
import de.zahrie.trues.api.coverage.match.log.EventStatus;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.playday.config.SchedulingRange;
import de.zahrie.trues.api.database.connector.SQLUtils;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import lombok.experimental.ExtensionMethod;

@Table(value = "coverage", department = "intern")
@ExtensionMethod(SQLUtils.class)
public class OrgaMatch extends LeagueMatch implements Entity<OrgaMatch> {
  @Serial
  private static final long serialVersionUID = 825420772280445656L;

  public OrgaMatch(Playday matchday, LocalDateTime start, LeagueImpl league, SchedulingRange schedulingRange, Integer matchId) {
    this(matchday, MatchFormat.TWO_GAMES, start, (short) 0, EventStatus.CREATED, "keine Infos", true, MatchResult.ZERO.toString(), league, league.getMatches().size() + 1, matchId, schedulingRange);
  }

  public OrgaMatch(Playday playday, MatchFormat format, LocalDateTime start, short rateOffset, EventStatus status, String lastMessage, boolean active, String result, LeagueImpl league, int matchIndex, Integer matchId, TimeRange timeRange) {
    super(playday, format, start, rateOffset, status, lastMessage, active, result, league, matchIndex, matchId, timeRange);
  }

  private OrgaMatch(int id, Playday playday, MatchFormat format, LocalDateTime start, short rateOffset, EventStatus status, String lastMessage, boolean active, String result, LeagueImpl league, int matchIndex, Integer matchId, TimeRange timeRange) {
    super(playday, format, start, rateOffset, status, lastMessage, active, result, league, matchIndex, matchId, timeRange);
    this.id = id;
  }

  public static OrgaMatch get(List<Object> objects) {
    final MatchFormat format = new SQLEnum<>(MatchFormat.class).of(objects.get(3));
    final int id = (int) objects.get(0);
    return new OrgaMatch(
        id,
        new Query<>(Playday.class).entity(objects.get(2)),
        format,
        (LocalDateTime) objects.get(4),
        objects.get(5).shortValue(),
        new SQLEnum<>(EventStatus.class).of(objects.get(6)),
        (String) objects.get(7),
        (boolean) objects.get(8),
        (String) objects.get(9),
        new Query<>(LeagueImpl.class).entity(objects.get(10)),
        (int) objects.get(11),
        (Integer) objects.get(12),
        new TimeRange((LocalDateTime) objects.get(13), (LocalDateTime) objects.get(14))
    );
  }

  @Override
  public OrgaMatch create() {
    return new Query<>(OrgaMatch.class)
        .col("matchday", playday).col("coverage_format", format).col("coverage_start", start).col("rate_offset", rateOffset)
        .col("status", status).col("last_message", lastMessage).col("active", active).col("result", result).col("coverage_group", league)
        .col("coverage_index", matchIndex).col("match_id", matchId).col("scheduling_start", range.getStartTime())
        .col("scheduling_end", range.getEndTime())
        .insert(this, match -> league.getMatches().add(match));
  }

  @Override
  public String getTypeString() {
    return "TRUE-Cup";
  }
}
