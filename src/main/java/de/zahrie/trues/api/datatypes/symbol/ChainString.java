package de.zahrie.trues.api.datatypes.symbol;

import java.util.Arrays;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

class ChainString implements Comparable<ChainString> {
  protected final String value;

  protected ChainString(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public int compareTo(@NotNull ChainString o) {
    return value.compareTo(o.value);
  }

  public char charAt(int index) {
    return value.charAt(index);
  }

  public boolean contains(CharSequence s) {
    return value.contains(s);
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof String ? value.equals(o) : (o instanceof ChainString) && (this == o || value.equals(((ChainString) o).value));
  }

  public boolean equalsCase(String anotherString) {
    return value.equalsIgnoreCase(anotherString);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  public int indexOf(String str) {
    return value.indexOf(str);
  }

  public int indexOf(Chain chain) {
    return value.indexOf(chain.toString());
  }

  public int indexOf(String str, int fromIndex) {
    return value.indexOf(str, fromIndex);
  }

  public boolean isBlank() {
    return value.isBlank();
  }

  public boolean isEmpty() {
    return value.isEmpty();
  }

  public int length() {
    return value.length();
  }

  public int lastIndexOf(String str) {
    return value.lastIndexOf(str);
  }

  public int lastIndexOf(String str, int fromIndex) {
    return value.lastIndexOf(str, fromIndex);
  }

  public Chain lower() {
    return Chain.of(value.toLowerCase());
  }
  public Chain repeat(int count) {
    return Chain.of(value.repeat(count));
  }

  public Chain replace(CharSequence target, CharSequence replacement) {
    return Chain.of(value.replace(target, replacement));
  }

  public Chain[] split(String regex) {
    return (Chain[]) Arrays.stream(value.split(regex)).map(Chain::of).toArray();
  }

  public boolean startsWith(String prefix) {
    return value.startsWith(prefix);
  }

  public boolean startsWith(String prefix, int toffset) {
    return value.startsWith(prefix, toffset);
  }

  public Chain substring(int beginIndex) {
    return Chain.of(value.substring(beginIndex));
  }

  public Chain substring(int beginIndex, int endIndex) {
    return Chain.of(value.substring(beginIndex, endIndex));
  }

  public char[] toCharArray() {
    return value.toCharArray();
  }

  public Chain upper() {
    return Chain.of(value.toUpperCase());
  }
}
