package de.zahrie.trues.api.discord.user;

import de.zahrie.trues.api.community.application.Application;
import de.zahrie.trues.api.community.application.ApplicationFactory;
import de.zahrie.trues.api.community.application.TeamPosition;
import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.discord.group.RoleGranter;
import de.zahrie.trues.api.database.Database;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.Nullable;

public class DiscordUserFactory {
  @Nullable
  public static DiscordUser fromId(int id) {
    return QueryBuilder.hql(DiscordUser.class, "FROM DiscordUser WHERE id = " + id).single();
  }

  public static DiscordUser getDiscordUser(Member member) {
    final long memberId = member.getIdLong();
    final DiscordUser user = QueryBuilder.hql(DiscordUser.class, "FROM DiscordUser WHERE discordId = :discordId").addParameter("discordId", memberId).single();
    return user == null ? createDiscordUser(member) : user;
  }

  public static DiscordUser createDiscordUser(Member member) {
    final var user = new DiscordUser(member.getIdLong(), member.getAsMention());
    Database.insert(user);
    return user;
  }

  public static Application apply(DiscordUser user, TeamRole role, TeamPosition position, String appNotes) {
    return ApplicationFactory.create(user, role, position, appNotes, true);
  }

  public static void addOrgaRole(DiscordUser user, TeamRole role, TeamPosition position) {
    role = (role.equals(TeamRole.TRYOUT)) ? TeamRole.ORGA_TRYOUT : TeamRole.ORGA;
    getMembership(user, role, position);
    new RoleGranter(user).addOrgaRole(role, position);
  }

  public static void getMembership(DiscordUser user, TeamRole role, TeamPosition position) {
    Membership membership = user.getMemberships().stream().filter(msp -> msp.getPosition().equals(position)).findFirst().orElse(null);
    if (membership == null) {
      membership = Membership.build(user, position);
    }
    membership.setRole(role);
    membership.setPosition(position);
    membership.setActive(true);
    Database.update(membership);
  }
}
