package de.zahrie.trues.discord.context.models;

import java.util.List;

import de.zahrie.trues.api.community.orgateam.OrgaTeamImpl;
import de.zahrie.trues.api.discord.command.context.Context;
import de.zahrie.trues.api.discord.command.context.ContextCommand;
import de.zahrie.trues.api.discord.command.context.UseView;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.community.member.MembershipFactory;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

@Context("Teammember entfernen")
@ExtensionMethod(OrgaTeamImpl.class)
public class TeamRemoveContext extends ContextCommand {
  @Override
  @Msg(value = "Der Nutzer wurde entfernt.")
  @UseView(ModalRegisterer.TEAM_REMOVE)
  public boolean execute(UserContextInteractionEvent event) {
    setPermission(o -> o.isEvenOrAbove(DiscordGroup.TEAM_CAPTAIN));

    final boolean advancedPermissions = getInvoker().isAbove(DiscordGroup.TEAM_CAPTAIN);
    if (advancedPermissions) return sendModal();
    final List<Membership> membership = MembershipFactory.getCaptainRoles(getInvoker());
    for (Membership member : membership) {
      final OrgaTeam orgaTeam = member.getOrgaTeam();
      orgaTeam.removeRole(getTarget());
      return sendMessage();
    }
    return reply("Internal Error");
  }
}
