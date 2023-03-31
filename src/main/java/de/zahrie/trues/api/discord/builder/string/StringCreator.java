package de.zahrie.trues.api.discord.builder.string;

import java.util.ArrayList;
import java.util.List;

import de.zahrie.trues.util.Const;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class StringCreator {
  private final List<String> data = new ArrayList<>();
  private final boolean enumerated;
  private final String title;

  private String description;

  private int lengthRemaining = Const.DISCORD_MESSAGE_MAX_CHARACTERS;

  private final List<String> builders = new ArrayList<>();

  public StringCreator(boolean enumerated, String title, String description) {
    this.enumerated = enumerated;
    this.title = title;
    this.description = description;
  }

  public void add(String row) {
    this.data.add(row);
  }

  public List<String> build() {
    StringBuilder builder = new StringBuilder("**" + this.title + "**");
    if (this.description != null) {
      builder.append("\n__").append(this.description).append("__");
    }
    builder.append("\n\n");

    if (this.data.isEmpty()) {
      return List.of("keine Daten");
    }
    builder.append(data.get(0)).append("\n").append(data.get(1)).append("\n");

    lengthRemaining -= builder.length();
    for (int i = 0; i < (enumerated ? data.size() / 5 : data.size()); i++) {
      final int start = enumerated ? i*5+2 : i+2;
      final int end = Math.min(data.size(), start + (enumerated ? 5 : 1));
      final String stripped = String.join("\n", data.subList(start, end));
      lengthRemaining -= stripped.length();
      if (lengthRemaining >= 0) {
        builder.append(stripped);
      } else {
        this.builders.add(builder.toString());
        builder = new StringBuilder(stripped);
      }
      if (end == data.size()) {
        break;
      }
    }
    this.builders.add(builder.toString());
    return this.builders;
  }

}
