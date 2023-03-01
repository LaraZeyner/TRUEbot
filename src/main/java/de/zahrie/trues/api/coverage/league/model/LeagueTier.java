package de.zahrie.trues.api.coverage.league.model;

import java.util.Arrays;

import de.zahrie.trues.util.Const;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LeagueTier {
  Division_3(3),
  Division_4(4),
  Division_5(5),
  Division_6(6),
  Division_7(7),
  Division_8(8),
  Division_9(9),
  Swiss_Starter(10);

  private final int index;

  private static int idFromName(String name) {
    if (name.startsWith("Division ")) {
      return Integer.parseInt(name.replace("Division ", "").split("\\.")[0]);
    }
    return name.equals(Const.Gamesports.STARTER_NAME) ? 10 : -1;
  }

  public static LeagueTier fromName(String name) {
    return Arrays.stream(LeagueTier.values()).filter(tier -> tier.index == idFromName(name)).findFirst().orElse(null);
  }

}
