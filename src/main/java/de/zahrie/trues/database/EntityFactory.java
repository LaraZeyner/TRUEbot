package de.zahrie.trues.database;

import jakarta.persistence.metamodel.EntityType;
import org.hibernate.Session;

/**
 * Created by Lara on 26.02.2023 for TRUEbot
 */
public final class EntityFactory {

  static <T> String getName(Class<T> entityClass) {
    final Session session = Database.connection().getSession();
    final EntityType<T> storedEntity = session.getMetamodel().entity(entityClass);
    return storedEntity.getName();
  }

}
