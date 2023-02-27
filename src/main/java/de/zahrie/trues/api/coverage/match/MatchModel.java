package de.zahrie.trues.api.coverage.match;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.zahrie.trues.api.coverage.ModelBase;
import de.zahrie.trues.api.coverage.match.model.PrimeMatch;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.util.io.request.HTML;
import lombok.Getter;

/**
 * Created by Lara on 15.02.2023 for TRUEbot
 */
@Getter
public class MatchModel extends ModelBase implements Serializable {
  @Serial
  private static final long serialVersionUID = -6308081229468669808L;

  protected String url;
  protected PrimeMatch match;
  protected List<HTML> logs;
  protected List<PrimeTeam> teams;

  public MatchModel(HTML html, String url, PrimeMatch match, List<HTML> logs, List<PrimeTeam> teams) {
    super(html);
    this.url = url;
    this.match = match;
    this.logs = logs;
    this.teams = teams;
  }

}
