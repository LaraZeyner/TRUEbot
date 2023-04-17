package de.zahrie.trues.api.community.member;

import java.util.List;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.application.TeamPosition;
import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.database.Database;

public class MembershipFactory {

  public static Membership getMember(OrgaTeam team, DiscordUser user, TeamRole role, TeamPosition position) {
    Membership membership = getMember(team, user);
    if (membership == null) {
      membership = Membership.build(user, team, role, position);
    }
    membership.setRole(role);
    membership.setPosition(position);
    membership.setActive(true);
    Database.update(membership);
    return membership;
  }
  public static List<Membership> getCurrentTeams(DiscordUser user) {
    return QueryBuilder.hql(Membership.class, "FROM Membership WHERE user = :user AND active = true")
        .addParameter("user", user).list();
  }

  public static Membership getMostImportantTeam(DiscordUser user) {
    return getCurrentTeams(user).stream().sorted().findFirst().orElse(null);
  }

  public static Membership getMembershipOf(DiscordUser user, OrgaTeam team) {
    return getCurrentTeams(user).stream().filter(member1 -> member1.getOrgaTeam().equals(team)).findFirst().orElse(null);
  }

  public static Membership getOfTeam(OrgaTeam team, TeamRole role, TeamPosition position) {
    return team.getMemberships().stream().filter(member -> member.getRole().equals(role))
        .filter(member -> member.getPosition().equals(position)).findFirst().orElse(null);
  }

  public static List<Membership> getOfPosition(TeamPosition position) {

    return QueryBuilder.hql(Membership.class,
        "FROM Membership WHERE position = :position and active = true")
        .addParameter("position", position).list();
  }

  public static Membership getMember(OrgaTeam orgaTeam, DiscordUser user) {
    return orgaTeam.getMemberships().stream().filter(membership -> membership.getUser().equals(user)).findFirst().orElse(null);
  }

  public static List<Membership> getCaptainRoles(DiscordUser user) {
    return QueryBuilder.hql(Membership.class, "FROM Membership WHERE user = :user AND active = true AND captain = true")
        .addParameter("user", user).list();
  }
}
