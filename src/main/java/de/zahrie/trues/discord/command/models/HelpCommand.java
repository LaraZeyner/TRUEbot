package de.zahrie.trues.discord.command.models;

import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.api.discord.ticket.Ticket;
import de.zahrie.trues.api.discord.ticket.Topic;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "help", descripion = "Schildere dein Anliegen", perm = @Perm(PermissionRole.EVERYONE), options = {
    @Option(name = "thema", description = "Wähle das Themengebiet", choices = {"Bugreport", "Featurerequest", "Nutzerreport"}),
    @Option(name = "titel", description = "Benenne dein Anliegen")
})
public class HelpCommand extends SlashCommand {
  @Override
  @Msg(value = "Ich habe dein Anliegen als Support-Ticket {} erstellt.")
  public boolean execute(SlashCommandInteractionEvent event) {
    final var thema = Topic.fromName(find("thema").string());
    final var title = find("titel").string();
    final Ticket ticket = new Ticket(getInvoker(), thema, title).create();
    return sendMessage(ticket.getChannel().getChannel().getAsMention());
  }
}
