package de.zahrie.trues.api.discord.channel;

import de.zahrie.trues.api.database.connector.Listing;

@Listing(Listing.ListingType.LOWER)
public enum DiscordChannelType {
  TEXT,
  PRIVATE,
  VOICE,
  GROUP,
  CATEGORY,
  NEWS,
  STAGE,
  GUILD_NEWS_THREAD,
  GUILD_PUBLIC_THREAD,
  GUILD_PRIVATE_THREAD,
  FORUM,
  UNKNOWN
}
