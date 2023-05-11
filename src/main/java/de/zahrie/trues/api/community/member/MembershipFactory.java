package de.zahrie.trues.api.community.member;

import java.util.List;

import de.zahrie.trues.api.community.application.TeamPosition;
import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.user.DiscordUser;

public class MembershipFactory {

  public static Membership getMember(OrgaTeam team, DiscordUser user, TeamRole role, TeamPosition position) {
    Membership membership = getMember(team, user);
    if (membership == null) {
      membership = new Membership(user, team, role, position).create();
    }
    membership.updateRoleAndPosition(role, position);
    return membership;
  }
  public static List<Membership> getCurrentTeams(DiscordUser user) {
    return new Query<>(Membership.class).where("discord_user", user).and("active", true).entityList();
  }

  public static Membership getMostImportantTeam(DiscordUser user) {
    return getCurrentTeams(user).stream().sorted().findFirst().orElse(null);
  }

  public static Membership getMembershipOf(DiscordUser user, OrgaTeam team) {
    return getCurrentTeams(user).stream().filter(member1 -> member1.getOrgaTeam().equals(team)).findFirst().orElse(null);
  }

  public static List<Membership> getOfPosition(TeamPosition position) {
    return new Query<>(Membership.class).where("position", position).and("active", true).entityList();
  }

  public static Membership getMember(OrgaTeam orgaTeam, DiscordUser user) {
    return new Query<>(Membership.class).where("orga_team", orgaTeam).and("discord_user", user).entity();
  }

  public static List<Membership> getCaptainRoles(DiscordUser user) {
    return new Query<>(Membership.class).where("discord_user", user).and("active", true).and("captain", true).entityList();
  }
}
