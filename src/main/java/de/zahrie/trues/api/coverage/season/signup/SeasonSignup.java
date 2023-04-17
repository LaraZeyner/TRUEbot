package de.zahrie.trues.api.coverage.season.signup;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.Database;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "season_signup", indexes = @Index(name = "season_team_idx_season_team", columnList = "season, team", unique = true))
public class SeasonSignup implements Serializable, Comparable<SeasonSignup> {
  @Serial
  private static final long serialVersionUID = -763378764697829834L;

  public static SeasonSignup build(Season season, Team team, String info) {
    final var signup = new SeasonSignup(season, team, info);
    Database.insert(signup);
    return signup;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seasonteam_id", nullable = false)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "season", nullable = false)
  @ToString.Exclude
  private Season season;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "team", nullable = false)
  @ToString.Exclude
  private Team team;

  @Column(name = "signup_info", length = 100)
  private String info;

  public SeasonSignup(Season season, Team team, String info) {
    this.season = season;
    this.team = team;
    this.info = info;
  }

  @Override
  public int compareTo(@NotNull SeasonSignup o) {
    return Comparator.comparing(SeasonSignup::getSeason).compare(this, o);
  }
}