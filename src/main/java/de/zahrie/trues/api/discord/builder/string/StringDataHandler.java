package de.zahrie.trues.api.discord.builder.string;

import java.util.ArrayList;
import java.util.List;

import de.zahrie.trues.api.database.connector.CellEntry;
import de.zahrie.trues.api.discord.builder.queryCustomizer.Enumeration;
import de.zahrie.trues.api.discord.builder.queryCustomizer.SimpleCustomQuery;
import de.zahrie.trues.api.discord.command.slash.Column;
import org.jetbrains.annotations.NotNull;

public class StringDataHandler {
  private final SimpleCustomQuery query;
  private final List<Object[]> entries;
  private final List<ColumnData> lengths;
  private int index;

  public StringDataHandler(SimpleCustomQuery query, List<Object[]> entries) {
    this.query = query;
    this.entries = entries;
    this.lengths = determineLengths();
  }

  @NotNull
  private List<ColumnData> determineLengths() {
    final List<ColumnData> lengths = new ArrayList<>();
    for (int j = 0; j < entries.get(0).length; j++) {
      final int i = j;
      final Column column = query.getColumns().get(i);
      final int maxRowLength = Math.min(column.maxLength(), entries.stream()
          .map(object -> new CellEntry(object[i]).round(column).length())
          .max(Integer::compare).orElse(column.maxLength()));
      if (column.withPrevious()) {
        final int lastIndex = lengths.size() - 1;
        lengths.get(lastIndex).add(maxRowLength);
        continue;
      }
      lengths.add(new ColumnData(j, maxRowLength));
    }
    return lengths;
  }

  public String createHeader(int index, Integer colIndex, String name) {
    this.index = index;
    final Object[] head = query.getColumns().stream().map(Column::name).toArray();
    if (name != null) head[colIndex] = name;
    this.index--;
    final StringBuilder headString = new StringBuilder(create(head, null) + "\n");
    headString.append(handleEnumeration(null, ""));
    for (int i = 0; i < lengths.size(); i++) {
      if (i > 0) headString.append("+");
      headString.append("-".repeat(lengths.get(i).getLength() + (i == 0 || i == lengths.size() - 1 ? 1 : 2)));
    }
    return headString.toString();
  }

  public String create(int entryIndex, int index) {
    this.index = index;
    final Object[] entry = entries.get(entryIndex);
    return create(entry, query.getEnumeration());
  }

  private String create(Object[] entry, Enumeration enumeration) {
    final List<String> keys = new ArrayList<>();
    for (ColumnData wrapper : lengths) {
      final int index = wrapper.getIndex();
      Column column = query.getColumns().get(index);
      final var baseEntry = new CellEntry(entry[index]);
      String cell;
      if (enumeration != null) {
        final boolean left = baseEntry.entry() instanceof String;
        cell = (column.left() ? "" : " - ") + String.format("%" + (left ? "-" : "") + wrapper.getSubLengths().get(0) + "s", baseEntry.round(column));
      } else {
        cell = String.format("%" + wrapper.getLength() + "s", baseEntry.round(column));
      }
      if (wrapper.isTwoInOne()) {
        final var additional = new CellEntry(entry[index + 1]);
        column = query.getColumns().get(index + 1);
        if (enumeration != null) {
          final boolean left = baseEntry.entry() instanceof String;
          final String cell2 = (column.left() ? "" : " - ") + String.format("%" + (left ? "-" : "") + wrapper.getSubLengths().get(1) + "s", additional.round(column));
          cell += cell2;
        }
      }
      keys.add(cell);
    }
    final String data = String.join(" | ", keys);
    return handleEnumeration(enumeration, data);
  }

  private String handleEnumeration(Enumeration enumeration, String data) {
    final int leadingSpaces = String.valueOf(entries.size()).length() - String.valueOf(index).length();
    if (enumeration != null) data = " ".repeat(leadingSpaces) + index + ". " + data;
    else if (!query.getEnumeration().equals(Enumeration.NONE)) data = " ".repeat(String.valueOf(entries.size()).length() + 2) + data;
    this.index++;
    return data;
  }

}
