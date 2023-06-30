package de.zahrie.trues.api.discord.builder.embed;

import java.util.List;
import java.util.stream.Collectors;

import de.zahrie.trues.api.discord.builder.queryCustomizer.SimpleCustomQuery;
import de.zahrie.trues.api.discord.command.slash.Column;
import de.zahrie.trues.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record EmbedQueryBuilder(EmbedCreator creator, SimpleCustomQuery query) {
  public EmbedCreator build(Integer index, String name) {
    final List<Object[]> entries = query.build();
    if (!handleNoData(query, entries, index, name)) handleData(query, Util.nonNull(entries), index, name);
    return creator;
  }

  private void handleData(SimpleCustomQuery query, @NotNull List<Object[]> entries, Integer index, String name) {
    for (int i = 0; i < entries.get(0).length; i++) {
      final String content = determineColumnEntry(entries, i);
      final Column column = query.getColumns().get(i);
      final String n = index != null && index == i ? name : column.getName();
      creator.add(n, content, column.isInline());
    }
  }

  @NotNull
  private String determineColumnEntry(List<Object[]> entries, int i) {
    final int j = i;
    return entries.stream().map(object -> String.valueOf(object[j]))
        .collect(Collectors.joining("\n"));
  }

  private boolean handleNoData(SimpleCustomQuery query, @Nullable List<Object[]> list, Integer index, String name) {
    if (list != null && !list.isEmpty()) return false;
    final String n = (index != null && index == 1) ? name : query.getColumns().get(0).getName();
    if (!query.getColumns().get(0).isIgnore()) creator.add(n, "keine Daten", false);
    return true;
  }
}
