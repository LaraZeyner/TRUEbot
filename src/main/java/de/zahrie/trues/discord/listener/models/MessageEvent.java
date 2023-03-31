package de.zahrie.trues.discord.listener.models;

import java.util.Comparator;
import java.util.regex.Pattern;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.orgateam.OrgaTeamFactory;
import de.zahrie.trues.api.coverage.match.MatchFactory;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.datatypes.symbol.StringExtention;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.discord.scouting.ScoutingManager;
import de.zahrie.trues.util.Const;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Events, die sich auf Nachrichten beziehen
 *
 * @see net.dv8tion.jda.api.events.message.GenericMessageEvent
 * @see net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent
 * @see net.dv8tion.jda.api.events.message.MessageDeleteEvent
 * @see net.dv8tion.jda.api.events.message.MessageEmbedEvent
 * @see MessageReceivedEvent
 * @see net.dv8tion.jda.api.events.message.MessageUpdateEvent
 * @see net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent
 * @see net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
 * @see net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent
 * @see net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEmojiEvent
 * @see net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent
 */
@ExtensionMethod(StringExtention.class)
public class MessageEvent extends ListenerAdapter {
  @Override
  public void onMessageReceived(@NotNull MessageReceivedEvent event) {

    if (event.getChannel() instanceof ThreadChannel threadChannel) {
      final String channelName = threadChannel.getName();
      if (channelName.startsWith(Const.THREAD_CHANNEL_START)) {
        final String between = channelName.between("(", ")", -1);
        final Team team = Database.Find.find(Team.class, between.intValue());
        final OrgaTeam orgaTeam = OrgaTeamFactory.getTeamFromChannel(threadChannel.getParentChannel());
        final Match match = MatchFactory.getMatchesOf(orgaTeam.getTeam(), team).stream().max(Comparator.naturalOrder()).orElse(null);
        if (match != null) {
          boolean executed = false;
          final String content = event.getMessage().getContentDisplay();
          final String value = content.between(":").strip();
          if (content.startsWith("Result:")) executed = handleResult(threadChannel, match, value);
          else if (content.startsWith("Start:")) executed = handleStart(threadChannel, match, value);
          else if (content.startsWith("Lineup 1:")) executed = handleLineup(threadChannel, match, value, true);
          else if (content.startsWith("Lineup 2:")) executed = handleLineup(threadChannel, match, value, false);
          if (executed) {
            Database.saveAndCommit(match);
            event.getMessage().delete().queue();
            ScoutingManager.updateThread(threadChannel);
          }
        }
      }
    }
    super.onMessageReceived(event);
  }

  private static boolean handleLineup(@NotNull ThreadChannel threadChannel, Match match, String value, boolean home) {
    final Participator participator = home ? match.getHome() : match.getGuest();
    participator.get().setOrderedLineup(value);
    Database.saveAndCommit(participator);
    if (value.equals("-:-") || Pattern.compile("\\d+:\\d+").matcher(value).matches()) {
      threadChannel.sendMessage("Neues Lineup festgelegt.").queue();
    }
    return true;
  }

  private static boolean handleStart(@NotNull ThreadChannel threadChannel, Match match, String value) {
    final Time time = Time.of(value);
    if (time != null) {
      match.setStart(time);
      threadChannel.sendMessage("Neuer Spieltermin: **" + time.text(TimeFormat.DISCORD) + "**").queue();
    }
    return true;
  }

  private static boolean handleResult(@NotNull ThreadChannel threadChannel, Match match, String value) {
    match.setResult(value);
    if (value.equals("-:-") || Pattern.compile("\\d+:\\d+").matcher(value).matches()) {
      threadChannel.sendMessage("Neues Ergebnis: **" + value + "**").queue();
    }
    return true;
  }
}
