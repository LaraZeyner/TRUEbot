package de.zahrie.trues.api.discord.command.slash;

public record Column(String value, boolean left, boolean inline, int round, int maxLength, boolean withPrevious)  {
  public Column(String value) {
    this(value, true, true, 0, Integer.MAX_VALUE, false);
  }

  public Column(String value, int maxLength) {
    this(value, true, true, 0, maxLength, false);
  }
}
