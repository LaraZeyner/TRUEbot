package de.zahrie.trues.api.coverage.league.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import de.zahrie.trues.api.coverage.match.model.TournamentMatch;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.playday.scheduler.PlaydayScheduleHandler;
import de.zahrie.trues.api.coverage.playday.scheduler.PlaydayScheduler;
import de.zahrie.trues.api.coverage.season.PrimeSeason;
import de.zahrie.trues.api.coverage.stage.model.PlayStage;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.util.Const;
import de.zahrie.trues.util.io.request.URLType;
import de.zahrie.trues.api.datatypes.calendar.Time;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "coverage_group", indexes = {
        @Index(name = "idx_coverage_group", columnList = "stage, group_name", unique = true) })
@NamedQuery(name = "Group.fromNameAndSeason", query = "FROM League WHERE name = :name AND stage.season = :season")
public class League implements Serializable {

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

  @Column(name = "prm_id", nullable = false)
  private int prmId;

  @Column(name = "group_name", nullable = false, length = 25)
  private String name;

  @OneToMany(mappedBy = "league")
  @ToString.Exclude
  private Set<TournamentMatch> coverages = new LinkedHashSet<>();

  @OneToMany(mappedBy = "league")
  @ToString.Exclude
  private Set<PrimeTeam> teams = new LinkedHashSet<>();

  public LeagueTier getTier() {
    return LeagueTier.fromName(name);
  }

  public Time getAlternative(Playday playday) {
    //TODO (Abgie) 15.03.2023: never used
    final PlaydayScheduler scheduler = new PlaydayScheduleHandler(stage, playday.getId(), getTier()).create();
    return scheduler.defaultTime();
  }
  public boolean isStarter() {
    return this.name.equals(Const.Gamesports.STARTER_NAME);
  }

  public String getUrl() {
    return String.format(URLType.LEAGUE.getUrlName(), ((PrimeSeason) stage.getSeason()).getPrmId(), stage.pageId(), prmId);
  }

}
