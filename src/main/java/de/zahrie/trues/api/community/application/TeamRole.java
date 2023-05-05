package de.zahrie.trues.api.community.application;

import de.zahrie.trues.api.database.connector.Listing;

@Listing(Listing.ListingType.CAPITALIZE)
public enum TeamRole {
  /**
   * hilft für einen Tag aus
   */
  STANDIN,
  /**
   * Bewerbungsgespräch angenommen <br>
   * Wenn Tryout für Team dann ist dies die Auswahlrolle
   */
  TRYOUT,
  /**
   * Substitude oder wenn temporär Tryout für dieses Team
   */
  SUBSTITUTE,
  /**
   * Stammspieler (max. 5 pro Team)
   */
  MAIN,
  ORGA_TRYOUT,
  /**
   * Teil als Staffmember
   */
  ORGA,
  REMOVE
}
