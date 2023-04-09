package de.zahrie.trues.api.coverage.season;

import de.zahrie.trues.api.database.QueryBuilder;
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
    return QueryBuilder.hql(PRMSeason.class, "FROM PRMSeason WHERE timing.startTime < NOW() ORDER BY timing.startTime DESC LIMIT 1")
        .single();
  }

  @Nullable
  public static PRMSeason getUpcomingPRMSeason() {
    return QueryBuilder.hql(PRMSeason.class,
        "FROM PRMSeason WHERE now() between timing.startTime and timing.endTime or timing.startTime > now() ORDER BY timing.startTime LIMIT 1").single();
  }

  @Nullable
  public static OrgaCupSeason getLastInternSeason() {
    return QueryBuilder.hql(OrgaCupSeason.class, "FROM OrgaCupSeason WHERE timing.startTime < NOW() ORDER BY timing.startTime DESC LIMIT 1")
        .single();
  }

  @Nullable
  public static OrgaCupSeason getUpcomingInternSeason() {
    return QueryBuilder.hql(OrgaCupSeason.class,
        "FROM OrgaCupSeason WHERE now() between timing.startTime and timing.endTime or timing.startTime > now() ORDER BY timing.startTime LIMIT 1").single();
  }


}
