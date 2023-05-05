package de.zahrie.trues.api.database.query;

import de.zahrie.trues.api.database.connector.Table;

public class JoinQuery<E extends Id, J extends Id> {
  private Class<E> targetClass;
  private Class<J> joinedClass;
  private final String column;
  private final JoinType joinType;
  private final String alias;

  public JoinQuery() {
    this.column = joinedClass.getAnnotation(Table.class).value();
    this.joinType = JoinType.INNER;
    this.alias = joinedClass.getSimpleName().toLowerCase();
  }

  /**
   * @param columnOrAlias Alias wenn mit _ gestartet
   */
  public JoinQuery(String columnOrAlias) {
    this.column = columnOrAlias.startsWith("_") ? joinedClass.getAnnotation(Table.class).value() : columnOrAlias;
    this.joinType = JoinType.INNER;
    this.alias = (columnOrAlias.startsWith("_") && !columnOrAlias.contains(".")) ? columnOrAlias : joinedClass.getSimpleName().toLowerCase();
  }

  public JoinQuery(JoinType joinType) {
    this.column = joinedClass.getAnnotation(Table.class).value();
    this.joinType = joinType;
    this.alias = joinedClass.getSimpleName().toLowerCase();
  }

  public JoinQuery(String column, JoinType joinType) {
    this.column = column;
    this.joinType = joinType;
    this.alias = joinedClass.getSimpleName().toLowerCase();
  }

  public JoinQuery(String column, String alias) {
    this.column = column;
    this.joinType = JoinType.INNER;
    this.alias = alias;
  }

  public JoinQuery(JoinType joinType, String alias) {
    this.column = joinedClass.getAnnotation(Table.class).value();
    this.joinType = joinType;
    this.alias = alias;
  }

  public JoinQuery(String column, JoinType joinType, String alias) {
    this.column = column;
    this.joinType = joinType;
    this.alias = alias;
  }

  @Override
  public String toString() {
    final String targetTableName = targetClass.getAnnotation(Table.class).value();
    final String joinedTableName = joinedClass.getAnnotation(Table.class).value();
    return joinType.toString() + joinedTableName + " as _" + (alias.startsWith("_") ? alias.substring(1) : alias) +
        " ON " + targetTableName + "." + column +
        " = " + joinedTableName + "." + joinedTableName + "_id ";
  }

  public enum JoinType {
    INNER,
    OUTER,
    LEFT,
    RIGHT;

    @Override
    public String toString() {
      return name() + " JOIN";
    }
  }
}
