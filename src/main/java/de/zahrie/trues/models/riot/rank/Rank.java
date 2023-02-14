package de.zahrie.trues.models.riot.rank;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Rank {
  private RankDivision division = new RankDivision();
  private LeaguePoints points = null;
  private int wins = 0;
  private int losses = 0;

  public Rank(RankDivision division, LeaguePoints points) {
    this.division = division;
    this.points = points;
  }
}
