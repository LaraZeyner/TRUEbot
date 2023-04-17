package de.zahrie.trues.discord.scheduler.models;

import java.util.Comparator;

import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.community.member.MembershipFactory;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.discord.group.RoleGranter;
import de.zahrie.trues.api.discord.user.DiscordUserGroup;
import de.zahrie.trues.api.scheduler.Schedule;
import de.zahrie.trues.api.scheduler.ScheduledTask;

@Schedule(minute = "0")
public class CheckSubstitudeStatus extends ScheduledTask {
  @Override
  public void execute() {
    for (DiscordUserGroup toRemove : QueryBuilder.hql(DiscordUserGroup.class, "FROM DiscordUserGroup WHERE isActive = true AND range.endTime is not null AND range.endTime > NOW()").list()) {
      toRemove.setActive(false);
      new RoleGranter(toRemove.getUser()).remove(toRemove.getDiscordGroup());
      MembershipFactory.getCurrentTeams(toRemove.getUser()).stream()
          .filter(om -> om.isActive() && om.getRole().equals(TeamRole.TRYOUT))
          .min(Comparator.comparing(Membership::getTimestamp))
          .ifPresent(om -> om.getOrgaTeam().getRoleManager().removeRole(toRemove.getUser()));
    }
  }
}
