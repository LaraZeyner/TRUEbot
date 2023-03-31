package de.zahrie.trues.api.discord.builder.string;

import java.util.List;
import java.util.stream.IntStream;

import de.zahrie.trues.api.discord.builder.embed.CustomEmbedData;
import de.zahrie.trues.api.discord.builder.queryCustomizer.CustomQuery;
import de.zahrie.trues.database.Database;

public record StringQueryBuilder(StringCreator creator, List<CustomQuery> queries, List<CustomEmbedData> embedData) {
  public StringCreator build() {
    for (CustomQuery query : queries) {
      final List<Object[]> entries = query.getQuery().contains(".") ? Database.Find.getData(query.getQuery()) :
          embedData.stream().filter(data -> data.key().equals(query.getQuery())).map(CustomEmbedData::data).findFirst().orElse(null);
      if (entries != null && !entries.isEmpty()) {
        final var dataHandler = new StringDataHandler(query, entries);
        creator.add(dataHandler.createHeader());
        IntStream.range(0, entries.size()).mapToObj(dataHandler::create).forEach(creator::add);
      }
    }
    return creator;
  }
}
