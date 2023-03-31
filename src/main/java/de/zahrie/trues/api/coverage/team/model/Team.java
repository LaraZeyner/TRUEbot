package de.zahrie.trues.api.coverage.team.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.database.types.TimeCoverter;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.api.datatypes.calendar.Time;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.Type;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "team", schema = "prm", indexes = {@Index(name = "idx_channel", columnList = "orga_team", unique = true)})
@DiscriminatorFormula("IF(team_id IS NULL, '0', '1')")
@NamedQuery(name = "Team.OrgaTeams.str", query = "SELECT name FROM Team WHERE orgaTeam IS NOT NULL")
public class Team implements Serializable {
  @Serial
  private static final long serialVersionUID = -8929555475128771601L;


  @Id
  @Column(name = "t_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "team_name", nullable = false, length = 100)
  private String name;

  public void setName(String name) {
    if (orgaTeam != null && !orgaTeam.getNameCreation().equals(name)) {
      orgaTeam.setNameCreation(name);
      Database.saveAndCommit(orgaTeam);
    }
    this.name = name;
  }

  @Column(name = "team_abbr", nullable = false, length = 50)
  private String abbreviation;

  public void setAbbreviation(String abbreviation) {
    if (orgaTeam != null && !orgaTeam.getAbbreviation().equals(abbreviation)) {
      orgaTeam.setAbbreviationCreation(abbreviation);
      Database.saveAndCommit(orgaTeam);
    }
    this.abbreviation = abbreviation;
  }

  @Temporal(TemporalType.TIMESTAMP)
  @Type(TimeCoverter.class)
  @Column(name = "refresh", nullable = false)
  private Time refresh;

  @OneToOne(mappedBy = "team")
  @ToString.Exclude
  private OrgaTeam orgaTeam;

  @Column(name = "highlight", nullable = false)
  private boolean highlight = false;

  @Column(name = "last_team_mmr")
  private Integer lastMMR;
  @OneToMany(mappedBy = "team")
  @ToString.Exclude
  private Set<Participator> participators = new LinkedHashSet<>();

  @OneToMany(mappedBy = "team")
  @ToString.Exclude
  private Set<Player> players = new LinkedHashSet<>();

  public Team(String name, String abbreviation) {
    this.name = name;
    this.abbreviation = abbreviation;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Team)) return false;
    if (this.name != null) return this.name.equals(((Team) obj).getName());
    return this.id == ((Team) obj).getId();
  }

  public void refresh(Time start) {
    final Time time = new Time(start).plus(Time.DATE, 70);
    this.setRefresh(time);
  }

  public void setRefresh(Time refresh) {
    if (refresh.after(this.refresh)) {
      this.refresh = refresh;
      Database.save(this);
    }
  }

  public boolean isNotOrgaTeam() {
    return this.orgaTeam == null;
  }

  public void highlight() {
    this.highlight = !this.highlight;
    Database.save(this);
  }

  public void setHighlight(boolean highlight) {
    this.highlight = highlight;
    Database.save(this);
  }

  public String getFullName() {
    return name + " (" + abbreviation + ")";
  }

  public Match nextOrLastMatch() {
    final List<Match> matches = participators.stream().map(Participator::getCoverage).filter(Match::isActive).sorted().toList();
    return matches.stream().filter(match -> match.getStart().after(new Time())).findFirst().orElse(matches.get(matches.size() - 1));
  }
}
