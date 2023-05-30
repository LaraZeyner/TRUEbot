package de.zahrie.trues.discord.command.models;

import de.zahrie.trues.api.community.betting.BetFactory;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.io.SelectionQueries;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "wetten", descripion = "Eine Wette auf ein Spiel abgeben", perm = @Perm(PermissionRole.EVERYONE), options = {
    @Option(name = "match", description = "kommende Matches", completion = SelectionQueries.UPCOMING_MATCHES),
    @Option(name = "ergebnis", description = "Wette eingeben"),
    @Option(name = "menge", description = "Wettmenge eingeben")
})
@ExtensionMethod({BetFactory.class, StringUtils.class})
public class BetCommand extends SlashCommand {
  @Override
  @Msg(value = "Die Wette wurde abgegeben", error = "Du hast nicht genügend TRUEs")
  public boolean execute(SlashCommandInteractionEvent event) {
    final String matchString = find("match").string();
    final Integer matchId = matchString.before(":").intValue();
    final Match match = new Query<>(Match.class).entity(matchId);
    final String result = find("ergebnis").string();
    final Integer amount = find("menge").integer();
    return send(getInvoker().bet(match, result, amount));
  }
}
