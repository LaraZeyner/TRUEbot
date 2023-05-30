package de.zahrie.trues.api.riot;

import java.util.List;

import com.merakianalytics.orianna.types.common.Region;
import de.zahrie.trues.api.riot.champion.ChampionFactory;
import de.zahrie.trues.util.io.cfg.JSON;
import lombok.EqualsAndHashCode;
import no.stelar7.api.r4j.basic.APICredentials;
import no.stelar7.api.r4j.impl.R4J;
import org.json.JSONArray;

@EqualsAndHashCode(callSuper = true)
public final class Zeri extends R4J {
  private static List<String> keys;
  private static String currentKey;
  private static Zeri api;

  public static LOLAPI get() {
    if (api == null) connect();
    return api.getLoLAPI();
  }

  private Zeri(String key) {
    super(new APICredentials(key, key, key, key, key));
  }

  public static void next() {
    final int i = keys.indexOf(currentKey);
    api = new Zeri(keys.size() > i + 1 ? keys.get(i + 1) : keys.get(0));
  }

  private static void connect() {
    final var json = JSON.read("connect.json");
    final JSONArray riotKeys = json.getJSONArray("riot");
    keys = riotKeys.toList().stream().map(o -> (String) o).toList();

    currentKey = keys.get(0);
    api = new Zeri(currentKey);

    Xayah.setDefaultRegion(Region.EUROPE_WEST);
    ChampionFactory.loadAllChampions();
  }
}
