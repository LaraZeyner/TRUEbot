package de.zahrie.trues.discord.command.models;

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

@Command(name = "wetten", descripion = "Eine Wette auf ein Spiel abgeben", perm = @Perm(PermissionRole.EVERYONE), options = {
    @Option(name = "match", description = "kommende Matches", completion = SelectionQueries.UPCOMING_MATCHES),
    @Option(name = "ergebnis", description = "Wette eingeben (zulässig: 0:0, 0-0, S, W, U, T, N, L)"),
    @Option(name = "menge", description = "Wettmenge eingeben")
})
@ExtensionMethod({BetFactory.class, StringUtils.class})
public class BetCommand extends SlashCommand {
  @Override
  @Msg(value = "Die Wette wurde abgegeben. Dir verbleiben {} TRUEs.", error = "Du hast nicht genügend TRUEs. Verfügbar: {} TRUEs")
  public boolean execute(SlashCommandInteractionEvent event) {
    final String matchString = find("match").string();
    final Integer matchId = matchString.before(" ").intValue();
    final Match match = new Query<>(Match.class).entity(matchId);
    final String result = determineResultString(find("ergebnis").string(), match);
    final MatchResult matchResult = MatchResult.fromResultString(result, match);
    assert matchResult != null;
    if (matchResult.getHomeScore() + matchResult.getGuestScore() != matchResult.getMaxGames())
      return reply("""
          Dieser Tipp ist nicht zulässig.
          Das Ergebnis kannst du beispielsweise mit **1:0** oder **1-0** tippen.
          Alternativ kannst du mit **S** / **W** auf Sieg, **U** / **T** auf Unentschieden oder **L** / **N** auf Niederlage tippen.
          Bedenke, dass auf diese Art und Weise immer eine X:0 Tipp eingetragen wird.
          """);

    final Integer amount = find("menge").integer();
    if (amount == null) return errorMessage(getInvoker().getPoints());

    return send(getInvoker().bet(match, result, amount), getInvoker().getPoints());
  }

  private String determineResultString(String result, Match match) {
    if (result.strip().matches("\\d+:\\d+")) return result;
    if (result.strip().matches("\\d+-\\d+")) return result.replace("-", ":");
    return switch (result) {
      case "S", "W" -> match.getFormat().ordinal() + ":0";
      case "L", "N" -> "0:" + match.getFormat().ordinal();
      case "T", "U" -> match.getFormat().ordinal() % 2 == 0 ? "0:0" : match.getFormat().ordinal() / 2 + ":" + match.getFormat().ordinal() / 2;
      default -> "0:0";
    };
  }
}
