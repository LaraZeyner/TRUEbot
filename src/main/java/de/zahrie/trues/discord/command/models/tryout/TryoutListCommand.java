package de.zahrie.trues.discord.command.models.tryout;

import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Column;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.DBQuery;
import de.zahrie.trues.api.discord.command.slash.annotations.Embed;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "tryouts", descripion = "Liste aller offenen Bewerbungen", perm = @Perm(PermissionRole.ORGA_MEMBER))
public class TryoutListCommand extends SlashCommand {
  @Override
  @Msg(value = "List der Bewerbungen", embeds = @Embed(queries = @DBQuery(query = "Application.current",
      columns = {@Column("Nutzer"), @Column("Rolle"), @Column("wartend")}, enumerated = true)))
  public boolean execute(SlashCommandInteractionEvent event) {
    return sendMessage();
  }
}
