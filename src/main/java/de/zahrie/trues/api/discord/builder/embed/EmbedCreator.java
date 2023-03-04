package de.zahrie.trues.api.discord.builder.embed;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.datatypes.symbol.Chain;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Lara on 10.02.2023 for TRUEbot
 */

@RequiredArgsConstructor
public class EmbedCreator {
  private final boolean enumerated;
  private final String title;
  private String description = "";
  private Color color;
  private final List<EmbedColumn> data = new ArrayList<>();

  private int totalDataLength = 0;

  private final List<EmbedColumn> waitingColumns = new ArrayList<>();

  private final List<EmbedBuilder> builders = new ArrayList<>();

  private EmbedBuilder currentBuilder;

  public EmbedCreator(boolean enumerated, String title, String description) {
    this.enumerated = enumerated;
    this.title = title;
    this.description = description;
  }

  // TODO color
  public EmbedCreator(boolean enumerated, String title, String description, Color color) {
    this.enumerated = enumerated;
    this.title = title;
    this.description = description;
    this.color = color;
  }

  public void add(String name, String value, boolean inline) {
    this.data.add(new EmbedColumn(name, value, inline));
  }

  public List<MessageEmbed> build() {
    return getBuilder().stream().map(EmbedBuilder::build).toList();
  }

  private void getBaseBuilder() {
    String str = "zuletzt aktualisiert " + Time.of().chain(TimeFormat.DEFAULT).toString();
    final Chain footer = Chain.of("zuletzt aktualisiert ").add(Time.of().chain(TimeFormat.DEFAULT));
    this.currentBuilder = new EmbedBuilder().setFooter(footer.toString());
    this.totalDataLength = footer.length();

    final Chain titleStripped = Chain.of(title).strip(MessageEmbed.TITLE_MAX_LENGTH);
    this.currentBuilder.setTitle(titleStripped.toString());
    this.totalDataLength += titleStripped.length();

    final Chain descriptionStripped = Chain.of(description).strip(MessageEmbed.DESCRIPTION_MAX_LENGTH);
    this.currentBuilder.setDescription(descriptionStripped.toString());
    this.totalDataLength += descriptionStripped.length();

    if (this.color != null) {
      this.currentBuilder.setColor(this.color);
    }
  }

  @NotNull
  private List<EmbedBuilder> getBuilder() {
    getBaseBuilder();
    for (int i = 0; i < data.size(); i++) {
      i += checkAddField(i);
    }
    addAllColumns();
    builders.add(currentBuilder);
    return builders;
  }

  private int checkAddField(int index) {
    final EmbedColumn column = data.get(index);
    this.totalDataLength += column.name().length() + column.value().length();
    if (this.totalDataLength > MessageEmbed.EMBED_MAX_LENGTH_BOT || column.value().length() > MessageEmbed.VALUE_MAX_LENGTH) {
      if (column.value().contains("\n")) {
        waitingColumns.add(column);
        final int skipped = determineWaiting(index);
        handleLimitedSpace();
        return skipped;
      }
      createNewBuilder();
      return 0;
    }

    if (!column.inline() || this.waitingColumns.size() == 3) {
      addAllColumns();
    }
    waitingColumns.add(column);
    return 0;
  }

  private void handleLimitedSpace() {
    final int nameLength = waitingColumns.stream().mapToInt(c -> c.name().length()).sum();
    final int space = Math.min(MessageEmbed.VALUE_MAX_LENGTH, MessageEmbed.EMBED_MAX_LENGTH_BOT - this.totalDataLength - nameLength);
    final boolean newEmbed = MessageEmbed.EMBED_MAX_LENGTH_BOT - this.totalDataLength - nameLength < MessageEmbed.VALUE_MAX_LENGTH;
    final int valueLength = waitingColumns.stream().mapToInt(c -> c.value().length()).max().orElse(0);
    if (valueLength <= space) {
      addAllColumns();
      return;
    }

    List<Integer> cols = new ArrayList<>();
    for (int i = 0; i < waitingColumns.get(0).value().split("\n").length / 5; i++) {
      final int j = enumerated ? i * 5 : i;
      final List<Integer> cls = waitingColumns.stream().map(c -> Chain.of(c.value()).ordinalIndexOf("\n", j)).toList();
      if (cls.stream().reduce(0, Integer::sum) > space) {
        splitFields(cols);
        break;
      }
      cols = cls;
    }
    if (newEmbed) {
      createNewBuilder();
    }
    handleLimitedSpace();
  }

  private void splitFields(List<Integer> cols) {
    for (int k = 0; k < waitingColumns.size(); k++) {
      final EmbedColumn embedColumn = waitingColumns.get(k);
      final String name = embedColumn.name();
      final String value = embedColumn.value();
      final boolean inline = embedColumn.inline();
      if (cols.get(k) >= value.length()) {
        this.currentBuilder.addField(name, value, inline);
        continue;
      }
      this.currentBuilder.addField(name, value.substring(0, cols.get(k)), inline);
      final var newCol = new EmbedColumn(name, value.substring(cols.get(k)), inline);
      this.waitingColumns.set(k, newCol);
    }
  }

  private int determineWaiting(int index) {
    int skipped = 0;
    for (int i = index +1; i < this.data.size(); i++) {
      final EmbedColumn col = this.data.get(i);
      if (!col.inline() || this.waitingColumns.size() == 3) {
        break;
      }
      waitingColumns.add(col);
      skipped++;
    }
    return skipped;
  }

  private void createNewBuilder() {
    this.builders.add(this.currentBuilder);
    getBaseBuilder();
  }

  private void addAllColumns() {
    for (EmbedColumn column : this.waitingColumns) {
      final Chain name = Chain.of(column.name()).strip(MessageEmbed.TITLE_MAX_LENGTH);
      this.currentBuilder.addField(name.toString(), column.value(), column.inline());
    }
    this.waitingColumns.clear();
  }

}
