package de.zahrie.trues.api.discord.builder;

import java.util.List;

import de.zahrie.trues.api.discord.builder.embed.CustomEmbedData;
import de.zahrie.trues.api.discord.builder.embed.EmbedCreator;
import de.zahrie.trues.api.discord.builder.embed.EmbedQueryBuilder;
import de.zahrie.trues.api.discord.builder.string.StringCreator;
import de.zahrie.trues.api.discord.builder.string.StringQueryBuilder;
import de.zahrie.trues.api.discord.command.slash.annotations.DBQuery;

public record InfoPanelBuilder(String title, String description, DBQuery[] queries, List<CustomEmbedData> embedData) {
  public EmbedWrapper build() {
    EmbedWrapper wrapper = EmbedWrapper.of();
    EmbedCreator currentEmbedCreator = null;
    StringCreator currentStringCreator = null;

    for (final DBQuery query : queries) {
      if (query.columns().length > 3) {
        if (currentStringCreator == null) currentStringCreator = new StringCreator(query.enumerated(), this.title, this.description);
        if (currentEmbedCreator != null) {
          wrapper = wrapper.embed(currentEmbedCreator.build());
          currentEmbedCreator = null;
        }
        currentStringCreator = new StringQueryBuilder(currentStringCreator, queries, embedData).build();
        continue;
      }

      if (currentEmbedCreator == null) currentEmbedCreator = new EmbedCreator(query.enumerated(), this.title, this.description);
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
