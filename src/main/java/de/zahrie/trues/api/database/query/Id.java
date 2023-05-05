package de.zahrie.trues.api.database.query;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.database.connector.Table;

public interface Id {
  int getId();
  void setId(int id);

  default <U> void updateId(Class<U> clazz, int id) {
    setId(id);
    final String tableName = clazz.getAnnotation(Table.class).value();
    new Query<OrgaTeam>().col(tableName + "_id", id).update(id);
  }
}
