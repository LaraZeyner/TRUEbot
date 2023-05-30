package de.zahrie.trues.api.discord.channel;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.orgateam.OrgaTeamFactory;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannel;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelRepository;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.util.Nunu;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import org.jetbrains.annotations.Nullable;

public interface ADiscordChannel {
  long getDiscordId();

  DiscordChannelType getChannelType();

  String getName();

  void setName(String name);

  ChannelType getPermissionType();

  void setPermissionType(ChannelType permissionType);

  default void updatePermissions() {
    final IPermissionContainer channel = getChannel();
    for (ChannelType.APermissionOverride permission : getPermissionType().getPermissions()) {
      final IPermissionHolder permissionHolder = determineHolder(channel, permission.permissionHolder());
      if (permissionHolder == null) continue;

      channel.getManager().putPermissionOverride(permissionHolder, permission.getAllowed(), permission.getDenied()).queue();
    }
  }

  default void updateForGroup(DiscordGroup group) {
    final IPermissionHolder holder = group.equals(DiscordGroup.TEAM_ROLE_PLACEHOLDER) ?
        detemineRoleOfChannel((ICategorizableChannel) getChannel()) : determineHolder(getChannel(), group.getRole());
    final ChannelType.APermissionOverride permission = getPermissionType().getTeamPermission();
    if (holder == null) return;

    getChannel().getManager().putPermissionOverride(holder, permission.getAllowed(), permission.getDenied()).queue();
  }

  @Nullable
  private static IPermissionHolder determineHolder(IPermissionContainer channel, IPermissionHolder holder) {
    return holder instanceof Role role && OrgaTeamFactory.isRoleOfTeam(role) && channel instanceof ICategorizableChannel categorizableChannel ?
        detemineRoleOfChannel(categorizableChannel) : holder;
  }

  @Nullable
  private static Role detemineRoleOfChannel(ICategorizableChannel categorizableChannel) {
    assert categorizableChannel.getParentCategory() != null;
    final TeamChannel teamChannel = TeamChannelRepository.getTeamChannelFromChannel(categorizableChannel.getParentCategory());
    if (teamChannel == null) return null;

    final OrgaTeam team = teamChannel.getOrgaTeam();
    if (team == null) return null;

    return team.getRoleManager().getRole();
  }

  default IPermissionContainer getChannel() {
    return (IPermissionContainer) Nunu.DiscordChannel.getChannel(getDiscordId());
  }
}
