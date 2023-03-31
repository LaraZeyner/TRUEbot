package de.zahrie.trues.api.discord.builder;

import java.util.List;

import de.zahrie.trues.api.discord.builder.embed.CustomEmbedData;
import de.zahrie.trues.api.discord.builder.embed.EmbedCreator;
import de.zahrie.trues.api.discord.builder.embed.EmbedQueryBuilder;
import de.zahrie.trues.api.discord.builder.queryCustomizer.CustomQuery;
import de.zahrie.trues.api.discord.builder.string.StringCreator;
import de.zahrie.trues.api.discord.builder.string.StringQueryBuilder;

public record InfoPanelBuilder(String title, String description, List<CustomQuery> queries, List<CustomEmbedData> embedData) {
  public EmbedWrapper build() {
    EmbedWrapper wrapper = EmbedWrapper.of();
    EmbedCreator currentEmbedCreator = null;
    StringCreator currentStringCreator = null;

    for (CustomQuery query : queries) {
      if (query.getColumns().size() > 3) {
        if (currentStringCreator == null) currentStringCreator = new StringCreator(query.isEnumerated(), this.title, this.description);
        if (currentEmbedCreator != null) {
          wrapper = wrapper.embed(currentEmbedCreator.build());
          currentEmbedCreator = null;
        }
        currentStringCreator = new StringQueryBuilder(currentStringCreator, queries, embedData).build();
        continue;
      }

      if (currentEmbedCreator == null) currentEmbedCreator = new EmbedCreator(query.isEnumerated(), this.title, this.description);
      if (currentStringCreator != null) {
        wrapper = wrapper.content(currentStringCreator.build());
        currentStringCreator = null;
      }
      currentEmbedCreator = new EmbedQueryBuilder(currentEmbedCreator, queries, embedData).build();
    }

    if (currentEmbedCreator != null) wrapper = wrapper.embed(currentEmbedCreator.build());
    if (currentStringCreator != null) wrapper = wrapper.content(currentStringCreator.build());
    return wrapper;
  }
}
