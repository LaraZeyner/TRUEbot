package de.zahrie.trues.api.discord.builder.leaderboard;

import java.util.List;

import de.zahrie.trues.api.datatypes.symbol.Chain;
import de.zahrie.trues.api.discord.builder.EmbedWrapper;
import de.zahrie.trues.api.discord.builder.InfoPanelBuilder;
import de.zahrie.trues.api.discord.builder.embed.CustomEmbedData;
import de.zahrie.trues.api.discord.builder.queryCustomizer.CustomQuery;
import lombok.Data;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Data
public class Leaderboard {
  protected final CustomQuery.Queries customQuery;

  public void createNewPublic(List<CustomEmbedData> customEmbedData, SlashCommandInteractionEvent event) {
    final MessageChannelUnion eventChannel = event.getChannel();
    final PublicLeaderboard publicLeaderboard = new PublicLeaderboard(customQuery, eventChannel.getIdLong());

    final EmbedWrapper data = getData(customEmbedData);
    final List<MessageEmbed> wrapperEmbeds = data.getEmbeds();
    for (Chain chain : data.merge()) {
      eventChannel.sendMessage(chain.toString()).queue(publicLeaderboard::add);
    }
    if (!wrapperEmbeds.isEmpty()) eventChannel.sendMessageEmbeds(wrapperEmbeds).queue(publicLeaderboard::add);

    LeaderboardHandler.add(publicLeaderboard);
  }

  public void buildNew(List<CustomEmbedData> customEmbedData, SlashCommandInteractionEvent event) {
    final EmbedWrapper data = getData(customEmbedData);
    final List<MessageEmbed> wrapperEmbeds = data.getEmbeds();
    final List<Chain> merge = data.merge();
    for (int i = 0; i < merge.size(); i++) {
      final Chain chain = merge.get(i);
      if (i == 0) {
        event.reply(chain.toString()).setEphemeral(false).queue();
        continue;
      }
      event.getChannel().sendMessage(chain.toString()).queue();
    }
    if (!wrapperEmbeds.isEmpty()) event.getChannel().sendMessageEmbeds(wrapperEmbeds.subList(0, Math.min(wrapperEmbeds.size(), 10))).queue();
  }

  protected EmbedWrapper getData(List<CustomEmbedData> customEmbedData) {
    return new InfoPanelBuilder(customQuery.getTitle(), customQuery.getDescription(), List.of(customQuery.getCustomQuery()), customEmbedData).build();
  }
}
