package de.zahrie.trues.api.discord.builder.string;

import java.util.ArrayList;
import java.util.List;

import de.zahrie.trues.api.discord.builder.queryCustomizer.CustomColumn;
import de.zahrie.trues.api.discord.builder.queryCustomizer.CustomQuery;
import de.zahrie.trues.api.database.CellEntry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StringDataHandler {
  private final List<ColumnData> lengths = new ArrayList<>();
  private final CustomQuery query;
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
    final Object[] head = query.getColumns().stream().map(CustomColumn::getValue).toArray();
    final StringBuilder headString = new StringBuilder(create(head, false) + "\n");
    for (int i = 0; i < this.lengths.size(); i++) {
      headString.append("-".repeat(this.lengths.get(i).getLength() + (i == 0 || i == this.lengths.size() - 1 ? 1 : 2)));
    }
    return headString + "\n";
  }

  private void init() {
    for (int j = 0; j < entries.get(0).length; j++) {
      final int i = j;
      final CustomColumn column = query.getColumns().get(i);
      final int maxRowLength = Math.min(column.getMaxLength(), entries.stream()
          .map(object -> new CellEntry(object[i]).round(column).length())
          .max(Integer::compare).orElse(column.getMaxLength()));
      if (column.isWithPrevious()) {
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
      CustomColumn column = query.getColumns().get(index);
      final var baseEntry = new CellEntry(entry[index]);
      String cell = String.format((column.isLeft() ? "" : "-") + "%" + wrapper.getSubLengths().get(0) + "s", baseEntry.round(column));
      if (wrapper.isTwoInOne()) {
        final var additional = new CellEntry(entry[index + 1]);
        column = query.getColumns().get(index + 1);
        final String cell2 = String.format((column.isLeft() ? "" : "-") + "%" + wrapper.getSubLengths().get(1) + "s", additional.round(column));
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
