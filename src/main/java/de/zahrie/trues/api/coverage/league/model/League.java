package de.zahrie.trues.api.coverage.league.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

import de.zahrie.trues.api.coverage.match.model.TournamentMatch;
import de.zahrie.trues.api.coverage.stage.model.PlayStage;
import de.zahrie.trues.api.coverage.team.leagueteam.LeagueTeam;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@ToString
@Entity
@DiscriminatorColumn(name = "prm_id")
@DiscriminatorValue("null")
@Table(name = "coverage_group", indexes = {@Index(name = "idx_coverage_group", columnList = "stage, group_name", unique = true) })
public class League implements Serializable, Comparable<League> {

  @Serial
  private static final long serialVersionUID = -4755609416246322480L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "coverage_group_id", columnDefinition = "TINYINT UNSIGNED not null")
  private short id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "stage", nullable = false)
  @ToString.Exclude
  private PlayStage stage;

  @Column(name = "group_name", nullable = false, length = 25)
  private String name;

  @OneToMany(mappedBy = "league")
  @ToString.Exclude
  private Set<TournamentMatch> matches = new LinkedHashSet<>();

  @OneToMany(mappedBy = "league")
  @ToString.Exclude
  private Set<LeagueTeam> signups = new LinkedHashSet<>();

  public LeagueTier getTier() {
    return LeagueTier.fromName(name);
  }

  @Override
  public int compareTo(@NotNull League o) {
    return Comparator.comparing(League::getStage).compare(this, o);
  }
}
