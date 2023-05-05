package de.zahrie.trues.api.coverage.season;

import de.zahrie.trues.api.database.query.Condition;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.util.Util;
import org.jetbrains.annotations.Nullable;

public final class SeasonFactory {

  @Nullable
  public static PRMSeason getSeason(int seasonId) {
    return new Query<PRMSeason>().where("season_id", seasonId).entity();
  }

  @Nullable
  public static PRMSeason getSeason(String seasonName) {
    return new Query<PRMSeason>().where("season_name", seasonName).entity();

  }

  @Nullable
  public static PRMSeason getLastPRMSeason() {
    return new Query<PRMSeason>().where("season_start <= now()").descending("season_start").entity();
  }

  @Nullable
  public static PRMSeason getUpcomingPRMSeason() {
    return new Query<PRMSeason>()
        .where(Condition.between("now", "season_start", "season_end")).or("season_start >= now()")
        .ascending("season_start").entity();
  }

  @Nullable
  public static PRMSeason getCurrentPRMSeason() {
    final PRMSeason last = getLastPRMSeason();
    return Util.nonNull(last).getRange().hasEnded() ? getUpcomingPRMSeason() : last;
  }

  @Nullable
  public static OrgaCupSeason getLastInternSeason() {
    return new Query<OrgaCupSeason>().where("season_start <= now()").descending("season_start").entity();
  }

  @Nullable
  public static OrgaCupSeason getUpcomingInternSeason() {
    return new Query<OrgaCupSeason>()
        .where(Condition.between("now", "season_start", "season_end")).or("season_start >= now()")
        .ascending("season_start").entity();
  }

  @Nullable
  public static OrgaCupSeason getCurrentInternSeason() {
    final PRMSeason last = getLastPRMSeason();
    return Util.nonNull(last).getRange().hasEnded() ? getUpcomingInternSeason() : getLastInternSeason();
  }
}
