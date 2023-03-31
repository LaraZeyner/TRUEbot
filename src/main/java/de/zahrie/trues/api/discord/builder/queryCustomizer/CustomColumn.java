package de.zahrie.trues.api.discord.builder.queryCustomizer;

import de.zahrie.trues.api.discord.command.slash.annotations.Column;
import lombok.Data;

@Data
public class CustomColumn {
  public static CustomColumn fromDBColumn(Column column) {
    return new CustomColumn(column.value(), column.left(), column.inline(), column.round(), column.maxLength(), column.withPrevious());
  }

  private final String value;
  private boolean left = true;
  private boolean inline = true;
  private int round = 0;
  private final int maxLength;
  private boolean withPrevious = false;

  public CustomColumn(String value, int maxLength) {
    this.value = value;
    this.maxLength = maxLength;
  }

  public CustomColumn(String value, boolean left, boolean inline, int round, int maxLength, boolean withPrevious) {
    this.value = value;
    this.left = left;
    this.inline = inline;
    this.round = round;
    this.maxLength = maxLength;
    this.withPrevious = withPrevious;
  }
}
