package de.zahrie.trues.api.discord.builder;

import java.util.List;

import de.zahrie.trues.api.discord.builder.embed.EmbedQueryBuilder;
import de.zahrie.trues.api.discord.util.cmd.annotations.DBQuery;
import de.zahrie.trues.api.discord.builder.string.StringCreator;
import de.zahrie.trues.api.discord.builder.embed.EmbedCreator;
import de.zahrie.trues.api.discord.builder.string.StringQueryBuilder;
import de.zahrie.trues.util.database.Database;

/**
 * Created by Lara on 12.02.2023 for TRUEbot
 */
public record InfoPanelBuilder(String title, String description, DBQuery[] queries) {

  public EmbedWrapper build() {
    EmbedWrapper wrapper = EmbedWrapper.of();
    EmbedCreator currentEmbedCreator = null;
    StringCreator currentStringCreator = null;

    for (DBQuery query : queries) {
      final List<Object[]> entries = Database.getData(query.query());

      if (entries.get(0).length > 3) {
        if (currentStringCreator == null) {
          currentStringCreator = new StringCreator(query.enumerated(), this.title, this.description);
        }
        if (currentEmbedCreator != null) {
          wrapper = wrapper.embed(currentEmbedCreator.build());
          currentEmbedCreator = null;
        }

        currentStringCreator = new StringQueryBuilder(currentStringCreator, queries).build();
        continue;
      }

      // EMBED
      if (currentEmbedCreator == null) {
        currentEmbedCreator = new EmbedCreator(query.enumerated(), this.title, this.description);
      }
      if (currentStringCreator != null) {
        wrapper = wrapper.content(currentStringCreator.build());
        currentStringCreator = null;
      }

      currentEmbedCreator = new EmbedQueryBuilder(currentEmbedCreator, queries).build();

    }

    if (currentEmbedCreator != null) {
      wrapper = wrapper.embed(currentEmbedCreator.build());
    }
    if (currentStringCreator != null) {
      wrapper = wrapper.content(currentStringCreator.build());
    }

    return wrapper;
  }

}
