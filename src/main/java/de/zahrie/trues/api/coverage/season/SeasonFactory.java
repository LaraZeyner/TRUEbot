package de.zahrie.trues.api.coverage.season;

import de.zahrie.trues.util.database.Database;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Lara on 15.02.2023 for TRUEbot
 */
public final class SeasonFactory {

  @Nullable
  public static PrimeSeason getSeason(int seasonId) {
    return Database.Find.find(PrimeSeason.class, new String[]{"seasonId"}, new Object[]{seasonId}, "fromSeasonId");
  }

  @Nullable
  public static PrimeSeason getSeason(String seasonName) {
    return Database.Find.find(PrimeSeason.class, new String[]{"name"}, new Object[]{seasonName}, "fromName");
  }

}
