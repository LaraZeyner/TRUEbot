package de.zahrie.trues.discord.command.models;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.zahrie.trues.api.community.betting.Bet;
import de.zahrie.trues.api.community.betting.BetFactory;
import de.zahrie.trues.api.coverage.match.MatchResult;
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

@Command(name = "quoten", descripion = "Wettquoten für ein Spiel ansehen", perm = @Perm(PermissionRole.EVERYONE), options = {
    @Option(name = "match", description = "kommende Matches", completion = SelectionQueries.UPCOMING_MATCHES)
})
@ExtensionMethod({BetFactory.class, StringUtils.class})
public class QuotesCommand extends SlashCommand {
  @Override
  @Msg
  public boolean execute(SlashCommandInteractionEvent event) {
    final String matchString = find("match").string();
    final Integer matchId = matchString.before(" ").intValue();
    final Match match = new Query<>(Match.class).entity(matchId);
    final List<Object[]> list = new Query<>(Bet.class).get("bet_outcome", String.class)
        .where("coverage", match).groupBy("bet_outcome").descending("bet_outcome").list();
    final String data = list.stream().map(objects -> "Quote für **" + objects[0] + "**: " + Math.round(100 * BetFactory.quote(Objects.requireNonNull(MatchResult.fromResultString((String) objects[0], match)))) / 100. + "x").collect(Collectors.joining("\n"));
    return list.isEmpty() ? reply("**aktuell sind keine Quoten bekannt.") : reply("**Quoten für das Match " + match.toString() + "**\n" + data);
  }
}
