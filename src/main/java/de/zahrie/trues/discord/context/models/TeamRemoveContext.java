package de.zahrie.trues.discord.context.models;

import java.util.List;

import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.community.member.MembershipFactory;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.discord.command.context.Context;
import de.zahrie.trues.api.discord.command.context.ContextCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

@Context("Teammember entfernen")
@Deprecated(forRemoval = true)
public class TeamRemoveContext extends ContextCommand {
  @Override
  @Msg(value = "Der Nutzer wurde entfernt.")
  //@UseView(ModalRegisterer.TEAM_REMOVE)
  public boolean execute(UserContextInteractionEvent event) {
    setPermission(o -> o.isEvenOrAbove(DiscordGroup.TEAM_CAPTAIN));

    final boolean advancedPermissions = getInvoker().isAbove(DiscordGroup.TEAM_CAPTAIN);
    if (advancedPermissions) return sendModal();
    final List<Membership> membership = MembershipFactory.getCaptainRoles(getInvoker());
    for (Membership member : membership) {
      final OrgaTeam orgaTeam = member.getOrgaTeam();
      orgaTeam.getRoleManager().removeRole(getTarget());
      return sendMessage();
    }
    return reply("Internal Error");
  }
}
