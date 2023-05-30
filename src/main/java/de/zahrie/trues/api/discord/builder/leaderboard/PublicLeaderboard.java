package de.zahrie.trues.api.discord.builder.leaderboard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import de.zahrie.trues.api.discord.builder.EmbedWrapper;
import de.zahrie.trues.api.discord.builder.queryCustomizer.NamedQuery;
import de.zahrie.trues.api.discord.builder.queryCustomizer.SimpleCustomQuery;
import de.zahrie.trues.api.discord.util.Nunu;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.json.JSONArray;
import org.json.JSONObject;

public class PublicLeaderboard extends Leaderboard {
  private final long channelID;
  private final List<Long> messageIds;

  public PublicLeaderboard(SimpleCustomQuery customQuery, long channelID) {
    this(customQuery, channelID, new ArrayList<>());
  }

  private PublicLeaderboard(SimpleCustomQuery customQuery, long channelID, List<Long> messageIds) {
    super(customQuery);
    this.channelID = channelID;
    this.messageIds = messageIds;
  }

  void add(Message message) {
    messageIds.add(message.getIdLong());
  }

  public void updateData() {
    final GuildChannel eventChannel = Nunu.DiscordChannel.getChannel(channelID);
    if (!(eventChannel instanceof MessageChannel messageChannel)) {
      return;
    }
    final EmbedWrapper data = super.getData(List.of());
    final List<MessageEmbed> wrapperEmbeds = data.getEmbeds();
    final List<String> merge = data.merge();
    for (int i = 0; i < merge.size(); i++) {
      final String str = merge.get(i);
      final Long messageId = messageIds.get(i);
      messageChannel.retrieveMessageById(messageId).queue(message -> message.editMessage(str).queue());
    }
    for (int i = 0; i < messageIds.size(); i++) {
      final Long messageId = messageIds.get(i);
      final int finalI = i;
      messageChannel.retrieveMessageById(messageId).queue(message -> {
        if (merge.size() > finalI) message.editMessage(merge.get(finalI)).queue();
        fromTo(finalI, messageIds.size(), wrapperEmbeds, message);
      });
    }
  }

  private void fromTo(int page, int maxPages, List<MessageEmbed> embeds, Message message) {
    final int embedPages = (int) Math.ceil(embeds.size() / 10.);
    final int firstRemainingPage = maxPages - embedPages;
    if (page >= firstRemainingPage) {
      final int upcomingPages = maxPages - page;
      final int end = embeds.size() - 10 * upcomingPages;
      final int start = Math.max(0, end - 10);
      message.editMessageEmbeds(embeds.subList(start, end)).queue();
    } else {
      message.editMessageEmbeds().queue();
    }
  }

  public JSONObject toJSON() {
    final var leaderboardData = new JSONObject();
    leaderboardData.put("key", customQuery.getName());
    leaderboardData.put("channelId", channelID);
    leaderboardData.put("messageIds", new JSONArray(messageIds));
    leaderboardData.put("parameters", new JSONArray(customQuery.getParameters().stream().map(param -> (String) param).toList()));
    return leaderboardData;
  }
  
  public static PublicLeaderboard fromJSON(JSONObject entry) {
    final List<String> parameters = IntStream.range(0, entry.getJSONArray("parameters").length()).mapToObj(entry.getJSONArray("parameters")::getString).toList();
    return new PublicLeaderboard(
        SimpleCustomQuery.params(NamedQuery.valueOf("key"), parameters.stream().map(string -> (Object) string).toList()),
        entry.getLong("channelId"),
        IntStream.range(0, entry.getJSONArray("messageIds").length()).mapToObj(entry.getJSONArray("messageIds")::getLong).toList());
  }
}
