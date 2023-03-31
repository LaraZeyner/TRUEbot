package de.zahrie.trues.api.discord.builder.string;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class ColumnData {
  private final int index;
  private final List<Integer> subLengths = new ArrayList<>();

  public ColumnData(int index, int length) {
    this.index = index;
    this.subLengths.add(length);
  }

  public void add(int length) {
    this.subLengths.add(length);
  }

  public boolean isTwoInOne() {
    return this.subLengths.size() > 1;
  }

  public int getLength() {
    final int length = this.subLengths.stream().mapToInt(Integer::intValue).sum();
    return this.subLengths.size() == 1 ? length : length + 3;
  }

}
