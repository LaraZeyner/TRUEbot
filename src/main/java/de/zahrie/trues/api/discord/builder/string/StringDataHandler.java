package de.zahrie.trues.api.discord.builder.string;

import java.util.ArrayList;
import java.util.List;

import de.zahrie.trues.api.database.connector.CellEntry;
import de.zahrie.trues.api.discord.builder.queryCustomizer.SimpleCustomQuery;
import de.zahrie.trues.api.discord.command.slash.Column;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StringDataHandler {
  private final List<ColumnData> lengths = new ArrayList<>();
  private final SimpleCustomQuery query;
  private final List<Object[]> entries;
  private int index = 0;

  public String create(int entryIndex) {
    if (index == 0) {
      init();
      index = 1;
    }
    final Object[] entry = this.entries.get(entryIndex);
    return create(entry, query.isEnumerated());
  }

  public String createHeader() {
    final Object[] head = query.getColumns().stream().map(Column::name).toArray();
    final StringBuilder headString = new StringBuilder(create(head, false) + "\n");
    for (int i = 0; i < this.lengths.size(); i++) {
      headString.append("-".repeat(this.lengths.get(i).getLength() + (i == 0 || i == this.lengths.size() - 1 ? 1 : 2)));
    }
    return headString + "\n";
  }

  private void init() {
    for (int j = 0; j < entries.get(0).length; j++) {
      final int i = j;
      final Column column = query.getColumns().get(i);
      final int maxRowLength = Math.min(column.maxLength(), entries.stream()
          .map(object -> new CellEntry(object[i]).round(column).length())
          .max(Integer::compare).orElse(column.maxLength()));
      if (column.withPrevious()) {
        final int lastIndex = lengths.size() - 1;
        lengths.get(lastIndex).add(maxRowLength + 3);
        continue;
      }
      lengths.add(new ColumnData(j, maxRowLength));
    }
  }

  private String create(Object[] entry, boolean enumerate) {
    final List<String> keys = new ArrayList<>();
    for (ColumnData wrapper : lengths) {
      final int index = wrapper.getIndex();
      Column column = query.getColumns().get(index);
      final var baseEntry = new CellEntry(entry[index]);
      String cell = String.format((column.left() ? "" : "-") + "%" + wrapper.getSubLengths().get(0) + "s", baseEntry.round(column));
      if (wrapper.isTwoInOne()) {
        final var additional = new CellEntry(entry[index + 1]);
        column = query.getColumns().get(index + 1);
        final String cell2 = String.format((column.left() ? "" : "-") + "%" + wrapper.getSubLengths().get(1) + "s", additional.round(column));
        cell += " (" + cell2 + ")";
      }
      keys.add(cell);
    }
    final String data = String.join(" | ", keys);
    return handleEnumeration(enumerate, data);
  }

  private String handleEnumeration(boolean enumerate, String data) {
    if (enumerate) {
      data = index + ". " + data;
    }
    this.index++;
    return data;
  }

}
