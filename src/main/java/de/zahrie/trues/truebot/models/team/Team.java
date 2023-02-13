package de.zahrie.trues.truebot.models.team;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import de.zahrie.trues.truebot.models.community.OrgaTeam;
import de.zahrie.trues.truebot.models.coverage.Participator;
import de.zahrie.trues.truebot.models.riot.Player;
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
@Entity(name = "Team")
@Table(name = "team", schema = "prm", indexes = {@Index(name = "idx_channel", columnList = "orga_team", unique = true)})
@NamedQuery(name = "Team.Orgateams.str", query = "SELECT name FROM Team WHERE orgaTeam IS NOT NULL")
@NamedQuery(name = "Team.Teaminfo", query = "SELECT name, divisionName, score FROM Team WHERE orgaTeam IS NOT NULL")
public class Team implements Serializable {
  @Serial
  private static final long serialVersionUID = -8929555475128771601L;


  @Id
  @Column(name = "team_id", nullable = false)
  private int id;

  @Column(name = "team_name", nullable = false, length = 100)
  private String name;

  @Column(name = "team_abbr", nullable = false, length = 50)
  private String abbreviation;
  @ToString.Exclude
  @Column(name = "division", length = 46)
  private String divisionName;
  @Column(name = "score", length = 30)
  private String score;
  @Column(name = "record", length = 9)
  private String record;
  @Column(name = "seasons", columnDefinition = "TINYINT UNSIGNED")
  private Short seasons;
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "refresh", nullable = false)
  private Calendar refresh;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "orga_team")
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

  public TeamScore determineScore() {
    final String place = this.score.split("\\.")[0];
    final int placeInteger = Integer.parseInt(place);
    final String wins = this.score.split("\\(")[1].split("/")[0];
    final int winsInteger = Integer.parseInt(wins);
    final String losses = this.score.split("/")[1].split("\\)")[0];
    final int lossesInteger = Integer.parseInt(losses);
    return new TeamScore(placeInteger, winsInteger, lossesInteger);
  }

  public TeamRecord determineRecord() {
    final String wins = this.record.split(" / ")[0];
    final int winsInteger = Integer.parseInt(wins);
    final String losses = this.record.split(" / ")[1];
    final int lossesInteger = Integer.parseInt(losses);
    return new TeamRecord(winsInteger, lossesInteger);
  }
}