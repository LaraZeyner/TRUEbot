package de.zahrie.trues.api.coverage.team.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.coverage.season.signup.SeasonSignup;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.database.QueryBuilder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DiscriminatorFormula;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "team")
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
      Database.update(orgaTeam);
    }
    this.name = name;
    Database.updateAndCommit(this);
  }

  @Column(name = "team_abbr", nullable = false, length = 50)
  private String abbreviation;

  public void setAbbreviation(String abbreviation) {
    if (orgaTeam != null && !orgaTeam.getAbbreviation().equals(abbreviation)) {
      orgaTeam.setAbbreviationCreation(abbreviation);
      Database.updateAndCommit(orgaTeam);
    }
    this.abbreviation = abbreviation;
  }

  @Column(name = "refresh", nullable = false)
  @Setter(AccessLevel.NONE)
  private LocalDateTime refresh;

  @OneToOne(mappedBy = "team")
  @ToString.Exclude
  private OrgaTeam orgaTeam;

  @Column(name = "highlight", nullable = false)
  private boolean highlight = false;

  @Column(name = "last_team_mmr")
  private Integer lastMMR;

  public List<Participator> getParticipators() {
    return QueryBuilder.hql(Participator.class, "FROM Participator WHERE team = :team").addParameter("team", this).list();
  }

  public List<SeasonSignup> getSignups() {
    return QueryBuilder.hql(SeasonSignup.class, "FROM SeasonSignup WHERE team = :team").addParameter("team", this).list();
  }

  @Nullable
  public SeasonSignup getSignupForSeason(Season season) {
    return QueryBuilder.hql(SeasonSignup.class, "FROM SeasonSignup WHERE season = :season and team = :team").addParameters(Map.of("season", season, "team", this)).single();
  }

  public List<Player> getPlayers() {
    return QueryBuilder.hql(Player.class, "FROM Player WHERE team = :team").addParameter("team", this).list();
  }

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

  public void refresh(LocalDateTime start) {
    final LocalDateTime refreshUntil = orgaTeam == null ? start.plusDays(70) : LocalDateTime.MAX;
    if (refreshUntil.isAfter(this.refresh)) {
      this.refresh = refreshUntil;
      Database.update(this);
    }
  }

  public void highlight() {
    setHighlight(!this.highlight);
  }

  public void setHighlight(boolean highlight) {
    this.highlight = highlight;
    Database.update(this);
  }

  public String getFullName() {
    return name + " (" + abbreviation + ")";
  }

  public Match nextOrLastMatch() {
    final List<Match> matches = QueryBuilder.hql(Match.class, "SELECT coverage FROM Participator WHERE team = :team and coverage.active = true ORDER BY coverage.start").addParameter("team", this).list();
    return matches.stream().filter(match -> match.getStart().isAfter(LocalDateTime.now())).findFirst().orElse(matches.get(matches.size() - 1));
  }

  public MatchManager getMatches() {
    return new MatchManager(this);
  }

  public PRMTeam getPRMTeam() {
    return (PRMTeam) Hibernate.unproxy(this);
  }
}
