package de.zahrie.trues.api.discord.channel;

import de.zahrie.trues.database.Database;
import de.zahrie.trues.models.community.OrgaTeam;
import de.zahrie.trues.models.community.OrgaTeamFactory;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

public class DiscordChannelFactory {
  public static DiscordChannel getDiscordChannel(GuildChannel channel) {
    DiscordChannel discordChannel = Database.Find.find(DiscordChannel.class, new String[]{"discordId"}, new Object[]{channel.getIdLong()}, "fromDiscordId");
    if (discordChannel == null) {
      discordChannel = createChannel(channel);
    }
    return discordChannel;
  }

  public static DiscordChannel createChannel(GuildChannel channel) {
    final ChannelType channelType = DiscordChannelFactory.determineChannelType(channel);
    final var discordChannel = new DiscordChannel(channel.getIdLong(), channel.getName(), channelType);
    Database.save(discordChannel);
    return discordChannel;
  }

  public static ChannelType determineChannelType(GuildChannel initialChannel) {
    if (!(initialChannel instanceof final ICategorizableChannel channel)) {
      return ChannelType.PUBLIC;
    }
    final OrgaTeam team = OrgaTeamFactory.getTeamFromCategory(channel.getParentCategoryIdLong());
    if (team != null) {
      return initialChannel instanceof AudioChannel ? ChannelType.TEAM_VOICE : ChannelType.TEAM_CHAT;
    }
    final Category category = channel.getParentCategory();
    if (category == null) {
      return ChannelType.PUBLIC;
    }
    final ChannelType type;
    switch (category.getName()) {
      case "Social Media" -> type = ChannelType.SOCIALS;
      case "Events" -> type = ChannelType.EVENTS;
      case "Orga Intern" -> type = ChannelType.ORGA_INTERN;
      case "Content" -> type = ChannelType.CONTENT_INTERN;
      default -> type = ChannelType.PUBLIC;
    }
    return type;
  }
}
