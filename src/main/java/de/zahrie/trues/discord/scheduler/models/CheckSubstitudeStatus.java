package de.zahrie.trues.discord.scheduler.models;

import java.util.Comparator;

import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.community.member.MembershipFactory;
import de.zahrie.trues.api.database.query.Condition;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.group.RoleGranter;
import de.zahrie.trues.api.discord.user.DiscordUserGroup;
import de.zahrie.trues.api.scheduler.Schedule;
import de.zahrie.trues.api.scheduler.ScheduledTask;
import de.zahrie.trues.util.io.log.Console;

@Schedule(minute = "0")
public class CheckSubstitudeStatus extends ScheduledTask {
  @Override
  public void execute() {
    for (DiscordUserGroup toRemove : new Query<>(DiscordUserGroup.class).where("active", true)
        .and(Condition.notNull("permission_end")).and("permission_end > now()").entityList()) {
      toRemove.setActive(false);
      new RoleGranter(toRemove.getUser()).remove(toRemove.getDiscordGroup());
      MembershipFactory.getCurrentTeams(toRemove.getUser()).stream()
          .filter(om -> om.isActive() && om.getRole().equals(TeamRole.TRYOUT))
          .min(Comparator.comparing(Membership::getTimestamp))
          .ifPresent(om -> om.getOrgaTeam().getRoleManager().removeRole(toRemove.getUser()));
    }
    new Console("Substitute-Rollen bearbeitet").info();
  }
}
