package de.zahrie.trues.discord.event.models;

import de.zahrie.trues.api.community.orgateam.OrgaTeamFactory;
import de.zahrie.trues.api.discord.channel.AbstractDiscordChannel;
import de.zahrie.trues.api.discord.channel.DiscordChannelFactory;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Events, die sich auf Channel beziehen
 * @see ChannelCreateEvent
 * @see ChannelDeleteEvent
 * @see net.dv8tion.jda.api.events.channel.GenericChannelEvent
 * @see net.dv8tion.jda.api.events.channel.forum.ForumTagAddEvent
 * @see net.dv8tion.jda.api.events.channel.forum.ForumTagRemoveEvent
 * @see net.dv8tion.jda.api.events.channel.forum.GenericForumTagEvent
 * @see net.dv8tion.jda.api.events.channel.forum.update.ForumTagUpdateEmojiEvent
 * @see net.dv8tion.jda.api.events.channel.forum.update.ForumTagUpdateModeratedEvent
 * @see net.dv8tion.jda.api.events.channel.forum.update.ForumTagUpdateNameEvent
 * @see net.dv8tion.jda.api.events.channel.forum.update.GenericForumTagUpdateEvent
 * @see net.dv8tion.jda.api.events.channel.update.ChannelUpdateAppliedTagsEvent
 * @see net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent
 * @see net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchiveTimestampEvent
 * @see net.dv8tion.jda.api.events.channel.update.ChannelUpdateAutoArchiveDurationEvent
 * @see net.dv8tion.jda.api.events.channel.update.ChannelUpdateBitrateEvent
 * @see net.dv8tion.jda.api.events.channel.update.ChannelUpdateDefaultLayoutEvent
 * @see net.dv8tion.jda.api.events.channel.update.ChannelUpdateDefaultReactionEvent
 * @see net.dv8tion.jda.api.events.channel.update.ChannelUpdateDefaultThreadSlowmodeEvent
 * @see net.dv8tion.jda.api.events.channel.update.ChannelUpdateFlagsEvent
 * @see net.dv8tion.jda.api.events.channel.update.ChannelUpdateInvitableEvent
 * @see net.dv8tion.jda.api.events.channel.update.ChannelUpdateLockedEvent
 * @see ChannelUpdateNameEvent
 * @see net.dv8tion.jda.api.events.channel.update.ChannelUpdateNSFWEvent
 * @see net.dv8tion.jda.api.events.channel.update.ChannelUpdateParentEvent
 * @see net.dv8tion.jda.api.events.channel.update.ChannelUpdatePositionEvent
 * @see net.dv8tion.jda.api.events.channel.update.ChannelUpdateRegionEvent
 * @see net.dv8tion.jda.api.events.channel.update.ChannelUpdateSlowmodeEvent
 * @see net.dv8tion.jda.api.events.channel.update.ChannelUpdateTopicEvent
 * @see net.dv8tion.jda.api.events.channel.update.ChannelUpdateTypeEvent
 * @see net.dv8tion.jda.api.events.channel.update.ChannelUpdateUserLimitEvent
 * @see net.dv8tion.jda.api.events.channel.update.GenericChannelUpdateEvent
 */
@ExtensionMethod({DiscordChannelFactory.class, OrgaTeamFactory.class})
public class ChannelEvent extends ListenerAdapter {

  @Override
  public void onChannelCreate(ChannelCreateEvent event) {
    final GuildChannel channel = event.getChannel().asGuildChannel();
    if (event.getChannelType().equals(ChannelType.GUILD_NEWS_THREAD) || event.getChannelType().equals(ChannelType.GUILD_PRIVATE_THREAD) || event.getChannelType().equals(ChannelType.GUILD_PUBLIC_THREAD)) return;

    channel.getDiscordChannel().updatePermissions();
  }

  @Override
  public void onChannelDelete(ChannelDeleteEvent event) {
    final GuildChannel channel = event.getChannel().asGuildChannel();
    channel.removeTeamChannel();

  }

  @Override
  public void onChannelUpdateName(ChannelUpdateNameEvent event) {
    final GuildChannel channel = event.getChannel().asGuildChannel();
    final AbstractDiscordChannel discordChannel = DiscordChannelFactory.getDiscordChannel(channel);
    discordChannel.setName(event.getNewValue());
  }
}
