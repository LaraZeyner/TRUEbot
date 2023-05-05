package de.zahrie.trues.api.database.query;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UnknownFormatConversionException;
import java.util.stream.Collectors;

import de.zahrie.trues.api.database.connector.Listing;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.util.StringUtils;
import org.jetbrains.annotations.Nullable;

public abstract class SimpleAbstractQuery<T extends Id> {
  protected Class<T> targetId;
  protected Class<? extends Id> subEntity;
  protected final List<SQLField> fields = new ArrayList<>();
  protected final List<JoinQuery<?, ?>> joins = new ArrayList<>();
  protected final ConditionManager<T> conditionManager = new ConditionManager<>();
  protected SQLGroup group;
  protected SQLOrder order;
  protected Integer offset;
  protected int limit = 1000;
  protected final List<Union> unified = new ArrayList<>();

  public Query<T> key(String key, Object value) {
    return field(new SQLField.Key(key, value));
  }

  public Query<T> col(String key, Object value) {
    return field(SQLField.set(key, value));
  }

  protected Query<T> getAll(Class<? extends Id> subEntity) {
    this.subEntity = subEntity;
    return (Query<T>) this;
  }

  public Query<T> get(String columnName, Class<?> clazz) {
    return field(SQLField.get(columnName, clazz));
  }

  public Query<T> distinct(String columnName, Class<?> clazz) {
    return field(SQLField.distinct(columnName, clazz));
  }

  public Query<T> get(@Nullable String delimiter, Formatter... columnNames) {
    final String joinDelimiter = delimiter == null ? "', '" : "', '" + delimiter + "', '";
    final String columnName = Arrays.stream(columnNames).map(formatter -> formatter.toString(getTargetId().getSimpleName()))
        .collect(Collectors.joining(joinDelimiter, "CONCAT('", "')"));
    return get(columnName, String.class);
  }

  public Query<T> field(SQLField sqlField) {
    fields.add(sqlField);
    return (Query<T>) this;
  }

  public <E extends Id, J extends Id> Query<T> join(JoinQuery<E, J> joinSimpleQuery) {
    joins.add(joinSimpleQuery);
    return (Query<T>) this;
  }

  public Query<T> forId(int id) {
    return or(getTableName() + "_id", id);
  }

  public Query<T> where(Condition... conditions) {
    Arrays.stream(conditions).forEach(conditionManager::and);
    return (Query<T>) this;
  }

  public Query<T> where(String name, Object value) {
    conditionManager.and(Condition.compare(Condition.Comparer.EQUAL, name, value));
    return (Query<T>) this;
  }

  public Query<T> where(String query) {
    conditionManager.and(new Condition(query));
    return (Query<T>) this;
  }

  public Query<T> where(Condition.Comparer comparer, String name, Object value) {
    conditionManager.and(Condition.compare(comparer, name, value));
    return (Query<T>) this;
  }

  public Query<T> or(Condition condition) {
    conditionManager.or(condition);
    return (Query<T>) this;
  }

  public Query<T> or(String name, Object value) {
    conditionManager.or(Condition.compare(Condition.Comparer.EQUAL, name, value));
    return (Query<T>) this;
  }

  public Query<T> or(String query) {
    conditionManager.or(new Condition(query));
    return (Query<T>) this;
  }

  public Query<T> keep(Condition.Comparer comparer, String name, Object value) {
    conditionManager.keep(Condition.compare(comparer, name, value));
    return (Query<T>) this;
  }

  public Query<T> keep(String name, Object value) {
    conditionManager.keep(Condition.compare(Condition.Comparer.EQUAL, name, value));
    return (Query<T>) this;
  }

  public Query<T> and(String query) {
    conditionManager.and(new Condition(query));
    return (Query<T>) this;
  }

  public Query<T> and(Condition condition) {
    conditionManager.and(condition);
    return (Query<T>) this;
  }

