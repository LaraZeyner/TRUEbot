package de.zahrie.trues.api.coverage.season.signup;

import java.util.NoSuchElementException;

import de.zahrie.trues.api.coverage.season.PRMSeason;
import de.zahrie.trues.api.coverage.season.SeasonFactory;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import lombok.NonNull;

public class SignupFactory {
  @NonNull
  public static SeasonSignup create(PRMTeam team, String info) {
    final PRMSeason upcomingPRMSeason = SeasonFactory.getUpcomingPRMSeason();
    if (upcomingPRMSeason == null) throw new NoSuchElementException("Season fehlt!");

    final SeasonSignup signup = team.getSignupForSeason(upcomingPRMSeason);
    return signup != null ? signup : new SeasonSignup(upcomingPRMSeason, team, info).create();
  }
}
