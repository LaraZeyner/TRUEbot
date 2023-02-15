package de.zahrie.trues.models.coverage.match;

import de.zahrie.trues.util.database.Database;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Lara on 15.02.2023 for TRUEbot
 */
public final class MatchFactory {

  @Nullable
  public static PrimeMatch getEvent(int matchId) {
    return Database.find(PrimeMatch.class, new String[]{"matchId"}, new Object[]{matchId}, "fromMatchId");
  }

}
