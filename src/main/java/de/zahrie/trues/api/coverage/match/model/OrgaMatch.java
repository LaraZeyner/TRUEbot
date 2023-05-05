package de.zahrie.trues.api.coverage.match.model;

import java.io.Serial;
import java.time.LocalDateTime;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.match.MatchResult;
import de.zahrie.trues.api.coverage.match.log.EventStatus;
import de.zahrie.trues.api.coverage.match.log.MatchLog;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.playday.config.SchedulingRange;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;

@Table(value = "coverage", department = "intern")
public class OrgaMatch extends LeagueMatch implements Entity<OrgaMatch> {
  @Serial
  private static final long serialVersionUID = 825420772280445656L;

  public OrgaMatch(Playday matchday, LocalDateTime start, League league, SchedulingRange schedulingRange, Integer matchId) {
    this(matchday, MatchFormat.TWO_GAMES, start, (short) 0, EventStatus.CREATED, "keine Infos", true, MatchResult.ZERO, league, league.getMatches().size() + 1, matchId, schedulingRange);
  }

  public OrgaMatch(Playday playday, MatchFormat format, LocalDateTime start, short rateOffset, EventStatus status, String lastMessage, boolean active, MatchResult result, League league, int matchIndex, Integer matchId, TimeRange timeRange) {
    super(playday, format, start, rateOffset, status, lastMessage, active, result, league, matchIndex, matchId, timeRange);
  }

  private OrgaMatch(int id, Playday playday, MatchFormat format, LocalDateTime start, short rateOffset, EventStatus status, String lastMessage, boolean active, MatchResult result, League league, int matchIndex, Integer matchId, TimeRange timeRange) {
    super(playday, format, start, rateOffset, status, lastMessage, active, result, league, matchIndex, matchId, timeRange);
    this.id = id;
  }

  public static OrgaMatch get(Object[] objects) {
    final var match = new OrgaMatch(
        (int) objects[0],
        new Query<Playday>().entity(objects[1]),
        new SQLEnum<MatchFormat>().of(objects[2]),
        (LocalDateTime) objects[3],
        (short) objects[4],
        new SQLEnum<EventStatus>().of(objects[5]),
        (String) objects[6],
        (boolean) objects[7],
        MatchResult.fromResultString((String) objects[8], new SQLEnum<MatchFormat>().of(objects[2])),
        new Query<League>().entity(objects[9]),
        (int) objects[10],
        (Integer) objects[11],
        new TimeRange((LocalDateTime) objects[12], (LocalDateTime) objects[13])
    );
    match.participators[0] = new Query<Participator>().where("coverage", match).and("first", true).entity();
    match.participators[1] = new Query<Participator>().where("coverage", match).and("first", false).entity();
    match.getLogs().addAll(new Query<MatchLog>().where("coverage", match).entityList());
    return match;
  }

  @Override
  public OrgaMatch create() {
    return new Query<OrgaMatch>()
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
