package de.zahrie.trues.api.discord.builder.embed;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.zahrie.trues.api.discord.command.slash.annotations.Column;
import de.zahrie.trues.api.discord.command.slash.annotations.DBQuery;
import de.zahrie.trues.database.Database;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record EmbedQueryBuilder(EmbedCreator creator, DBQuery[] queries, List<CustomEmbedData> embedData) {

  public EmbedCreator build() {
    for (DBQuery query : queries) {
      final List<Object[]> entries = query.query().contains(".") ? Database.Find.getData(query.query()) :
          embedData.stream().filter(data -> data.key().equals(query.query()))
              .map(CustomEmbedData::data).findFirst().orElse(null);
      if (!handleNoData(query, entries)) handleData(query, Objects.requireNonNull(entries));
    }
    return creator;
  }

  private void handleData(DBQuery query, @NotNull List<Object[]> entries) {
    for (int i = 0; i < entries.get(0).length; i++) {
      final String content = determineColumnEntry(entries, i);
      final Column column = query.columns()[i];
      final boolean inline = i != 0 || column.inline();
      creator.add(column.value(), content, inline);
    }
  }

  @NotNull
  private String determineColumnEntry(List<Object[]> entries, int i) {
    final int j = i;
    return entries.stream().map(object -> String.valueOf(object[j]))
        .collect(Collectors.joining("\n"));
  }

  private boolean handleNoData(DBQuery query, @Nullable List<Object[]> list) {
    if (list != null && !list.isEmpty()) {
      return false;
    }
    creator.add(query.columns()[0].value(), "keine Daten", true);
    return true;
  }

}
