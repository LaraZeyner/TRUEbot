package de.zahrie.trues.api.discord.builder.embed;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.zahrie.trues.api.discord.builder.queryCustomizer.CustomColumn;
import de.zahrie.trues.api.discord.builder.queryCustomizer.CustomQuery;
import de.zahrie.trues.database.Database;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record EmbedQueryBuilder(EmbedCreator creator, List<CustomQuery> queries, List<CustomEmbedData> embedData) {
  public EmbedCreator build() {
    for (CustomQuery query : queries) {
      final List<Object[]> entries = query.getQuery().contains(".") ? Database.Find.getData(query.getQuery()) :
          embedData.stream().filter(data -> data.key().equals(query.getQuery()))
              .map(CustomEmbedData::data).findFirst().orElse(null);
      if (!handleNoData(query, entries)) handleData(query, Objects.requireNonNull(entries));
    }
    return creator;
  }

  private void handleData(CustomQuery query, @NotNull List<Object[]> entries) {
    for (int i = 0; i < entries.get(0).length; i++) {
      final String content = determineColumnEntry(entries, i);
      final CustomColumn column = query.getColumns().get(i);
      final boolean inline = i != 0 || column.isInline();
      creator.add(column.getValue(), content, inline);
    }
  }

  @NotNull
  private String determineColumnEntry(List<Object[]> entries, int i) {
    final int j = i;
    return entries.stream().map(object -> String.valueOf(object[j]))
        .collect(Collectors.joining("\n"));
  }

  private boolean handleNoData(CustomQuery query, @Nullable List<Object[]> list) {
    if (list != null && !list.isEmpty()) return false;
    creator.add(query.getColumns().get(0).getValue(), "keine Daten", true);
    return true;
  }
}
