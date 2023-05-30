package de.zahrie.trues.api.database.query;

import java.util.List;

import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.util.StringUtils;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(StringUtils.class)
public class JoinQuery<E extends Id, J extends Id> {
  private Class<E> targetClass;
  private Class<J> joinedClass;
  private String column;
  private JoinType joinType;
  private String alias;
  private Query<E> innerQuery;


  protected Class<E> getTargetClass() {
    return targetClass;
  }

  public Query<E> getInnerQuery() {
    return innerQuery;
  }

  public JoinQuery(Query<E> innerQuery) {
    this.innerQuery = innerQuery;
  }

  public JoinQuery(Class<E> targetClass, Class<J> joinedClass) {
    this.targetClass = targetClass;
    this.joinedClass = joinedClass;
    this.column = joinedClass.getAnnotation(Table.class).value();
    this.joinType = JoinType.INNER;
    this.alias = joinedClass.getSimpleName().toLowerCase();
  }

  /**
   * @param columnOrAlias Alias wenn mit _ gestartet
   */
  public JoinQuery(Class<E> targetClass, Class<J> joinedClass, String columnOrAlias) {
    this.targetClass = targetClass;
    this.joinedClass = joinedClass;
    this.column = columnOrAlias.startsWith("_") ? joinedClass.getAnnotation(Table.class).value() : columnOrAlias;
    this.joinType = JoinType.INNER;
    this.alias = (columnOrAlias.startsWith("_") && !columnOrAlias.contains(".")) ? columnOrAlias : joinedClass.getSimpleName().toLowerCase();
  }

  public JoinQuery(Class<E> targetClass, Class<J> joinedClass, JoinType joinType) {
    this.targetClass = targetClass;
    this.joinedClass = joinedClass;
    this.column = joinedClass.getAnnotation(Table.class).value();
    this.joinType = joinType;
    this.alias = joinedClass.getSimpleName().toLowerCase();
  }

  public JoinQuery(Class<E> targetClass, Class<J> joinedClass, String column, JoinType joinType) {
    this.targetClass = targetClass;
    this.joinedClass = joinedClass;
    this.column = column;
    this.joinType = joinType;
    this.alias = joinedClass.getSimpleName().toLowerCase();
  }

  public JoinQuery(Class<E> targetClass, Class<J> joinedClass, String column, String alias) {
    this.targetClass = targetClass;
    this.joinedClass = joinedClass;
    this.column = column;
    this.joinType = JoinType.INNER;
    this.alias = alias;
  }

  public JoinQuery(Class<E> targetClass, Class<J> joinedClass, JoinType joinType, String alias) {
    this.targetClass = targetClass;
    this.joinedClass = joinedClass;
    this.column = joinedClass.getAnnotation(Table.class).value();
    this.joinType = joinType;
    this.alias = alias;
  }

  public JoinQuery(Class<E> targetClass, Class<J> joinedClass, String column, JoinType joinType, String alias) {
    this.targetClass = targetClass;
    this.joinedClass = joinedClass;
    this.column = column;
    this.joinType = joinType;
    this.alias = alias;
  }

  @Override
  public String toString() {
    if (innerQuery != null) return innerQuery.query;
    final String joinedTableName = joinedClass.getAnnotation(Table.class).value();
    String joinedTableAlias = "_" + (this.alias.startsWith("_") ? this.alias.substring(1) : this.alias);
    if (column.contains(".")) joinedTableAlias = column.before(".");
    final String col = column.contains(".") ? column : "`_" + targetClass.getSimpleName().toLowerCase() + "`.`" + column + "`";
    return joinType.toString() + joinedTableName + "` as `" + joinedTableAlias + "` ON " + col + " = `" + joinedTableAlias +
        "`.`" + joinedTableName + "_id`";
  }

  public List<Object> getParams() {
    return innerQuery == null ? List.of() : innerQuery.additionalParameters;
  }

  public enum JoinType {
    INNER,
    OUTER,
    LEFT,
    RIGHT;

    @Override
    public String toString() {
      return " " + name() + " JOIN `";
    }
  }
}
