package de.zahrie.trues.api.database.query;

import lombok.Getter;

@Getter
public class SQLOrder extends AbstractSQLField {
  private final boolean descending;

  public SQLOrder(String columnName) {
    this(columnName, false);
  }

  public SQLOrder(String columnName, boolean descending) {
    super(columnName);
    this.descending = descending;
  }

  @Override
  public String toString() {
    return getColumnName() + (descending ? " DESC" : "");
  }
}
