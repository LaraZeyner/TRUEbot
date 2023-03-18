package de.zahrie.trues.api.discord.builder.string;

import java.util.List;
import java.util.stream.IntStream;

import de.zahrie.trues.api.discord.builder.embed.CustomEmbedData;
import de.zahrie.trues.api.discord.command.slash.annotations.DBQuery;
import de.zahrie.trues.database.Database;

public record StringQueryBuilder(StringCreator creator, DBQuery[] queries, List<CustomEmbedData> embedData) {
  public StringCreator build() {
    for (DBQuery query : queries) {
      final List<Object[]> entries = query.query().contains(".") ? Database.Find.getData(query.query()) :
          embedData.stream().filter(data -> data.key().equals(query.query())).map(CustomEmbedData::data).findFirst().orElse(null);
      if (entries != null && !entries.isEmpty()) {
        final var dataHandler = new StringDataHandler(query, entries);
        creator.add(dataHandler.createHeader());
        IntStream.range(0, entries.size()).mapToObj(dataHandler::create).forEach(creator::add);
      }
    }
    return creator;
  }
}
