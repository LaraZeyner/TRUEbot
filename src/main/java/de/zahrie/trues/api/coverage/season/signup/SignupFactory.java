package de.zahrie.trues.api.coverage.season.signup;

import java.util.NoSuchElementException;

import de.zahrie.trues.api.coverage.season.PRMSeason;
import de.zahrie.trues.api.coverage.season.SeasonFactory;
import de.zahrie.trues.api.coverage.team.model.Team;
import lombok.NonNull;

public class SignupFactory {
  @NonNull
  public static SeasonSignup create(Team team, String info) {
    final PRMSeason upcomingPRMSeason = SeasonFactory.getUpcomingPRMSeason();
    if (upcomingPRMSeason == null) throw new NoSuchElementException("Season fehlt!");
    SeasonSignup signup = team.getSignupForSeason(upcomingPRMSeason);
    if (signup == null) signup = SeasonSignup.build(upcomingPRMSeason, team, info);
    return signup;
  }
}
