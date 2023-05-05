package de.zahrie.trues.api.coverage.player.model;

import de.zahrie.trues.api.database.connector.Listing;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Listing(Listing.ListingType.ORDINAL)
@RequiredArgsConstructor
@Getter
public enum Division {
  ZERO(0),
  I(300),
  II(200),
  III(100),
  IV(0);
  private final int points;

}
