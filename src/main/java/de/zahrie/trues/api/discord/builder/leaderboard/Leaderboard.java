package de.zahrie.trues.api.discord.builder.leaderboard;

import java.util.List;

import de.zahrie.trues.api.discord.builder.EmbedWrapper;
import de.zahrie.trues.api.discord.builder.InfoPanelBuilder;
import de.zahrie.trues.api.discord.builder.queryCustomizer.SimpleCustomQuery;
import lombok.Data;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Data
public class Leaderboard {
  protected final SimpleCustomQuery customQuery;

  public void createNewPublic(List<SimpleCustomQuery> customEmbedData, SlashCommandInteractionEvent event) {
    final MessageChannelUnion eventChannel = event.getChannel();
    final PublicLeaderboard publicLeaderboard = new PublicLeaderboard(customQuery, eventChannel.getIdLong());

    final List<Object[]> objects = customEmbedData.stream().flatMap(simpleCustomQuery -> simpleCustomQuery.build().stream()).toList();
    final EmbedWrapper data = getData(objects);
    final List<MessageEmbed> wrapperEmbeds = data.getEmbeds();
    for (String chain : data.merge()) eventChannel.sendMessage(chain).queue(publicLeaderboard::add);
    if (!wrapperEmbeds.isEmpty()) eventChannel.sendMessageEmbeds(wrapperEmbeds).queue(publicLeaderboard::add);

    LeaderboardHandler.add(publicLeaderboard);
  }

  public void buildNew(List<SimpleCustomQuery> customEmbedData, SlashCommandInteractionEvent event) {
    final List<Object[]> objects = customEmbedData.stream().flatMap(simpleCustomQuery -> simpleCustomQuery.build().stream()).toList();
    final EmbedWrapper data = getData(objects);
    final List<MessageEmbed> wrapperEmbeds = data.getEmbeds();
    final List<String> merge = data.merge();
    for (int i = 0; i < merge.size(); i++) {
      final String chain = merge.get(i);
      if (i == 0) {
        event.reply(chain).setEphemeral(false).queue();
        continue;
      }
      event.getChannel().sendMessage(chain).queue();
    }
    if (!wrapperEmbeds.isEmpty()) event.getChannel().sendMessageEmbeds(wrapperEmbeds.subList(0, Math.min(wrapperEmbeds.size(), 10))).queue();
  }

  protected EmbedWrapper getData(List<Object[]> customEmbedData) {
    return new InfoPanelBuilder(customQuery.getHeadTitle(), customQuery.getHeadDescription(), List.of(customQuery.custom(customEmbedData))).build();
  }
}
