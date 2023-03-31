package de.zahrie.trues.api.coverage.player;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.coverage.player.model.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerModel implements Serializable {

  @Serial
  private static final long serialVersionUID = -1890044855143728407L;

  protected String url;
  protected Player player;

}
