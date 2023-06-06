package de.zahrie.trues.api.discord.builder.leaderboard;

import java.util.List;

import de.zahrie.trues.api.discord.builder.EmbedWrapper;
import de.zahrie.trues.api.discord.builder.InfoPanelBuilder;
import de.zahrie.trues.api.discord.builder.queryCustomizer.SimpleCustomQuery;
import lombok.Data;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

@Data
public class Leaderboard {
  protected final SimpleCustomQuery customQuery;

  public PublicLeaderboard createNewPublic(@NotNull SlashCommandInteractionEvent event) {
    final MessageChannelUnion eventChannel = event.getChannel();
    final PublicLeaderboard publicLeaderboard = new PublicLeaderboard(customQuery, eventChannel.getIdLong());

    final EmbedWrapper data = getDataList();
    final List<MessageEmbed> wrapperEmbeds = data.getEmbeds();
    final List<String> merge = data.merge();
    if ((merge.isEmpty() || merge.get(0).isBlank()) && wrapperEmbeds.isEmpty()) {
      eventChannel.sendMessage("keine Daten").queue(publicLeaderboard::add);
    }
    for (int i = 0; i < merge.size(); i++) {
      final String content = merge.get(i);
      if (i + 1 == merge.size() && !wrapperEmbeds.isEmpty())
        eventChannel.sendMessage(content).addEmbeds(wrapperEmbeds).queue(publicLeaderboard::add);
      else
        eventChannel.sendMessage(content).queue(publicLeaderboard::add);
    }
    return publicLeaderboard;
  }

  public void buildNew(SlashCommandInteractionEvent event) {
    final EmbedWrapper data = getDataList();
    final List<MessageEmbed> wrapperEmbeds = data.getEmbeds();
    final String first = data.merge().get(0);
    if (first.isBlank()) {
      event.getHook().sendMessageEmbeds(wrapperEmbeds).queue();
    } else if (wrapperEmbeds.isEmpty()) {
      event.getHook().sendMessage(first).queue();
    } else {
      event.getHook().sendMessage(first).addEmbeds(wrapperEmbeds).queue();
    }
  }

  protected EmbedWrapper getDataList() {
    final List<List<Object[]>> data = customQuery.getData();
    final List<SimpleCustomQuery> queries = data == null ? List.of(customQuery) : data.stream().map(d -> SimpleCustomQuery.custom(customQuery.getNamedQuery(), d)).toList();
    return new InfoPanelBuilder(customQuery.getHeadTitle(), customQuery.getHeadDescription(), queries).build();
  }
}
