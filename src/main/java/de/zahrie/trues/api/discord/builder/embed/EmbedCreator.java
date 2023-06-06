package de.zahrie.trues.api.discord.builder.embed;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.util.StringUtils;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

@ExtensionMethod(StringUtils.class)
public class EmbedCreator extends AbstractSimpleEmbedCreator {
  private final List<EmbedBuilder> embeds = new ArrayList<>();
  private EmbedBuilder currentEmbed;
  private int remainingSpace = MessageEmbed.EMBED_MAX_LENGTH_BOT;

  public EmbedCreator(boolean enumerated, String title, String description) {
    super(enumerated, title, description);
  }

  public EmbedCreator(boolean enumerated, String title, String description, Color color) {
    super(enumerated, title, description, color);
  }

  public List<MessageEmbed> build() {
    for (int i = 0; i < data.size(); i++) i += addField(i) - 1;
    embeds.add(currentEmbed);

    return embeds.stream().map(EmbedBuilder::build).toList();
  }

  private int addField(int index) {
    final EmbedColumn column = data.get(index);
    remainingSpace -= (column.name().length() + column.value().length());
    final List<EmbedColumn> waitingColumns = determineWaitingColumns(index);
    doAdd(waitingColumns);
    return waitingColumns.size();
  }

  private List<EmbedColumn> determineWaitingColumns(int index) {
    final EmbedColumn column = data.get(index);
    return column.value().contains("\n") ? determineWaiting(index) : List.of(column);
  }

  private List<EmbedColumn> determineWaiting(int index) {
    final List<EmbedColumn> waitingColumns = new ArrayList<>();
    final EmbedColumn column = data.get(index);
    waitingColumns.add(column);

    for (int i = index + 1; i < this.data.size(); i++) {
      final EmbedColumn col = this.data.get(i);
      if (!col.inline() || waitingColumns.size() == 3) break;
      waitingColumns.add(col);
    }
    return waitingColumns;
  }

  private void doAdd(List<EmbedColumn> columns) {
    if (currentEmbed == null) createNewEmbed();

    List<String> columnValues = columns.stream().map(EmbedColumn::value).toList();
    final int rows = columnValues.get(0).count("\n") + 1;
    List<String> toBeAdd = new ArrayList<>(List.of("", "", ""));

    for (int i = 0; i < rows; i++) {
      final List<String> row = columnValues.stream().map(string -> string.before("\n")).toList();
      int requiredSpace = row.stream().map(String::length).reduce(0, Integer::sum);
      if (enumerated) requiredSpace += 2 + String.valueOf(rows + 1).length();
      if (requiredSpace > remainingSpace) {
        createNewEmbed();
        remainingSpace -= columns.stream().mapToInt(c -> c.name().length()).sum();
      }

      if (isOutOfBounds(columns, toBeAdd, row)) {
        addFields(columns, toBeAdd);
        toBeAdd = new ArrayList<>(row);
      } else {
        for (int j = 0; j < columns.size(); j++) {
          final String add = ((!toBeAdd.get(j).isBlank()) ? "\n" : "") + ((j == 0 && enumerated) ? i+1 + ": " : "") + row.get(j);
          remainingSpace -= add.length();
          toBeAdd.set(j, toBeAdd.get(j) + add);
        }
      }
      columnValues = columnValues.stream().map(string -> string.after("\n")).toList();
    }

    addFields(columns, toBeAdd);
  }

  private static boolean isOutOfBounds(List<EmbedColumn> columns, List<String> toBeAdd, List<String> row) {
    for (int j = 0; j < columns.size(); j++) {
      final String current = toBeAdd.get(j);
      final String add = row.get(j);
      if (current.length() + add.length() > MessageEmbed.VALUE_MAX_LENGTH) return true;
    }
    return false;
  }

  private void addFields(List<EmbedColumn> columns, List<String> addStrings) {
    for (int i = 0; i < columns.size(); i++) {
      if (addStrings.get(i).isBlank()) continue;

      final EmbedColumn column = columns.get(i);
      currentEmbed.addField(column.name(), addStrings.get(i), column.inline());
    }
  }

  /**
   * Füge alle wartenden Spalten der Einbettung hinzu
   */
  private void addAllWaitingColumns(List<EmbedColumn> columns) {
    for (EmbedColumn column : columns) {
      final String name = column.name().keep(MessageEmbed.TITLE_MAX_LENGTH);
      this.currentEmbed.addField(name, column.value(), column.inline());
    }
  }

  private void createNewEmbed() {
    if (currentEmbed != null) this.embeds.add(currentEmbed);

    this.remainingSpace = MessageEmbed.EMBED_MAX_LENGTH_BOT;

    final String footer = "zuletzt aktualisiert " + TimeFormat.DEFAULT_FULL.now();
    this.currentEmbed = new EmbedBuilder().setFooter(footer);
    this.remainingSpace -= footer.length();

    final String titleStripped = title.keep(MessageEmbed.TITLE_MAX_LENGTH);
    this.currentEmbed.setTitle(titleStripped);
    this.remainingSpace -= titleStripped.length();

    final String descriptionStripped = description.keep(MessageEmbed.DESCRIPTION_MAX_LENGTH);
    this.currentEmbed.setDescription(descriptionStripped);
    this.remainingSpace -= descriptionStripped.length();

    if (this.color != null) this.currentEmbed.setColor(this.color);
  }

}
