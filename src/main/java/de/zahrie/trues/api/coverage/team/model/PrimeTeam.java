package de.zahrie.trues.api.coverage.team.model;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.coverage.league.model.League;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
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
@DiscriminatorValue("1")
@NamedQuery(name = "PrimeTeam.Orgateams.str", query = "SELECT name FROM Team WHERE orgaTeam IS NOT NULL")
@NamedQuery(name = "PrimeTeam.Teaminfo", query = "SELECT name, league.name, score FROM PrimeTeam WHERE orgaTeam IS NOT NULL")
@NamedQuery(name = "PrimeTeam.fromNameAbbr", query = "FROM PrimeTeam WHERE name = :name AND abbreviation = :abbr")
@NamedQuery(name = "PrimeTeam.fromPrmId", query = "FROM PrimeTeam WHERE prmId = :id")
// TODO extract to other table
public class PrimeTeam extends Team implements Serializable {

  @Serial
  private static final long serialVersionUID = 6594410747323472599L;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "division")
  @ToString.Exclude
  private League league;

  @Column(name = "score", length = 30)
  private String score;

  @Column(name = "record", length = 9)
  private String record;

  @Column(name = "seasons", columnDefinition = "TINYINT UNSIGNED")
  private Short seasons;

  public PrimeTeam(int prmId, String name, String abbreviation) {
    super(name, abbreviation);
    this.setPrmId(prmId);
  }

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