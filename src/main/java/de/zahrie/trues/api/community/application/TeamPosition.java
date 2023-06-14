package de.zahrie.trues.api.community.application;

import de.zahrie.trues.api.database.connector.Listing;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.message.Emote;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

@RequiredArgsConstructor
@Getter
@Listing(Listing.ListingType.CAPITALIZE)
public enum TeamPosition {
  TOP(DiscordGroup.TOP, "Toplaner", null, Emote.TOP.getEmoji()),
  JUNGLE(DiscordGroup.JUNGLE, "Jungler", null, Emote.JUNGLE.getEmoji()),
  MIDDLE(DiscordGroup.MIDDLE, "Midlaner", null, Emote.MIDDLE.getEmoji()),
  BOTTOM(DiscordGroup.BOTTOM, "Botlaner", null, Emote.BOTTOM.getEmoji()),
  SUPPORT(DiscordGroup.SUPPORT, "Supporter", null, Emote.SUPPORT.getEmoji()),
  TEAM_COACH(DiscordGroup.TEAM_COACH, "Team Coach/Mentor", "Begleiter für Teams", null),
  COACH(DiscordGroup.TEAM_COACH, "Coach", "Genereller Coach", null),
  EVENT_PLANNING(DiscordGroup.EVENT_PLANNING, "Event", "Event Planung", null),
  SOCIAL_MEDIA(DiscordGroup.SOCIAL_MEDIA, "Socials", "Social Media", null),
  CASTER(DiscordGroup.CASTER, "Caster", "Casting", null),
  DEVELOPER(DiscordGroup.DEVELOPER, "Dev", "Entwickler mit Kenntnissen in Java, HTML, SQL", null);


  private final DiscordGroup discordGroup;
  private final String name;
  private final String description;
  private final Emoji emoji;

  public SelectOption toSelectOption() {
    return name == null ? null : SelectOption.of(name, name()).withDescription(description).withEmoji(emoji);
  }

  @Override
  public String toString() {
    return name;
  }

  public boolean isTeam() {
    return ordinal() <= TEAM_COACH.ordinal();
  }

  public boolean isOrga() {
    return ordinal() >= TEAM_COACH.ordinal();
  }
}
