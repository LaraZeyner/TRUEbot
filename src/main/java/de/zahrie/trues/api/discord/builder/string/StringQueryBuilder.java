package de.zahrie.trues.api.discord.builder.string;

import java.util.List;
import java.util.stream.IntStream;

import de.zahrie.trues.api.discord.builder.queryCustomizer.SimpleCustomQuery;

public record StringQueryBuilder(StringCreator creator, SimpleCustomQuery query) {
  public StringCreator build() {
    final List<Object[]> entries = query.build();
    if (entries != null && !entries.isEmpty()) {
      final var dataHandler = new StringDataHandler(query, entries);
      creator.add(dataHandler.createHeader());
      IntStream.range(0, entries.size()).mapToObj(dataHandler::create).forEach(creator::add);
    }
    return creator;
  }
}
