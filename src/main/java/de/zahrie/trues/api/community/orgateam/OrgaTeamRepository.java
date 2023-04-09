package de.zahrie.trues.api.community.orgateam;

import de.zahrie.trues.api.database.QueryBuilder;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public class OrgaTeamRepository {
  @Nullable
  public static OrgaTeam getTeamFromName(@NonNull String name) {
    return QueryBuilder.hql(OrgaTeam.class, "FROM OrgaTeam WHERE nameCreation = " + name).single();
  }
}
