package de.zahrie.trues.dc.command;

import java.util.Objects;

import de.zahrie.trues.dc.util.cmd.SlashCommand;
import de.zahrie.trues.dc.util.cmd.annotations.Column;
import de.zahrie.trues.dc.util.cmd.annotations.Command;
import de.zahrie.trues.dc.util.cmd.annotations.DBQuery;
import de.zahrie.trues.dc.util.cmd.annotations.Embed;
import de.zahrie.trues.dc.util.cmd.annotations.Msg;
import de.zahrie.trues.dc.util.cmd.annotations.Option;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Created by Lara on 09.02.2023 for TRUEbot
 */
@Command(name = "test", descripion = "Dies ist ein Test", options = {
    @Option(name = "name", description = "Testname", choices = {"Hallo", "Welt"}),
    @Option(name = "teamname", description = "Teamname des Communityteams", completion = "Team.Orgateams.str")})
public class TestCommand extends SlashCommand {

  @Override
  @Msg(value = "Hi **{}**", embeds = {
      @Embed(description = "Team-Standings", queries =
      @DBQuery(query = "Team.Teaminfo", columns = {@Column("Teamname"), @Column("Division"), @Column("Score")}))})
  public void onCommand(SlashCommandInteractionEvent event) {
    final var name = Objects.requireNonNull(event.getOption("name")).getAsString();
    final var teamName = Objects.requireNonNull(event.getOption("teamname")).getAsString();

    send(event, name);
  }
}
