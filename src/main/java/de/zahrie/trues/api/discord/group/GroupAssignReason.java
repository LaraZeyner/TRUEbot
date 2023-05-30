package de.zahrie.trues.api.discord.group;

import de.zahrie.trues.api.database.connector.Listing;

@Listing(Listing.ListingType.UPPER)
public enum GroupAssignReason {
  ADD,
  BDAY,
  RANKUP
}
