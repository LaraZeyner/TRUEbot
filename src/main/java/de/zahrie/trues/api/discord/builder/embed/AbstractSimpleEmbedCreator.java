package de.zahrie.trues.api.discord.builder.embed;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.zahrie.trues.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;

@RequiredArgsConstructor
@ExtensionMethod(StringUtils.class)
public class AbstractSimpleEmbedCreator {
  protected final boolean enumerated;
  protected final String title;
  protected String description = "";
  protected Color color;
  protected final List<EmbedColumn> data = new ArrayList<>();



  public AbstractSimpleEmbedCreator(boolean enumerated, String title, String description) {
    this.enumerated = enumerated;
    this.title = title;
    this.description = description;
  }

  public AbstractSimpleEmbedCreator(boolean enumerated, String title, String description, Color color) {
    this.enumerated = enumerated;
    this.title = title;
    this.description = description;
    this.color = color;
  }

  public void add(String name, String value, boolean inline) {
    this.data.add(new EmbedColumn(name, value, inline));
  }
}
