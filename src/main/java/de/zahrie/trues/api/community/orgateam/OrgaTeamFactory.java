package de.zahrie.trues.api.community.orgateam;


import java.util.Arrays;
import java.util.stream.Collectors;

import de.zahrie.trues.api.coverage.team.TeamFactory;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.api.datatypes.symbol.StringExtention;
import de.zahrie.trues.api.discord.channel.ChannelKind;
import de.zahrie.trues.api.discord.channel.DiscordChannelFactory;
import de.zahrie.trues.api.discord.group.CustomDiscordGroup;
import de.zahrie.trues.api.discord.group.GroupType;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.util.Util;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jetbrains.annotations.Nullable;

@ExtensionMethod({DiscordChannelFactory.class, StringExtention.class})
public final class OrgaTeamFactory {
  @Nullable
  public static OrgaTeam getTeamFromName(@NonNull String name) {
    return Database.Find.find(OrgaTeam.class, new String[]{"name"}, new Object[]{name}, "fromName");
  }

  @Nullable
  public static OrgaTeam getTeamFromChannel(@NonNull GuildChannel channel) {
    if (channel instanceof ICategorizableChannel categorizableChannel) {
      final Category category = categorizableChannel.getParentCategory();
      if (category != null) return getTeamFromCategoryId(category.getIdLong());
    }
    return getTeamFromCategoryId(channel.getIdLong());
  }

  @Nullable
  public static TeamChannel getTeamChannelFromChannel(@NonNull GuildChannel channel) {
    return Database.Find.find(TeamChannel.class, new String[]{"discordId"}, new Object[]{channel.getIdLong()}, "fromDiscordId");
  }

  @Nullable
  private static OrgaTeam getTeamFromCategoryId(long channelId) {
    final TeamChannel channel = Database.Find.find(TeamChannel.class, new String[]{"discordId"}, new Object[]{channelId}, "fromDiscordId");
    return Util.avoidNull(channel, null, TeamChannel::getOrgaTeam);
  }

  public static OrgaTeam create(String name, String abbreviation, Integer id) {
    if (abbreviation == null) {
      abbreviation = "TRU" + Arrays.stream(name.split(" ")).map(word -> word.substring(0, 1).upper()).collect(Collectors.joining());
    }
    final String teamName = "TRUEsports " + name;
    final OrgaTeam orgaTeam = new OrgaTeam(teamName, abbreviation);

    final String roleName = "TRUE " + name;
    final PrimeTeam team = Util.avoidNull(id, null, TeamFactory::getTeam);
    Nunu.getInstance().getGuild().createRole().setName(roleName).setPermissions().setMentionable(true).setHoisted(true).queue(role -> {
      final CustomDiscordGroup discordGroup = new CustomDiscordGroup(role.getIdLong(), roleName, GroupType.PINGABLE, true, orgaTeam);
      orgaTeam.setGroup(discordGroup);
      orgaTeam.setTeam(team);
      Database.save(discordGroup);
      Database.save(orgaTeam);
    });
    final String categoryName = name + " (" + abbreviation + ")";
    createChannels(orgaTeam, categoryName);
    Database.connection().commit();
    return orgaTeam;
  }

  private static void createChannels(@NonNull OrgaTeam team, @NonNull String categoryName) {
    Nunu.getInstance().getGuild().createCategory(categoryName).queue(category -> {
      category.createTeamChannel(team);
      ChannelKind.TEXT.createTeamChannel(category, TeamChannelType.CHAT);
      ChannelKind.TEXT.createTeamChannel(category, TeamChannelType.INFO);
      ChannelKind.TEXT.createTeamChannel(category, TeamChannelType.SCOUTING);
      ChannelKind.VOICE.createTeamChannel(category, TeamChannelType.PRACTICE);
    });
  }
}
