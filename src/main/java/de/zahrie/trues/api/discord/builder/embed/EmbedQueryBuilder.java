package de.zahrie.trues.api.discord.builder.embed;

import java.util.List;
import java.util.stream.Collectors;

import de.zahrie.trues.api.discord.builder.queryCustomizer.SimpleCustomQuery;
import de.zahrie.trues.api.discord.command.slash.Column;
import de.zahrie.trues.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record EmbedQueryBuilder(EmbedCreator creator, SimpleCustomQuery query) {
  public EmbedCreator build() {
    if (query.getHeadTitle() != null) creator.add(query.getHeadTitle(), query.getHeadDescription(), false);
    final List<Object[]> entries = query.build();
    if (!handleNoData(query, entries)) handleData(query, Util.nonNull(entries));
    return creator;
  }

  private void handleData(SimpleCustomQuery query, @NotNull List<Object[]> entries) {
    for (int i = 0; i < entries.get(0).length; i++) {
      final String content = determineColumnEntry(entries, i);
      final Column column = query.getColumns().get(i);
      final boolean inline = query.getColumns().size() > 1;
      creator.add(column.value(), content, inline);
    }
  }

  @NotNull
  private String determineColumnEntry(List<Object[]> entries, int i) {
    final int j = i;
    return entries.stream().map(object -> String.valueOf(object[j]))
        .collect(Collectors.joining("\n"));
  }

  private boolean handleNoData(SimpleCustomQuery query, @Nullable List<Object[]> list) {
    if (list != null && !list.isEmpty()) return false;
    creator.add(query.getColumns().get(0).value(), "keine Daten", true);
    return true;
  }
}
