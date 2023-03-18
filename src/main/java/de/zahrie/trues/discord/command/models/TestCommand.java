package de.zahrie.trues.discord.command.models;

import java.util.Objects;

import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.api.discord.command.slash.annotations.Embed;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Column;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.DBQuery;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "test", descripion = "Dies ist ein Test", perm = @Perm(PermissionRole.EVERYONE), options = {
    @Option(name = "name", description = "Testname", choices = {"Hallo", "Welt"}),
    @Option(name = "teamname", description = "Teamname des Communityteams", completion = "Team.Orgateams.str")
})
public class TestCommand extends SlashCommand {

  @Override
  @Msg(value = "Hi **{}**", embeds = {
      @Embed(description = "Team-Standings", queries =
      @DBQuery(query = "PrimeTeam.Teaminfo", columns = {@Column("Teamname"), @Column("Division"), @Column("Score")}))})
  public boolean execute(SlashCommandInteractionEvent event) {
    final var name = Objects.requireNonNull(event.getOption("name")).getAsString();
    return sendMessage(name);
  }
}
