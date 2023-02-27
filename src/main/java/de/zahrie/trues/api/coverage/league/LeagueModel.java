package de.zahrie.trues.api.coverage.league;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Lara on 15.02.2023 for TRUEbot
 */
@Getter
@AllArgsConstructor
public class LeagueModel implements Serializable {
  @Serial
  private static final long serialVersionUID = 2667181184120392512L;

  protected String url;
  protected League league;
  protected List<PrimeTeam> teams;
  protected List<LeaguePlayday> playdays;

}
