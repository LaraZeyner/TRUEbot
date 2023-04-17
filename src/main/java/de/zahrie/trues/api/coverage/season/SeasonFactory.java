package de.zahrie.trues.api.coverage.season;

import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.util.Util;
import org.jetbrains.annotations.Nullable;

public final class SeasonFactory {

  @Nullable
  public static PRMSeason getSeason(int seasonId) {
    return QueryBuilder.hql(PRMSeason.class, "FROM PRMSeason WHERE prmId = " + seasonId).single();
  }

  @Nullable
  public static PRMSeason getSeason(String seasonName) {
    return QueryBuilder.hql(PRMSeason.class, "FROM PRMSeason WHERE fullName = " + seasonName).single();
  }

  @Nullable
  public static PRMSeason getLastPRMSeason() {
    return QueryBuilder.hql(PRMSeason.class, "FROM PRMSeason WHERE range.startTime < NOW() ORDER BY range.startTime DESC LIMIT 1")
        .single();
  }

  @Nullable
  public static PRMSeason getUpcomingPRMSeason() {
    return QueryBuilder.hql(PRMSeason.class,
        "FROM PRMSeason WHERE now() between range.startTime and range.endTime or range.startTime > now() ORDER BY range.startTime LIMIT 1").single();
  }

  @Nullable
  public static PRMSeason getCurrentPRMSeason() {
    final PRMSeason last = SeasonFactory.getLastPRMSeason();
    return Util.nonNull(last).getRange().hasEnded() ? getUpcomingPRMSeason() : last;
  }

  @Nullable
  public static OrgaCupSeason getLastInternSeason() {
    return QueryBuilder.hql(OrgaCupSeason.class, "FROM OrgaCupSeason WHERE range.startTime < NOW() ORDER BY range.startTime desc LIMIT 1")
        .single();
  }

  @Nullable
  public static OrgaCupSeason getUpcomingInternSeason() {
    return QueryBuilder.hql(OrgaCupSeason.class,
        "FROM OrgaCupSeason WHERE now() between range.startTime and range.endTime or range.startTime > now() ORDER BY range.startTime LIMIT 1").single();
  }

  @Nullable
  public static OrgaCupSeason getCurrentInternSeason() {
    final PRMSeason last = SeasonFactory.getLastPRMSeason();
    return Util.nonNull(last).getRange().hasEnded() ? getUpcomingInternSeason() : getLastInternSeason();
  }


}
