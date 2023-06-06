package de.zahrie.trues.api.discord.command.slash;

public record Column(String name, boolean left, boolean inline, int round, int maxLength, boolean withPrevious, boolean ignore)  {
  public Column(String name) {
    this(name, true, true, 0, Integer.MAX_VALUE, false, false);
  }

  public Column(String name, boolean ignore) {
    this(name, true, true, 0, Integer.MAX_VALUE, false, ignore);
  }

  public Column(String name, int maxLength) {
    this(name, true, true, 0, maxLength, false, false);
  }

  public Column(String name, int maxLength, boolean ignore) {
    this(name, true, true, 0, maxLength, false, ignore);
  }

  public Column(String name, boolean left, boolean inline, int round) {
    this(name, left, inline, round, Integer.MAX_VALUE, !left, false);
  }

  public Column(String name, boolean left, boolean inline, int round, boolean ignore) {
    this(name, left, inline, round, Integer.MAX_VALUE, !left, ignore);
  }

  public Column(String name, boolean left, boolean inline, int round, int maxLength) {
    this(name, left, inline, round, maxLength, !left, false);
  }

  public Column(String name, boolean left, boolean inline, int round, int maxLength, boolean ignore) {
    this(name, left, inline, round, maxLength, !left, ignore);
  }
}
