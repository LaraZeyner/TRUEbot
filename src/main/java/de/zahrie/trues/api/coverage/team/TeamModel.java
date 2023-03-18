package de.zahrie.trues.api.coverage.team;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.zahrie.trues.api.coverage.ModelBase;
import de.zahrie.trues.api.coverage.player.model.PrimePlayer;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.util.io.request.HTML;
import lombok.Getter;

@Getter
public class TeamModel extends ModelBase implements Serializable {
  @Serial
  private static final long serialVersionUID = -5471189659485625565L;

  protected String url;
  protected PrimeTeam team;
  protected List<PrimePlayer> players;

  public TeamModel(HTML html, String url, PrimeTeam team, List<PrimePlayer> players) {
    super(html);
    this.url = url;
    this.team = team;
    this.players = players;
  }
}
