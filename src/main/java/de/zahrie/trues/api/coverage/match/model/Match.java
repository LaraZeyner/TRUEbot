package de.zahrie.trues.api.coverage.match.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import de.zahrie.trues.api.coverage.match.MatchResult;
import de.zahrie.trues.api.coverage.match.log.EventStatus;
import de.zahrie.trues.api.coverage.match.log.MatchLog;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Id;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.collections.SortedList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@RequiredArgsConstructor
@Table("coverage")
public abstract class Match implements AMatch, Comparable<Match>, Id {
  protected int id;
  protected final Playday playday;
  protected final MatchFormat format;
  protected LocalDateTime start;
  protected final short rateOffset;
  protected EventStatus status;
  protected String lastMessage;
  protected final boolean active;
  protected MatchResult result;
  protected final Participator[] participators = IntStream.range(0, 2).mapToObj(i -> new Participator(this, i == 0))
      .toArray(Participator[]::new);
  protected final List<MatchLog> logs = new SortedList<>();

  public Match(Playday playday, MatchFormat format, LocalDateTime start, short rateOffset, EventStatus status, String lastMessage, boolean active, MatchResult result) {
    this.playday = playday;
    this.format = format;
    this.start = start;
    this.rateOffset = rateOffset;
    this.status = status;
    this.lastMessage = lastMessage;
    this.active = active;
    this.result = result;
  }

  @Override
  public void setStart(LocalDateTime start) {
    if (this.start != start) new Query<Match>().col("coverage_start", start).update(id);
    this.start = start;
  }

  public void updateStart(LocalDateTime start) {
    if (getStart().equals(start)) return;
    setStart(start);
    new Query<Match>().col("coverage_start", start).update(id);
    handleNotifications();
  }

  public void updateStatus(EventStatus status) {
    setStatus(status);
    new Query<Match>().col("status", status).update(id);
  }

  public void updateLastMessage(String lastMessage) {
    setLastMessage(lastMessage);
    new Query<Match>().col("last_message", lastMessage).update(id);
  }

  public void updateResult(MatchResult result) {
    setResult(result);
    getHome().setWins((short) result.getHomeScore());
    getGuest().setWins((short) result.getGuestScore());
    setResult(result);
    new Query<Match>().col("result", result).update(id);
  }

  public void updateResult(String result) {
    updateResult(MatchResult.fromResultString(result, getFormat()));
  }

  @Override
  public void setStatus(EventStatus status) {
    if (this.status != status) new Query<Match>().col("status", status).update(id);
    this.status = status;
  }

  @Override
  public void setLastMessage(String lastMessage) {
    if (!this.lastMessage.equals(lastMessage)) new Query<Match>().col("last_message", lastMessage).update(id);
    this.lastMessage = lastMessage;
  }

  @Override
  public void setResult(MatchResult result) {
    if (this.result != result) new Query<Match>().col("result", result).update(id);
    this.result = result;
  }

  public String getExpectedResult() {
    return getResult().expectResultOf(this) + (isRunning() ? "*" : "");
  }

  @Override
  public int compareTo(@NotNull Match o) {
    return getStart().compareTo(o.getStart());
  }

  @Override
  public String toString() {
    return this instanceof LeagueMatch leagueMatch ? leagueMatch.toString() : "Scrim: " + getHomeAbbr() + " vs. " + getGuestAbbr();
  }
}
