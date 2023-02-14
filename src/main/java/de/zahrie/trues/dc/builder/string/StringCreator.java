package de.zahrie.trues.dc.builder.string;

import java.util.ArrayList;
import java.util.List;

import de.zahrie.trues.util.Const;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by Lara on 10.02.2023 for TRUEbot
 */

@RequiredArgsConstructor
@Getter
public class StringCreator {
  private final List<String> data = new ArrayList<>();
  private final boolean enumerated;
  private final String title;

  private String description;

  private int lengthRemaining = Const.DISCORD_MESSAGE_MAX_CHARACTERS;

  private List<String> builders = new ArrayList<>();

  public StringCreator(boolean enumerated, String title, String description) {
    this.enumerated = enumerated;
    this.title = title;
    this.description = description;
  }

  public StringCreator add(String row) {
    this.data.add(row);
    return this;
  }

  public List<String> build() {
    String builder = "**" + this.title + "**";
    if (this.description != null) {
      builder += "\n__" + this.description + "__";
    }
    builder += "\n\n";

    if (this.data.isEmpty()) {
      return List.of("keine Daten");
    }
    builder += data.get(0) + "\n" + data.get(1) + "\n";

    lengthRemaining -= builder.length();
    for (int i = 0; i < (enumerated ? data.size() / 5 : data.size()); i++) {
      final int start = enumerated ? i*5+2 : i+2;
      final int end = Math.min(data.size(), start + (enumerated ? 5 : 1));
      final String stripped = String.join("\n", data.subList(start, end));
      lengthRemaining -= stripped.length();
      if (lengthRemaining >= 0) {
        builder += stripped;
      } else {
        this.builders.add(builder);
        builder = stripped;
      }
      if (end == data.size()) {
        break;
      }
    }
    this.builders.add(builder);
    return this.builders;
  }

}
