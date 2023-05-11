package de.zahrie.trues.api.riot;

import com.merakianalytics.orianna.Orianna;
import de.zahrie.trues.api.riot.champion.ChampionFactory;
import de.zahrie.trues.util.Connectable;
import de.zahrie.trues.util.io.cfg.JSON;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public final class Xayah extends Orianna implements Connectable {
  private static Xayah instance = new Xayah();

  public static Xayah getInstance() {
    return instance;
  }

  @Override
  public void connect() {
    Xayah.loadConfiguration("riotcfg.json");
    final var json = JSON.fromFile("connect.json");
    Xayah.setRiotAPIKey(json.getString("riot"));
    ChampionFactory.loadAllChampions();
  }

  @Override
  public void disconnect() {
    // no actions
  }
}