  public Query<T> and(String name, Object value) {
    conditionManager.and(Condition.compare(Condition.Comparer.EQUAL, name, value));
    return (Query<T>) this;
  }

  public Query<T> and(Condition.Comparer comparer, String name, Object value) {
    conditionManager.and(Condition.compare(comparer, name, value));
    return (Query<T>) this;
  }

  public Query<T> groupBy(SQLGroup group) {
    this.group = group;
    return (Query<T>) this;
  }

  public Query<T> groupBy(String name) {
    this.group = new SQLGroup(name);
    return (Query<T>) this;
  }

  public Query<T> ascending(String column) {
    this.order = new SQLOrder(column, false);
    return (Query<T>) this;
  }

  public Query<T> descending(String column) {
    this.order = new SQLOrder(column, true);
    return (Query<T>) this;
  }

  public Query<T> offset(int offset) {
    this.offset = offset;
    return (Query<T>) this;
  }

  Query<T> limit(int limit) {
    this.limit = limit;
    return (Query<T>) this;
  }

  public Query<T> include(Query<?> query) {
    unified.add(new Union(query, Union.UnionType.UNION));
    return (Query<T>) this;
  }

  public Query<T> with(Query<?> query) {
    unified.add(new Union(query, Union.UnionType.INTERSECT));
    return (Query<T>) this;
  }

  public Query<T> exclude(Query<?> query) {
    unified.add(new Union(query, Union.UnionType.EXCEPT));
    return (Query<T>) this;
  }

  protected String getTableName() {
    return targetId.getAnnotation(Table.class).value();
  }

  protected String getDepartment() {
    final String department = targetId.getAnnotation(Table.class).department();
    return department.isBlank() ? null : department;
  }

  protected Class<T> getTargetId() {
    return targetId;
  }

  void setParams(PreparedStatement statement, Object... parameters) throws SQLException {
    boolean nextNull = false;
    int diff = 0;
    for (int i = 0; i < parameters.length; i++) {
      final Object parameter = parameters[i];
      final int pos = i - diff + 1;
      if (parameter instanceof Id id) statement.setInt(pos, id.getId());
      else if (parameter instanceof Enum<?> parameterEnum) {
        final Listing listing = parameterEnum.getClass().getAnnotation(Listing.class);
        if (listing == null) throw new IllegalArgumentException("Dieses Enum ist nicht zulässig.");

        switch (listing.value()) {
          case CUSTOM -> statement.setString(pos, parameterEnum.toString());
          case UPPER -> statement.setString(pos, parameterEnum.toString().toUpperCase());
          case LOWER -> statement.setString(pos, parameterEnum.toString().toLowerCase());
          case CAPITALIZE -> statement.setString(pos, StringUtils.capitalizeEnum(parameterEnum.toString().toLowerCase()));
          case ORDINAL -> statement.setByte(pos, (byte) (parameterEnum.ordinal() + listing.start()));
        }
        continue;
      }

      if (nextNull) {
        diff++;
        nextNull = false;
        statement.setNull(pos, (int) parameter);
      } else if (parameter == null) nextNull = true;
      else if (parameter instanceof LocalDateTime dateTime) statement.setTimestamp(pos, Timestamp.valueOf(dateTime));
      else if (parameter instanceof LocalDate date) statement.setDate(pos, Date.valueOf(date));
      else if (parameter instanceof String paramString) statement.setString(pos, paramString);
      else if (parameter instanceof Boolean paramBool) statement.setBoolean(pos, paramBool);
      else if (parameter instanceof Byte paramByte) statement.setByte(pos, paramByte);
      else if (parameter instanceof Short paramShort) statement.setShort(pos, paramShort);
      else if (parameter instanceof Integer paramInteger) statement.setInt(pos, paramInteger);
      else if (parameter instanceof Long paramLong) statement.setLong(pos, paramLong);
      else throw new UnknownFormatConversionException("Dieses Format ist nicht spezifiziert");
    }
  }

}
