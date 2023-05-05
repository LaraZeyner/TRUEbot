package de.zahrie.trues.discord.event.models;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import de.zahrie.trues.api.calendar.scheduling.SchedulingHandler;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.orgateam.OrgaTeamFactory;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannel;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelRepository;
import de.zahrie.trues.api.coverage.match.MatchFactory;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.team.model.TeamBase;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.discord.Settings;
import de.zahrie.trues.discord.scouting.ScoutingManager;
import de.zahrie.trues.util.Const;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.Util;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Events, die sich auf Nachrichten beziehen
 *
 * @see net.dv8tion.jda.api.events.message.GenericMessageEvent
 * @see net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent
 * @see MessageDeleteEvent
 * @see net.dv8tion.jda.api.events.message.MessageEmbedEvent
 * @see MessageReceivedEvent
 * @see MessageUpdateEvent
 * @see net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent
 * @see net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
 * @see net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent
 * @see net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEmojiEvent
 * @see net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent
 */
@ExtensionMethod({StringUtils.class, Nunu.DcMember.class, DiscordUserFactory.class})
public class MessageEvent extends ListenerAdapter {
  @Override
  public void onMessageReceived(@NotNull MessageReceivedEvent event) {
    if (event.getChannelType().equals(ChannelType.PRIVATE)) {
      handleSettings(event);
      return;
    }
    final DiscordUser user = DiscordUserFactory.getDiscordUser(Util.nonNull(event.getMember()));
    user.addMessage(event.getMessage().getContentDisplay());


    final TeamChannel teamChannel = TeamChannelRepository.getTeamChannelFromChannelId(event.getChannel().getIdLong());
    if (teamChannel != null) handleSchedulingEntry(event, event.getMessage());

    if (event.getChannel() instanceof ThreadChannel threadChannel) {
      final String channelName = threadChannel.getName();
      if (channelName.startsWith(Const.THREAD_CHANNEL_START)) handleEditMatchData(event, threadChannel);
    }

    if (event.getChannel().getId().equals(Nunu.DiscordChannel.getAdminChannel().getId())) {
      handleQuestionReply(event);
    }
  }

  private void handleSettings(MessageReceivedEvent event) {
    final Settings settings = new Settings(event.getMessage());
    if (settings.validate()) settings.execute(Util.nonNull(event.getMember()).getDiscordUser());
    else handleQuestion(event);
  }

  private static void handleQuestionReply(@NotNull MessageReceivedEvent event) {
    final String content = event.getMessage().getContentDisplay();
    if (!content.contains(":")) return;

    final Integer userId = content.before(":").intValue();
    final var discordUser = new Query<DiscordUser>().entity(userId);
    if (discordUser == null) return;

    final String answer = content.after(":");
    Nunu.DiscordMessager.dm(discordUser, answer);
    Nunu.DiscordChannel.getAdminChannel().sendMessage("Die Antwort wurde gesendet.").queue();
  }

  private static void handleQuestion(@NotNull MessageReceivedEvent event) {
    final DiscordUser discordUser = event.getAuthor().getMember().getDiscordUser();
    Nunu.DiscordChannel.getAdminChannel().sendMessage("**Neue Frage von **" + event.getAuthor().getAsMention() + " (" + discordUser.getId() +
        ")\n" + event.getMessage().getContentDisplay()).queue();
  }

  @Override
  public void onMessageUpdate(MessageUpdateEvent event) {
    final TeamChannel teamChannel = TeamChannelRepository.getTeamChannelFromChannelId(event.getChannel().getIdLong());
    if (teamChannel != null) handleSchedulingEntry(event, event.getMessage());
  }

  @Override
  public void onMessageDelete(MessageDeleteEvent event) {
    final TeamChannel teamChannel = TeamChannelRepository.getTeamChannelFromChannelId(event.getChannel().getIdLong());
    if (teamChannel != null) {
      event.getChannel().retrieveMessageById(event.getMessageId()).queue(message -> handleSchedulingEntry(event, message));
    }
  }

  private void handleSchedulingEntry(GenericMessageEvent event, Message message) {
    List<User> users = message.getMentions().getUsers();
    if (users.isEmpty()) users = List.of(message.getAuthor());
    if (SchedulingHandler.isRepeat(message.getContentDisplay())) {
      users.forEach(user -> new SchedulingHandler(user.getMember().getDiscordUser()).repeat());
      return;
    }
    final List<TimeRange> timeRanges = SchedulingHandler.determineTimeRanges(message.getContentStripped());
    if (!timeRanges.isEmpty()) {
      for (User user : users) {
        final DiscordUser discordUser = user.getMember().getDiscordUser();
        final SchedulingHandler handler = new SchedulingHandler(discordUser);
        if (event instanceof MessageDeleteEvent) handler.delete(timeRanges);
        else handler.add(timeRanges);
      }
    }
  }

  private static void handleEditMatchData(@NotNull MessageReceivedEvent event, ThreadChannel threadChannel) {
    final String between = threadChannel.getName().between("(", ")", -1);
    final var team = new Query<TeamBase>().entity(between.intValue());
    final OrgaTeam orgaTeam = OrgaTeamFactory.getTeamFromChannel(threadChannel.getParentChannel());
    if (orgaTeam == null || orgaTeam.getTeam() == null) return;
    final Match match = MatchFactory.getMatchesOf(orgaTeam.getTeam(), team).stream().max(Comparator.naturalOrder()).orElse(null);
    if (match != null) {
      final String content = event.getMessage().getContentDisplay();
      final String value = content.after(":").strip();
      if (content.startsWith("Result:")) handleResult(threadChannel, match, value);
      else if (content.startsWith("Start:")) handleStart(threadChannel, match, value);
      else if (content.startsWith("Lineup 1:")) handleLineup(threadChannel, match, value, true);
      else if (content.startsWith("Lineup 2:")) handleLineup(threadChannel, match, value, false);
      ((Entity<?>) match).forceUpdate();
      event.getMessage().delete().queue();
      ScoutingManager.updateThread(threadChannel);
    }
  }

  private static void handleLineup(@NotNull ThreadChannel threadChannel, Match match, String value, boolean home) {
    final Participator participator = home ? match.getHome() : match.getGuest();
    participator.get().setOrderedLineup(value);
    participator.forceUpdate();
    if (value.equals("-:-") || Pattern.compile("\\d+:\\d+").matcher(value).matches()) {
      threadChannel.sendMessage("Neues Lineup festgelegt.").queue();
    }
  }

  private static void handleStart(@NotNull ThreadChannel threadChannel, Match match, String value) {
    final LocalDateTime dateTime = value.getDateTime();
    if (dateTime != null) {
      match.updateStart(dateTime);
      threadChannel.sendMessage("Neuer Spieltermin: **" + TimeFormat.DISCORD.of(dateTime) + "**").queue();
    }
  }

  private static void handleResult(@NotNull ThreadChannel threadChannel, Match match, String value) {
    match.updateResult(value);
    if (value.equals("-:-") || Pattern.compile("\\d+:\\d+").matcher(value).matches()) {
      threadChannel.sendMessage("Neues Ergebnis: **" + value + "**").queue();
    }
  }
}
