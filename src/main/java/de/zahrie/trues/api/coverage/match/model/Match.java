package de.zahrie.trues.api.coverage.match.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import de.zahrie.trues.api.calendar.Cast;
import de.zahrie.trues.api.calendar.MatchCalendar;
import de.zahrie.trues.api.community.betting.Bet;
import de.zahrie.trues.api.community.betting.BetFactory;
import de.zahrie.trues.api.coverage.match.MatchResult;
import de.zahrie.trues.api.coverage.match.log.EventStatus;
import de.zahrie.trues.api.coverage.match.log.MatchLog;
import de.zahrie.trues.api.coverage.match.log.MatchLogAction;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Id;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.riot.game.Game;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
@Table("coverage")
public abstract class Match implements AMatch, Comparable<Match>, Id {
  @Setter
  protected int id;
  protected final Playday playday;
  protected final MatchFormat format;
  protected LocalDateTime start;
  protected final short rateOffset;
  protected EventStatus status;
  protected String lastMessage;
  protected boolean active;
  protected String result;
  protected Participator[] participators;

  public Participator[] getParticipators() {
    if (participators == null) this.participators = new Query<>(Participator.class).where("coverage", this)
        .descending("first").entityList().toArray(Participator[]::new);
    return participators;
  }
  protected MatchResult expectedResult;
  protected List<MatchLog> logs;

  public List<MatchLog> getLogs() {
    if (logs == null) this.logs = determineLog();
    return logs;
  }

  public List<Game> getGames() {
    return new Query<>(Game.class).where("coverage", this).entityList();
  }

  public List<MatchLog> getLogs(MatchLogAction action) {
    if (logs == null) this.logs = determineLog();
    return logs.stream().filter(log -> log.getAction().equals(action)).toList();
  }

  public boolean addLog(@NonNull MatchLog matchLog) {
    if (matchLog.getId() == 0) this.logs = null;
    else return getLogs().add(matchLog);
    return false;
  }

  private Cast cast;

  public Cast getCast() {
    if (cast == null) this.cast = new Query<>(Cast.class).where("details", String.valueOf(id)).entity();
    return cast;
  }

  protected MatchResult matchResult;

  public MatchResult getResult() {
    if (matchResult == null) this.matchResult = MatchResult.fromResultString(result,this);
    return matchResult;
  }

  public MatchCalendar asEvent() {
    return new MatchCalendar(getExpectedTimeRange(), String.valueOf(id));
  }

  public Match(Playday playday, MatchFormat format, LocalDateTime start, short rateOffset, EventStatus status, String lastMessage, boolean active, String result) {
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
    if (getStart().equals(start)) return;
    if (this.start != start) new Query<>(Match.class).col("coverage_start", start).update(id);
    this.start = start;
    handleNotifications();
  }

  @Override
  public void setStatus(EventStatus status) {
    if (this.status != status) new Query<>(Match.class).col("status", status).update(id);
    this.status = status;
  }

  @Override
  public void setLastMessage(String lastMessage) {
    if (!this.lastMessage.equals(lastMessage)) new Query<>(Match.class).col("last_message", lastMessage).update(id);
    this.lastMessage = lastMessage;
  }

  /**
   * Für die Matchlogs
   */
  public void updateResult() {
    if (result != null) setResult(MatchResult.fromResultString(result, this));
  }

  /**
   * Für Result setzen
   * @param result ResultString
   */
  public void updateResult(@NonNull String result) {
    setResult(MatchResult.fromResultString(result, this));
  }

  private void setResult(MatchResult result) {
    if (result == null || getResult().equals(result)) return;
    this.result = result.toString();
    new Query<>(Match.class).col("result", result.toString()).update(id);
    getHome().setWins(result.getHomeScore());
    getGuest().setWins(result.getGuestScore());
    if (matchResult.getPlayed()) {
      setStatus(EventStatus.PLAYED);
      analyseBets();
    }
  }

  private void analyseBets() {
    final List<Bet> bets = new Query<>(Bet.class).where("coverage", this.getId()).entityList();
    for (final Bet bet : bets) {
      int gain = bet.getAmount() * -1;
      if (!bet.getOutcome().equals(result)) {
        bet.getUser().dm("Falscher Tipp für _" + this + "_. Du hast **" + bet.getAmount() + "** TRUEs verloren.");
      } else {
        final double quote = BetFactory.quote(matchResult);
        final int won = (int) Math.round(bet.getAmount() * quote);
        gain += won;
        bet.getUser().addPoints(won);
        bet.getUser().dm("Richtiger Tipp für _" + this + "_. Du hast **" + bet.getAmount() + "** TRUEs (Quote: " +
            Math.round(quote * 10.) / 10 + ") gewonnen.");
      }

      bet.setDifference(gain);
    }
  }

  public MatchResult getExpectedResult() {
    if (getResult().getPlayed()) return getResult();
    if (expectedResult == null) this.expectedResult = getResult().expectResult();
    return expectedResult;
  }

  public String getExpectedResultString() {
    return getExpectedResult() + (isRunning() ? "*" : "");
  }

  @Override
  public int compareTo(@NotNull Match o) {
    return start.compareTo(o.getStart());
  }

  @Override
  public String toString() {
    return this instanceof LeagueMatch leagueMatch ? leagueMatch.toString() : "Scrim: " + getHomeAbbr() + " vs. " + getGuestAbbr();
  }

  @Override
  public boolean equals(Object o) {
    if (id == 0) return false;
    if (this == o) return true;
    if (!(o instanceof final Match match)) return false;
    return getId() == match.getId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
