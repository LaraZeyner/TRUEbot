package de.zahrie.trues.api.community.orgateam;

import de.zahrie.trues.api.community.application.TeamPosition;
import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.community.member.MembershipFactory;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelType;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.discord.channel.ChannelKind;
import de.zahrie.trues.api.discord.channel.DiscordChannelFactory;
import de.zahrie.trues.api.discord.group.RoleGranter;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.util.Nunu;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({MembershipFactory.class, DiscordChannelFactory.class})
public class OrgaTeamImpl {

  public static void addRole(OrgaTeam team, DiscordUser user, TeamRole role, TeamPosition position) {
    if (role.equals(TeamRole.MAIN)) checkMainOnPosition(team, position);
    final Membership member = team.getMember(user, role, position);
    new RoleGranter(member.getUser()).addTeamRole(role, position, team);
  }

  private static void checkMainOnPosition(OrgaTeam team, TeamPosition position) {
    final Membership mainOnPosition = team.getOfTeam(TeamRole.MAIN, position);
    if (mainOnPosition != null) {
      mainOnPosition.setRole(TeamRole.SUBSTITUDE);
      Database.save(mainOnPosition);
    }
  }

  public static void addCaptain(OrgaTeam team, DiscordUser user) {
    final Membership membership = MembershipFactory.getMembershipOf(user, team);
    membership.setCaptain(true);
    Database.save(membership);
    new RoleGranter(user).handleCaptain(true);
  }

  public static void removeCaptain(OrgaTeam team, DiscordUser user) {
    final Membership membership = MembershipFactory.getMembershipOf(user, team);
    membership.setCaptain(false);
    Database.save(membership);
    new RoleGranter(user).handleCaptain(false);
  }

  public static void removeRole(OrgaTeam team, DiscordUser user) {
    final Membership membership = MembershipFactory.getMembershipOf(user, team);
    if (membership == null) return;
    membership.setActive(false);
    Database.save(membership);
    new RoleGranter(user).removeTeamRole(membership, team);
  }

  static void createChannels(@NonNull OrgaTeam team, @NonNull String categoryName) {
    Nunu.getInstance().getGuild().createCategory(categoryName).queue(category -> {
      category.createTeamChannel(team);
      ChannelKind.TEXT.createTeamChannel(category, TeamChannelType.CHAT);
      ChannelKind.TEXT.createTeamChannel(category, TeamChannelType.INFO);
      ChannelKind.TEXT.createTeamChannel(category, TeamChannelType.SCOUTING);
      ChannelKind.VOICE.createTeamChannel(category, TeamChannelType.PRACTICE);
    });
  }
}
