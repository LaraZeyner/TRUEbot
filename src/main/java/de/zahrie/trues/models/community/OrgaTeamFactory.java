package de.zahrie.trues.models.community;


import de.zahrie.trues.database.Database;

public final class OrgaTeamFactory {
  public static OrgaTeam getTeamFromCategory(long categoryId) {
    return Database.Find.find(OrgaTeam.class, new String[]{"categoryId"}, new Object[]{categoryId}, "findCategory");
  }
}
