package de.zahrie.trues.api.coverage.team.model;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.season.SeasonFactory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
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
public class PrimeTeam extends Team implements Serializable {
  @Serial
  private static final long serialVersionUID = 6594410747323472599L;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "division", nullable = false)
  @ToString.Exclude
  private League league;

  @Embedded
  private TeamScore score;

  @Embedded
  private TeamRecord record;

  public PrimeTeam(int prmId, String name, String abbreviation) {
    super(name, abbreviation);
    this.setPrmId(prmId);
  }

  public League getCurrentLeague() {
    return league.getStage().getSeason().equals(SeasonFactory.getLastSeason()) ? league : null;
  }

  public void setScore(League division, String score) {
    final String place = score.split("\\.")[0];
    final short placeInteger = Short.parseShort(place);
    final String wins = score.split("\\(")[1].split("/")[0];
    final short winsInteger = Short.parseShort(wins);
    final String losses = score.split("/")[1].split("\\)")[0];
    final short lossesInteger = Short.parseShort(losses);
    this.score = new TeamScore(division, placeInteger, winsInteger, lossesInteger);
  }

  public void setRecord(String record, short seasons) {
    final String wins = record.split(" / ")[0];
    final short winsInteger = Short.parseShort(wins);
    final String losses = record.split(" / ")[1];
    final short lossesInteger = Short.parseShort(losses);
    this.record = new TeamRecord(seasons, winsInteger, lossesInteger);
  }

}