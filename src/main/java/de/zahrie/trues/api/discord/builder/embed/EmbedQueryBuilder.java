package de.zahrie.trues.api.discord.builder.embed;

import java.util.List;
import java.util.stream.Collectors;

import de.zahrie.trues.api.discord.util.cmd.annotations.Column;
import de.zahrie.trues.api.discord.util.cmd.annotations.DBQuery;
import de.zahrie.trues.util.database.Database;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Lara on 11.02.2023 for TRUEbot
 */
public record EmbedQueryBuilder(EmbedCreator creator, DBQuery[] queries) {

  public EmbedCreator build() {
    for (DBQuery query : queries) {
      final List<Object[]> entries = Database.Find.getData(query.query());

      if (!handleNoData(query, entries)) {
        handleData(query, entries);
      }
    }

    return creator;
  }

  private void handleData(DBQuery query, List<Object[]> entries) {
    for (int i = 0; i < entries.get(0).length; i++) {
      final String content = determineColumnEntry(entries, i);
      final Column column = query.columns()[i];
      final boolean inline = i != 0 || column.inline();
      this.creator.add(column.value(), content, inline);
    }
  }

  @NotNull
  private String determineColumnEntry(List<Object[]> entries, int i) {
    final int j = i;
    return entries.stream().map(object -> String.valueOf(object[j]))
        .collect(Collectors.joining("\n"));
  }

  private boolean handleNoData(DBQuery query, List<Object[]> list) {
    if (list.isEmpty()) {
      this.creator.add(query.columns()[0].value(), "keine Daten", true);
      return true;
    }
    return false;
  }

}
