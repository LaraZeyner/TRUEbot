package de.zahrie.trues.api.discord.user;

import de.zahrie.trues.api.community.application.Application;
import de.zahrie.trues.api.community.application.ApplicationFactory;
import de.zahrie.trues.api.community.application.TeamPosition;
import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.discord.group.RoleGranter;
import de.zahrie.trues.database.Database;
import net.dv8tion.jda.api.entities.Member;

public class DiscordUserFactory {
  public static DiscordUser getDiscordUser(Member member) {
    final long memberId = member.getIdLong();
    return Database.Find.find(DiscordUser.class, memberId);
  }

  public static Application apply(DiscordUser user, TeamRole role, TeamPosition position, String appNotes) {
    return ApplicationFactory.create(user, role, position, appNotes, true);
  }

  public static void addOrgaRole(DiscordUser user, TeamRole role, TeamPosition position) {
    role = (role.equals(TeamRole.TRYOUT)) ? TeamRole.ORGA_TRYOUT : TeamRole.ORGA;
    getMember(user, role, position);
    new RoleGranter(user).addOrgaRole(role, position);
  }

  public static Membership getMember(DiscordUser user, TeamRole role, TeamPosition position) {
    Membership membership = user.getMemberships().stream().filter(msp -> msp.getPosition().equals(position)).findFirst().orElse(null);
    if (membership == null) {
      membership = new Membership(user, position);
    }
    membership.setRole(role);
    membership.setPosition(position);
    membership.setActive(true);
    Database.save(membership);
    return membership;
  }
}
