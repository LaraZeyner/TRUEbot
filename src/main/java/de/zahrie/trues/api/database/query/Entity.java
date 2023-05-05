package de.zahrie.trues.api.database.query;

import java.io.Serializable;

import de.zahrie.trues.api.database.connector.Database;

public interface Entity<T extends Entity<T>> extends Serializable, Id {
  T create();

  default T forceCreate() {
    final T entity = create();
    Database.connection().commit();
    return entity;
  }

  default void update() {
    create();
  }

  default void forceUpdate() {
    update();
    Database.connection().commit();
  }

  default void delete() {
    new Query<T>().delete(getId());
  }

  default void forceDelete() {
    delete();
    Database.connection().commit();
  }

  default T refresh() {
    return new Query<T>().entity(getId());
  }
}
