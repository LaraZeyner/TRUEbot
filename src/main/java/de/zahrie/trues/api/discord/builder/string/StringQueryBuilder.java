package de.zahrie.trues.api.discord.builder.string;

import java.util.List;
import java.util.stream.IntStream;

import de.zahrie.trues.api.discord.util.cmd.annotations.DBQuery;
import de.zahrie.trues.util.database.Database;

/**
 * Created by Lara on 11.02.2023 for TRUEbot
 */
public record StringQueryBuilder(StringCreator creator, DBQuery[] queries) {

  public StringCreator build() {
    for (DBQuery query : queries) {
      final List<Object[]> entries = Database.Find.getData(query.query());

      if (!entries.isEmpty()) {
        final var dataHandler = new StringDataHandler(query, entries);
        creator.add(dataHandler.createHeader());
        IntStream.range(0, entries.size()).mapToObj(dataHandler::create).forEach(creator::add);
      }
    }

    return creator;
  }

}
