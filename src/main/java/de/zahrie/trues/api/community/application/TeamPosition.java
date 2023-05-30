package de.zahrie.trues.api.community.application;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.zahrie.trues.api.database.connector.Listing;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.message.Emote;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;

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
  ANALYST(DiscordGroup.ANALYST, "Analyst", null, null),
  LANE_COACH(DiscordGroup.LANE_COACH, "individueller Coach", null, null),
  MENTAL_COACH(DiscordGroup.MENTAL_COACH, "mentaler Coach", null, null),
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

  @NotNull
  public static ActionRow TEAM_ACTION_ROW() {
    final List<SelectOption> selectOptions = Arrays.stream(TeamPosition.values())
        .filter(position -> position.ordinal() <= TEAM_COACH.ordinal())
        .map(TeamPosition::toSelectOption).filter(Objects::nonNull).toList();
    return ActionRow.of(StringSelectMenu.create("app-position").setPlaceholder("Position im Team").addOptions(selectOptions)
        .addOption("andere", "OTHER").build());
  }

  @NotNull
  public static ActionRow ORGA_ACTION_ROW() {
    final List<SelectOption> selectOptions = Arrays.stream(TeamPosition.values())
        .filter(position -> position.ordinal() >= TEAM_COACH.ordinal())
        .map(TeamPosition::toSelectOption).filter(Objects::nonNull).toList();
    return ActionRow.of(StringSelectMenu.create("app-position").setPlaceholder("Position in der Orga").addOptions(selectOptions).build());
  }
}
