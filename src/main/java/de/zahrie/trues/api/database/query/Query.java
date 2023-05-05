package de.zahrie.trues.api.database.query;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UnknownFormatConversionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.zahrie.trues.api.database.connector.Database;
import de.zahrie.trues.api.database.connector.Listing;
import de.zahrie.trues.api.database.connector.SQLUtils;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.datatypes.collections.SortedList;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

@Log
public class Query<T extends Id> extends SimpleQueryFormer<T> {
  public Query() {
    super(null);
  }

  public Query(int amount) {
    super(null);
    limit(amount);
  }

  public Query(String query) {
    super(query);
  }

  public static int update(String query) {
    return update(query, List.of());
  }

  public static int update(String query, List<Object> parameters) {
    try (final PreparedStatement statement = Database.connection().getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      for (int i = 0; i < parameters.size(); i++) statement.setObject(i + 1, parameters.get(i));
      return statement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Füge Eintrag in die Datenbank ein
   *
   * @return Entity für Chaining
   */
  public T insert(T entity, List<Object> parameters) {
    return insert(entity, t -> null, t -> null, parameters);
  }

  public T insert(T entity) {
    return insert(entity, t -> null, t -> null, List.of());
  }

  /**
   * Füge Eintrag in die Datenbank ein
   *
   * @param action Wenn erfolgreich hinzugefügt (ohne Id)
   * @return Entity für Chaining
   */
  public <R> T insert(T entity, Function<T, R> action, List<Object> parameters) {
    return insert(entity, action, t -> null, parameters);
  }

  public <R> T insert(T entity, Function<T, R> action) {
    return insert(entity, action, t -> null, List.of());
  }

  /**
   * Füge Eintrag in die Datenbank ein
   *
   * @param action Wenn erfolgreich hinzugefügt (ohne Id)
   * @param otherwise Wenn nicht hinzugefügt
   * @return Entity für Chaining
   */
  public <R> T insert(T entity, Function<T, R> action, Function<T, R> otherwise, List<Object> parameters) {
    if (action == null) action = t -> null;
    return executeUpdate(insertString(), true, entity, action, otherwise, parameters);
  }

  public <R> T insert(T entity, Function<T, R> action, Function<T, R> otherwise) {
    if (action == null) action = t -> null;
    return executeUpdate(insertString(), true, entity, action, otherwise, List.of());
  }

  public void update(List<Object> parameters) {
    executeUpdate(updateString(), false, null, t -> null, t -> null, parameters);
  }

  public void update(int id) {
    forId(id).update(List.of());
  }

  public void update(int id, List<Object> parameters) {
    forId(id).update(parameters);
  }

  public void delete(int id) {
    forId(id).delete(List.of());
  }

  public void delete(int id, List<Object> parameters) {
    forId(id).delete(parameters);
  }

  public void delete(List<Object> parameters) {
    executeUpdate(deleteString(), false, null, t -> null, t -> null, parameters);
  }

  private <R> T executeUpdate(String query, boolean insert, T entity, Function<T, R> action, Function<T, R> otherWise, List<Object> parameters) {
    final Connection connection = Database.connection().getConnection();
    try (final PreparedStatement statement = insert ? connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS) : connection.prepareStatement(query)) {
      setValues(statement, parameters);
      statement.executeUpdate();
      if (entity == null) return null;

      try (final ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) { // id != 0
          final int id = generatedKeys.getInt(1);
          if (entity.getId() != 0) log.severe("Id existiert bereits - Skipping");
          else {
            entity.setId(id);
            action.apply(entity);
          }
        } else otherWise.apply(entity);
      }
      return entity;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public Object[] single() {
    final List<Object[]> results = list(1);
    return results.isEmpty() ? null : results.get(0);
  }

  public List<Object[]> list(int limit) {
    return limit(limit).list();
  }

  public List<Object[]> list() {
    return limit(limit).list(List.of());
  }

  public List<Object[]> list(List<Object> parameters) {
    List<? extends Class<?>> clazzes = new ArrayList<>(fields.stream().filter(sqlField -> sqlField instanceof SQLReturnField).map(o -> (SQLReturnField) o)
        .map(SQLReturnField::getReturnType).toList());

    try (final PreparedStatement statement = Database.connection().getConnection().prepareStatement(query == null ? selectString() : query)) {
      setValues(statement, parameters);
      final List<Object[]> out = new ArrayList<>();
      try (final ResultSet resultSet = statement.executeQuery()) {
        clazzes = adjust(clazzes, statement);

        while (resultSet.next()) {
          final Object[] data = getRow(resultSet, clazzes);
          out.add(data);
          if (out.size() > limit) break;
        }
      }
      return out;
    } catch (SQLException exception) {
      throw new IllegalArgumentException("SQL Error!");
    }
  }

  public T entity(Object id) {
    return entity((int) id);
  }

  public T entity(int id) {
    return forId(id).entity();
  }

  public T entity(List<Object> objects) {
    final List<T> results = limit(1).entityList(objects);
    return results.isEmpty() ? null : results.stream().findFirst().orElse(null);
  }

  public T entity() {
    return entity(List.of());
  }

  public List<T> entityList(int limit) {
    return limit(limit).entityList();
  }

  public List<T> entityList() {
    return determineEntityList(list(List.of()));
  }

  public List<T> entityList(List<Object> parameters) {
    return determineEntityList(list(parameters));
  }

  @SuppressWarnings("unchecked")
  private List<T> determineEntityList(List<Object[]> objectList) {
    final List<T> out = new SortedList<>();
    try {
      if (targetId.isInstance(Entity.class)) {
        final Method getMethod = getTargetId().getMethod("get", Object[].class);
        for (final Object[] objects : objectList) {
          final T object = (T) getMethod.invoke(null, objects);
          out.add(object);
        }

      } else if (getDepartment() != null) {
        final Map<String, Class<?>> classes = determineSubClasses();
        for (Object[] objects : objectList) {
          final Class<?> aClass = classes.get((String) objects[1]);
          final Method getMethod = aClass.getMethod("get", Object[].class);
          final T object = (T) getMethod.invoke(null, objects);
          out.add(object);
        }
      }
    } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    return out;
  }

  @NotNull
  private Map<String, Class<?>> determineSubClasses() {
    final Map<String, Class<?>> classes = new HashMap<>();
    for (final Class<?> aClass : new Reflections("de.zahrie.trues").get(Scanners.SubTypes.of(targetId).asClass())) {
      final Table annotation = aClass.getAnnotation(Table.class);
      if (annotation != null) classes.put(annotation.department(), aClass);
    }
    return classes;
  }

  @SuppressWarnings("unchecked")
  public Integer id() {
    fields.clear();
    fields.add(SQLReturnField.idOf((Class<Id>) targetId));
    return (Integer) single()[0];
  }

  public <C extends Id> C convert(Class<C> targetClass) {
    final List<C> results = convertList(targetClass, 1);
    return results.isEmpty() ? null : results.stream().findFirst().orElse(null);
  }

  public <C extends Id> List<C> convertList(Class<C> targetClass) {
    fields.clear();
    getAll(targetClass);
    final List<Object[]> objectsList = list();
    if (objectsList.get(0).length == 1) {
      return objectsList.stream().map(objects -> new Query<C>().entity(objects[0])).collect(Collectors.toCollection(SortedList::new));
    } else {
      return new Query<C>().get("_" + getTableName() + ".*").determineEntityList(objectsList);
    }
  }

  public <C extends Id> List<C> convertList(Class<C> targetClass, int limit) {
    return limit(limit).convertList(targetClass);
  }

  public List<T> entityListOr(Query<T> query) {
    return entityListOr(query, limit);
  }

  public List<T> entityListOr(Query<T> query, int limit) {
    List<T> ts = entityList(limit);
    if (ts.isEmpty()) ts = query.entityList(limit);
    return ts;
  }

  public T entityOr(Query<T> query) {
    T entity = entity();
    if (entity == null) entity = query.entity();
    return entity;
  }

  public <C extends Id> List<C> convertListOr(Class<C> targetClass, Query<C> query) {
    return convertListOr(targetClass, query, limit);
  }

  public <C extends Id> List<C> convertListOr(Class<C> targetClass, Query<C> query, int limit) {
    List<C> entity = convertList(targetClass, limit);
    if (entity.isEmpty()) entity = query.convertList(targetClass, limit);
    return entity;
  }

  public <C extends Id> C convertOr(Class<C> targetClass, Query<T> query) {
    C entity = convert(targetClass);
    if (entity == null) entity = query.convert(targetClass);
    return entity;
  }

  @NotNull
  private static Object[] getRow(ResultSet resultSet, List<? extends Class<?>> clazzes) throws SQLException {
    final Object[] data = new Object[clazzes.size()];
    for (int i = 0; i < clazzes.size(); i++) {
      final Class<?> clazz = clazzes.get(i);
      if (clazz.isInstance(Enum.class)) {
        final Listing listing = clazz.getAnnotation(Listing.class);
        if (listing == null) throw new IllegalArgumentException("Dieses Enum ist nicht zulässig.");

        final Object index = resultSet.getObject(i + 1);
        data[i] = index == null ? null : switch (listing.value()) {
          case ORDINAL -> SQLUtils.toEnum(clazz.asSubclass(Enum.class), (Byte) index - listing.start());
          case LOWER, CUSTOM, UPPER, CAPITALIZE -> SQLUtils.toEnum(clazz.asSubclass(Enum.class), index);
        };
        continue;
      }

      final Object field = resultSet.getObject(i + 1);
      if (field == null) data[i] = null;
      else if (clazz.isInstance(LocalDateTime.class)) data[i] = ((Timestamp) field).toLocalDateTime();
      else if (clazz.isInstance(LocalDate.class)) data[i] = ((Date) field).toLocalDate();
      else if (clazz.isInstance(String.class)) data[i] = field;
      else if (clazz.isInstance(Number.class)) data[i] = field;
      else throw new UnknownFormatConversionException("Das Format ist nicht bekannt.");
    }
    return data;
  }

  private List<? extends Class<?>> adjust(List<? extends Class<?>> clazzes, PreparedStatement statement) throws SQLException {
    final int columns = statement.getMetaData().getColumnCount();
    return new ArrayList<>(IntStream.range(0, columns)
        .mapToObj(i -> (clazzes.isEmpty() ? Object.class : clazzes.get(i % clazzes.size())))
        .toList());
  }
}
