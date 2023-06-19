package de.zahrie.trues.api.coverage.match.model;

import java.time.LocalDateTime;
import java.util.Objects;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.match.log.EventStatus;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import lombok.Getter;

@Getter
@Table("coverage")
public abstract class LeagueMatch extends Match implements AScheduleable, ATournament {
  protected final League league;
  protected final int matchIndex;
  protected final int matchId;
  protected TimeRange range;

  public LeagueMatch(Playday playday, MatchFormat format, LocalDateTime start, short rateOffset, EventStatus status, String lastMessage, boolean active, String result, League league, int matchIndex, Integer matchId, TimeRange timeRange) {
    super(playday, format, start, rateOffset, status, lastMessage, active, result);
    this.league = league;
    this.matchIndex = matchIndex;
    this.matchId = matchId;
    this.range = timeRange;
  }

  @Override
  public void setRange(TimeRange timeRange) {
    if (getRange().getStartTime() != range.getStartTime() || getRange().getEndTime() != timeRange.getEndTime()) {
      new Query<>(LeagueMatch.class).col("scheduling_start", timeRange.getStartTime()).col("scheduling_end", timeRange.getEndTime()).update(id);
    }
    this.range = timeRange;
  }

  @Override
  public String toString() {
    return league.getName() + " - " + getHomeAbbr() + " vs. " + getGuestAbbr();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof final LeagueMatch that)) return false;
    if (!super.equals(o)) return false;
    return getMatchId() == that.getMatchId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getMatchId());
  }
}
