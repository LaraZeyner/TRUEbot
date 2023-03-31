package de.zahrie.trues.api.community.betting;

import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.util.Util;

public class BetFactory {
  public static boolean bet(DiscordUser user, Match match, String outcome, int amount) {
    Bet currentBet = getBet(user, match);
    final int remainingPoints = user.getPoints() + Util.avoidNull(currentBet, 0, Bet::getAmount) - amount;
    if (remainingPoints < 0) return false;
    user.setPoints(remainingPoints);
    if (currentBet == null) currentBet = new Bet(match, user, outcome, amount);
    currentBet.setAmount(amount);
    currentBet.setOutcome(outcome);
    Database.save(currentBet);
    Database.save(user);
    return true;
  }

  public static Bet getBet(DiscordUser user, Match match) {
    return Database.Find.find(Bet.class, new String[]{"user", "match"}, new Object[]{user, match}, "findByUserAndMatch");
  }
}
