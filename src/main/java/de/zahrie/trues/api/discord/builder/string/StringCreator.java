package de.zahrie.trues.api.discord.builder.string;

import java.util.ArrayList;
import java.util.List;

import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.discord.builder.queryCustomizer.Enumeration;
import de.zahrie.trues.util.Const;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class StringCreator {
  private final List<String> data = new ArrayList<>();
  private final Enumeration enumeration;
  private final String title;

  private String description;
  private int index;
  private int lengthRemaining = Const.DISCORD_MESSAGE_MAX_CHARACTERS;
  private final List<String> builders = new ArrayList<>();

  public StringCreator(Enumeration enumeration, String title, String description, int index) {
    this.enumeration = enumeration;
    this.title = title;
    this.description = description;
    this.index = index;
  }

  public void add(String row) {
    data.add(row);
  }

  public List<String> build() {
    StringBuilder builder = new StringBuilder("**" + title + "**");
    if (description != null) builder.append("\n_").append(description).append("_");
    builder.append("\n```");

    if (data.isEmpty()) return List.of("`keine Daten`");

    builder.append(data.get(0)).append("\n").append(data.get(1)).append("\n");

    lengthRemaining -= builder.length();
    for (int i = 0; i < (!enumeration.equals(Enumeration.NONE) ? data.size() / 5 : data.size()); i++) {
      final int start = !enumeration.equals(Enumeration.NONE) ? i*5+2 : i+2;
      final int end = Math.min(data.size(), start + (!enumeration.equals(Enumeration.NONE) ? 5 : 1));
      final String stripped = String.join("\n", data.subList(start, end));
      lengthRemaining -= stripped.length();
      if (lengthRemaining >= 3) builder.append(stripped);
      else {
        builders.add(builder + "```");
        builder = new StringBuilder("```" + stripped);
      }
      if (end == data.size()) break;
    }
    builders.add(builder.append("```zuletzt aktualisiert ").append(TimeFormat.AUTO.now()).toString());
    return builders;
  }

}
