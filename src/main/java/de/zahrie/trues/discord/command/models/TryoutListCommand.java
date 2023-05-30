package de.zahrie.trues.discord.command.models;

import de.zahrie.trues.api.discord.builder.queryCustomizer.NamedQuery;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "tryouts", descripion = "Liste aller offenen Bewerbungen", perm = @Perm(PermissionRole.ORGA_MEMBER))
public class TryoutListCommand extends SlashCommand {
  @Override
  @Msg(value = "Liste der Bewerbungen", description = "Zeigt eine Liste aller offenen Bewerbungen")
  public boolean execute(SlashCommandInteractionEvent event) {
    addEmbed(NamedQuery.TRYOUTS);
    return sendMessage();
  }
}
