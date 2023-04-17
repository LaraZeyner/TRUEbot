package de.zahrie.trues.api.coverage.match.log;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.util.StringUtils;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "coverage_log")
@DiscriminatorColumn(name = "action")
@ExtensionMethod(StringUtils.class)
public class MatchLog implements Serializable, Comparable<MatchLog> {
  @Serial
  private static final long serialVersionUID = 7775661777098550144L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "log_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @Column(name = "log_time", nullable = false)
  private LocalDateTime timestamp = LocalDateTime.now();

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "coverage")
  @ToString.Exclude
  private Match match;

  @Enumerated(EnumType.STRING)
  @Column(name = "action", nullable = false, length = 50, insertable=false, updatable=false)
  private MatchLogAction action;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coverage_team")
  @ToString.Exclude
  private Participator participator;

  @Column(name = "details", nullable = false, length = 1000)
  private String details = "-";

  @Column(name = "to_send", nullable = false)
  private boolean toSend = true;

  public MatchLog(LocalDateTime timestamp, MatchLogAction action, String details) {
    this.timestamp = timestamp;
    this.action = action;
    this.details = details;
  }

  public MatchLog handleTeam(String content) {
    final MatchLog log = LogFactory.handleUserWithTeam(this, content);
    if (this.participator != null) Database.update(this.participator);
    return log;
  }

  public Participator getParticipator() {
    return participator != null ? participator : new Participator(false, new Team("Administration", "Admin"));
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
      final String players = getParticipator().getLineups().stream().map(Lineup::getPlayer).map(Player::getSummonerName)
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
    return match.getLogs().stream()
        .filter(matchLog -> matchLog.getAction().equals(MatchLogAction.LINEUP_SUBMIT))
        .filter(matchLog -> matchLog.getParticipator().equals(participator))
        .noneMatch(matchLog -> matchLog.getTimestamp().isAfter(timestamp));
  }
}
