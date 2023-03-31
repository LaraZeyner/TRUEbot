package de.zahrie.trues.discord.command.models;

import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.datatypes.symbol.StringExtention;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.api.community.betting.BetFactory;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "wetten", descripion = "Eine Wette auf ein Spiel abgeben", perm = @Perm(PermissionRole.EVERYONE), options = {
    @Option(name = "match", description = "kommende Matches", completion = "Match.upcoming.str"),
    @Option(name = "ergebnis", description = "Wette eingeben"),
    @Option(name = "menge", description = "Wettmenge eingeben")
})
@ExtensionMethod({BetFactory.class, StringExtention.class})
public class BetCommand extends SlashCommand {
  @Override
  @Msg(value = "Die Wette wurde abgegeben", error = "Du hast nicht genügend TRUEs")
  public boolean execute(SlashCommandInteractionEvent event) {
    final String matchString = find("match").string();
    final Integer matchId = matchString.between(null, ":").intValue();
    final Match match = Database.Find.find(Match.class, matchId);
    final String result = find("ergebnis").string();
    final Integer amount = find("menge").integer();
    return send(getInvoker().bet(match, result, amount));
  }
}
