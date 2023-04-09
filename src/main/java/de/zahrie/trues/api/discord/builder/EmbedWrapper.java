package de.zahrie.trues.api.discord.builder;

import java.util.ArrayList;
import java.util.List;

import de.zahrie.trues.util.Const;
import lombok.Data;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Data
public class EmbedWrapper {

  private final List<MessageEmbed> embeds = new ArrayList<>();

  private final List<List<String>> content = new ArrayList<>();

  public static EmbedWrapper of() {
    return new EmbedWrapper();
  }

  public EmbedWrapper embed(List<MessageEmbed> embed) {
    this.embeds.addAll(embed);
    return this;
  }

  public EmbedWrapper content(List<String> content) {
    this.content.add(content);
    return this;
  }

  public List<String> merge() {
    if (this.content.isEmpty()) return List.of("");

    final List<String> data = new ArrayList<>();
    StringBuilder out = new StringBuilder();
    for (List<String> texts : this.content) {
      for (String text : texts) {
        if (out.length() + text.length() > Const.DISCORD_MESSAGE_MAX_CHARACTERS) {
          data.add(out.toString());
          out = new StringBuilder(text);
        } else {
          out.append(text);
        }
      }
      out.append("\n\n");
    }
    data.add(out.toString());

    return data;
  }

}
