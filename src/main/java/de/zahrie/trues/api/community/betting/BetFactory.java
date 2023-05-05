package de.zahrie.trues.api.community.betting;

import de.zahrie.trues.api.coverage.match.model.AMatch;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.util.Util;

public class BetFactory {
  public static boolean bet(DiscordUser user, Match match, String outcome, int amount) {
    final Bet currentBet = getBet(user, match);
    final int remainingPoints = user.getPoints() + Util.avoidNull(currentBet, 0, Bet::getAmount) - amount;
    if (remainingPoints < 0) return false;

    user.setPoints(remainingPoints);
    new Bet(match, user, outcome, amount).create();
    return true;
  }

  public static Bet getBet(DiscordUser user, AMatch AMatch) {
    return new Query<Bet>().where("discord_user", user).and("coverage", AMatch).entity();
  }
}
