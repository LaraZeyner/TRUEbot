package de.zahrie.trues.discord.context.models;

import java.util.List;

import de.zahrie.trues.api.discord.command.context.Context;
import de.zahrie.trues.api.discord.command.context.ContextCommand;
import de.zahrie.trues.api.discord.command.context.UseView;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import de.zahrie.trues.models.community.OrgaTeam;
import de.zahrie.trues.models.community.application.OrgaMember;
import de.zahrie.trues.models.community.application.OrgaMemberFactory;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

@Context("Teammember entfernen")
public class TeamRemoveContext extends ContextCommand {
  @Override
  @Msg(value = "Der Nutzer wurde entfernt.")
  @UseView(ModalRegisterer.TEAM_REMOVE)
  public boolean execute(UserContextInteractionEvent event) {
    setPermission(o -> o.isEvenOrAbove(DiscordGroup.TEAM_CAPTAIN));

    final boolean advancedPermissions = getInvoker().isAbove(DiscordGroup.TEAM_CAPTAIN);
    if (advancedPermissions) return sendModal();
    final List<OrgaMember> orgaMember = OrgaMemberFactory.getCaptainRoles(getInvoker());
    for (OrgaMember member : orgaMember) {
      final OrgaTeam orgaTeam = member.getOrgaTeam();
      orgaTeam.removeRole(getTarget());
      return sendMessage();
    }
    return reply("Internal Error");
  }
}
