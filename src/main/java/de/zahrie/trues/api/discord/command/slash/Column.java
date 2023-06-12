package de.zahrie.trues.api.discord.command.slash;

import java.util.Objects;

import lombok.Setter;

public final class Column {
  @Setter
  private String name;
  private final boolean left;
  private final boolean inline;
  private final int round;
  private final int maxLength;
  private final boolean withPrevious;
  private final boolean ignore;

  public Column(String name, boolean left, boolean inline, int round, int maxLength, boolean withPrevious, boolean ignore) {
    this.name = name;
    this.left = left;
    this.inline = inline;
    this.round = round;
    this.maxLength = maxLength;
    this.withPrevious = withPrevious;
    this.ignore = ignore;
  }

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

  public String name() {
    return name;
  }

  public boolean left() {
    return left;
  }

  public boolean inline() {
    return inline;
  }

  public int round() {
    return round;
  }

  public int maxLength() {
    return maxLength;
  }

  public boolean withPrevious() {
    return withPrevious;
  }

  public boolean ignore() {
    return ignore;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (Column) obj;
    return Objects.equals(this.name, that.name) &&
        this.left == that.left &&
        this.inline == that.inline &&
        this.round == that.round &&
        this.maxLength == that.maxLength &&
        this.withPrevious == that.withPrevious &&
        this.ignore == that.ignore;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, left, inline, round, maxLength, withPrevious, ignore);
  }

  @Override
  public String toString() {
    return "Column[" +
        "name=" + name + ", " +
        "left=" + left + ", " +
        "inline=" + inline + ", " +
        "round=" + round + ", " +
        "maxLength=" + maxLength + ", " +
        "withPrevious=" + withPrevious + ", " +
        "ignore=" + ignore + ']';
  }

}
