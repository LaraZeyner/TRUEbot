package de.zahrie.trues.api.coverage.match.log;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import de.zahrie.trues.api.coverage.participator.model.Lineup;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.match.model.PRMMatch;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.player.PrimePlayerFactory;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.util.StringUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table("coverage_log")
@ExtensionMethod(StringUtils.class)
public class MatchLog implements Entity<MatchLog>, Comparable<MatchLog> {
  @Serial
  private static final long serialVersionUID = 2481079289217298298L;

  private int id;
  private final LocalDateTime timestamp; // log_time
  private final Match match; // coverage
  private final MatchLogAction action; // action
  private final String details; // details
  private final Participator participator; // coverage_team

  public MatchLog(Match match, MatchLogAction action, String details, Participator participator) {
    this(LocalDateTime.now(), match, action, details, participator);
  }

  public MatchLog(LocalDateTime timestamp, Match match, MatchLogAction action, String details, Participator participator) {
    this.timestamp = timestamp;
    this.match = match;
    this.action = action;
    this.details = details;
    this.participator = participator;
  }

  public static MatchLog get(List<Object> objects) {
    return new MatchLog(
        (int) objects.get(0),
        (LocalDateTime) objects.get(1),
        new Query<>(Match.class).entity(objects.get(2)),
        new SQLEnum<>(MatchLogAction.class).of(objects.get(3)),
        (String) objects.get(4),
        new Query<>(Participator.class).entity(objects.get(2))
    );
  }

  @Override
  public MatchLog create() {
    return new Query<>(MatchLog.class)
        .key("log_time", timestamp).key("coverage", match).key("action", action).key("details", details)
        .key("coverage_team", participator)
        .insert(this, match::addLog);
  }

  public Participator getParticipator() {
    return participator != null ? participator : Participator.ADMIN(match);
  }

  @Override
  public int compareTo(@NotNull MatchLog o) {
    return Comparator.comparing(MatchLog::getTimestamp).compare(this, o);
  }

  public String detailsOutput() {
    if (details.countMatches("+0100") + details.countMatches("+0200") > 0) {
      return Arrays.stream(details.split(":00 \\+0100"))
          .flatMap(s -> Arrays.stream(s.split(":00 \\+0200")))
          .collect(Collectors.joining("\n"));
    }
    if (action.equals(MatchLogAction.LINEUP_SUBMIT)) {
      final String players = getParticipator().getTeamLineup().getFixedLineups().stream().map(Lineup::getPlayer).map(Player::getSummonerName)
          .collect(Collectors.joining(", "));
      if (isMostRecentLogOfType()) {
        final String linkPlayers = players.replace(", ", ",").replace(" ", "%20");
        return players + "[Op.gg](https://euw.op.gg/multisearch/euw?summoners=" + linkPlayers + ") - [Poro](" + linkPlayers + "/season)";
      }
      return players;
    }
    return details;
  }

  public String actionOutput() {
    return TimeFormat.DISCORD.of(timestamp) + " - " + action.getOutput() + "\n".repeat(extralinesRequired());
  }

  public String teamOutput() {
    return participator == null ? "Admin" : participator.getAbbreviation() + "\n".repeat(extralinesRequired());
  }

  private int extralinesRequired() {
    return Math.max(details.countMatches("+0100") + details.countMatches("+0200"), 1) - 1;
  }

  private boolean isMostRecentLogOfType() {
    final MatchLog entity = new Query<>(MatchLog.class)
        .where("coverage", match).and("action", MatchLogAction.LINEUP_SUBMIT).and("coverage_team", participator)
        .descending("log_time").entity();
    return entity == null || !entity.getTimestamp().isAfter(timestamp);
  }

  public List<Player> determineLineup() {
    return Arrays.stream(getDetails().split(", "))
        .map(playerString -> playerString.before(":").intValue())
        .map(playerId -> match instanceof PRMMatch ? PrimePlayerFactory.getPlayer(playerId) : new Query<>(Player.class).entity(playerId))
        .collect(Collectors.toList());
  }
}
