package de.zahrie.trues.api.riot.matchhistory.champion;

import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.riot.Xayah;
import de.zahrie.trues.api.database.Database;
import org.jetbrains.annotations.NotNull;

public class ChampionFactory {
  public static void loadAllChampions() {
    for (final com.merakianalytics.orianna.types.core.staticdata.Champion riotChampion : Xayah.getChampions()) {
      final var champion = getChampion(riotChampion);
      champion.setName(riotChampion.getName());
      Database.save(champion);
    }
  }

  public static Champion getChampion(@NotNull com.merakianalytics.orianna.types.core.staticdata.Champion riotChampion) {
    Champion champion = Database.Find.find(Champion.class, riotChampion.getId());
    if (champion == null) {
      champion = new Champion(riotChampion.getId(), riotChampion.getName());
      Database.save(champion);
    }
    return champion;
  }

  public static Champion getChampion(int id) {
    Champion champion = Database.Find.find(Champion.class, id);
    if (champion == null) {
      final com.merakianalytics.orianna.types.core.staticdata.Champion riotChampion = Xayah.championWithId(id).get();
      if (riotChampion != null) {
        champion = new Champion(id, riotChampion.getName());
        Database.save(champion);
      }
    }
    return champion;
  }

  public static Champion getChampion(String name) {
    Champion champion = QueryBuilder.hql(Champion.class, "FROM Champion WHERE name = " + name).single();
    if (champion == null) {
      final com.merakianalytics.orianna.types.core.staticdata.Champion riotChampion = Xayah.championNamed(name).get();
      if (riotChampion != null) {
        champion = new Champion(riotChampion.getId(), riotChampion.getName());
        Database.save(champion);
      }
    }
    return champion;
  }
}
