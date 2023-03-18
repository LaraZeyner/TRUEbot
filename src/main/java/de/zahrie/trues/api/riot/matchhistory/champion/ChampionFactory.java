package de.zahrie.trues.api.riot.matchhistory.champion;

import de.zahrie.trues.api.riot.Xayah;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.RiotChampion;
import de.zahrie.trues.database.Database;
import org.jetbrains.annotations.NotNull;

public class ChampionFactory {
  public static Champion getChampion(@NotNull RiotChampion riotChampion) {
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
      final RiotChampion riotChampion = Xayah.championWithId(id).get();
      if (riotChampion != null) {
        champion = new Champion(id, riotChampion.getName());
        Database.save(champion);
      }
    }
    return champion;
  }

  public static Champion getChampion(String name) {
    Champion champion = Database.Find.find(Champion.class, new String[]{"name"}, new Object[]{name}, "fromName");
    if (champion == null) {
      final RiotChampion riotChampion = Xayah.championNamed(name).get();
      if (riotChampion != null) {
        champion = new Champion(riotChampion.getId(), riotChampion.getName());
        Database.save(champion);
      }
    }
    return champion;
  }
}
