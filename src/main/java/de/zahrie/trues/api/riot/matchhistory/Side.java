package de.zahrie.trues.api.riot.matchhistory;

import java.util.Arrays;

import com.merakianalytics.orianna.types.core.match.Match;
import com.merakianalytics.orianna.types.core.match.Team;
import de.zahrie.trues.api.database.connector.Listing;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Listing(Listing.ListingType.LOWER)
@RequiredArgsConstructor
@Getter
public enum Side {
  NONE(0),
  BLUE(100),
  RED(200);

  public static Side ofId(final int id) {
    return Arrays.stream(Side.values()).filter(side -> side.getId() == id).findFirst().orElse(NONE);
  }

  private final int id;

  public Team getTeam(Match match) {
    return switch (this) {
      case BLUE -> match.getBlueTeam();
      case RED -> match.getRedTeam();
      default -> null;
    };
  }

  public com.merakianalytics.orianna.types.common.Side getSide() {
    return com.merakianalytics.orianna.types.common.Side.valueOf(name());
  }
}
