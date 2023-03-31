package de.zahrie.trues.api.community.application;

import de.zahrie.trues.api.discord.group.DiscordGroup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TeamPosition {
  TOP(null),
  JUNGLE(null),
  MIDDLE(null),
  BOTTOM(null),
  SUPPORT(null),
  MENTOR(null),
  EVENT_PLANNING(DiscordGroup.EVENT_PLANNING),
  CASTER(DiscordGroup.CASTER),
  SOCIAL_MEDIA(DiscordGroup.SOCIAL_MEDIA),
  ANALYST(DiscordGroup.ANALYST),
  DRAFT_COACH(DiscordGroup.DRAFT_COACH),
  LANE_COACH(DiscordGroup.LANE_COACH),
  MENTAL_COACH(DiscordGroup.MENTAL_COACH),
  STRATEGIC_COACH(DiscordGroup.STRATEGIC_COACH),
  TEAM_BUILDING(DiscordGroup.TEAM_BUILDING),
  EVENT_LEAD(DiscordGroup.EVENT_LEAD),
  SOCIAL_MEDIA_LEAD(DiscordGroup.SOCIAL_MEDIA_LEAD),
  EVENT_MANAGER(DiscordGroup.EVENT_MANAGER),
  COMMUNITY_MANAGER(DiscordGroup.COMMUNITY_MANAGER),
  COACHING_MANAGER(DiscordGroup.COACHING_MANAGER);

  private final DiscordGroup discordGroup;
}
