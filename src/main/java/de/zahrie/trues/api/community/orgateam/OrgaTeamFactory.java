package de.zahrie.trues.api.community.orgateam;


import java.util.Arrays;
import java.util.stream.Collectors;

import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannel;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelRepository;
import de.zahrie.trues.api.coverage.team.TeamFactory;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.api.discord.group.CustomDiscordGroup;
import de.zahrie.trues.api.discord.group.GroupType;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.util.Util;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jetbrains.annotations.Nullable;

@ExtensionMethod(StringUtils.class)
public final class OrgaTeamFactory {
  @Nullable
  public static OrgaTeam getTeamFromChannel(@NonNull GuildChannel channel) {
    if (channel instanceof ICategorizableChannel categorizableChannel) {
      final Category category = categorizableChannel.getParentCategory();
      if (category != null) return getTeamFromCategoryId(category);
    }
    return getTeamFromCategoryId(channel);
  }

  @Nullable
  private static OrgaTeam getTeamFromCategoryId(@NonNull GuildChannel channel) {
    final TeamChannel teamChannel = TeamChannelRepository.getTeamChannelFromChannel(channel);
    return Util.avoidNull(teamChannel, null, TeamChannel::getOrgaTeam);
  }

  public static OrgaTeam create(String name, String abbreviation, Integer id) {
    if (abbreviation == null) {
      abbreviation = "TRU" + Arrays.stream(name.split(" ")).map(word -> word.substring(0, 1).upper()).collect(Collectors.joining());
    }
    final String teamName = "TRUEsports " + name;
    final OrgaTeam orgaTeam = new OrgaTeam(teamName, abbreviation);

    final String roleName = "TRUE " + name;
    final PRMTeam team = Util.avoidNull(id, null, TeamFactory::getTeam);
    Nunu.getInstance().getGuild().createRole().setName(roleName).setPermissions().setMentionable(true).setHoisted(true).queue(role -> {
      final CustomDiscordGroup discordGroup = new CustomDiscordGroup(role.getIdLong(), roleName, GroupType.PINGABLE, true, orgaTeam);
      orgaTeam.setGroup(discordGroup);
      orgaTeam.setTeam(team);
      Database.save(discordGroup);
      Database.save(orgaTeam);
    });
    final String categoryName = name + " (" + abbreviation + ")";
    OrgaTeamImpl.createChannels(orgaTeam, categoryName);
    Database.connection().commit();
    return orgaTeam;
  }
}
