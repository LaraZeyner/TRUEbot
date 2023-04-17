package de.zahrie.trues.api.community.betting;

import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.util.Util;

public class BetFactory {
  public static boolean bet(DiscordUser user, Match match, String outcome, int amount) {
    Bet currentBet = getBet(user, match);
    final int remainingPoints = user.getPoints() + Util.avoidNull(currentBet, 0, Bet::getAmount) - amount;
    if (remainingPoints < 0) return false;
    user.setPoints(remainingPoints);
    if (currentBet == null) {
      currentBet = new Bet(match, user, outcome, amount);
      Database.insert(currentBet);
    } else {
      currentBet.setAmount(amount);
      currentBet.setOutcome(outcome);
      Database.update(currentBet);
    }
    Database.update(user);

    return true;
  }

  public static Bet getBet(DiscordUser user, Match match) {
    return QueryBuilder.hql(Bet.class, "FROM Bet WHERE user = " + user.getId() + " AND match = " + match.getId()).single();
  }
}
