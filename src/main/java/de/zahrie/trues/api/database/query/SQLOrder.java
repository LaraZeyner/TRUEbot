package de.zahrie.trues.api.database.query;

import de.zahrie.trues.util.StringUtils;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;

@Getter
@ExtensionMethod(StringUtils.class)
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
    if (getColumnName().contains("`") || getColumnName().toLowerCase().contains("count(") || getColumnName().toLowerCase().contains("avg(")) return getColumnName() + (descending ? " DESC" : "");
    final String name = getColumnName().contains(".") ? "`" + getColumnName().before(".") + "`.`" + getColumnName().after(".") + "`" : "`" + getColumnName() + "`";
    return name + (descending ? " DESC" : "");
  }
}
