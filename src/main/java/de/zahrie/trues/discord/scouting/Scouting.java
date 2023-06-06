package de.zahrie.trues.discord.scouting;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannel;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelType;
import de.zahrie.trues.api.coverage.match.MatchResult;
import de.zahrie.trues.api.coverage.match.log.EventStatus;
import de.zahrie.trues.api.coverage.match.log.MatchLogBuilder;
import de.zahrie.trues.api.coverage.match.model.LeagueMatch;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import de.zahrie.trues.discord.scouting.teaminfo.TeamInfoManager;
import de.zahrie.trues.util.Const;
import de.zahrie.trues.util.Util;
import de.zahrie.trues.util.io.log.Console;
import de.zahrie.trues.util.io.log.DevInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.jetbrains.annotations.Nullable;

public record Scouting(OrgaTeam orgaTeam, Participator participator, Match match, @Nullable ThreadChannel thread) {

  public Scouting(OrgaTeam orgaTeam, Participator participator, Match match) {
    this(orgaTeam, participator, match, determineThreadChannel(orgaTeam, participator, match));
  }

  @Nullable
  private static ThreadChannel determineThreadChannel(OrgaTeam orgaTeam, Participator participator, Match match) {
    final AtomicReference<ThreadChannel> thread = new AtomicReference<>();
    final TeamChannel scoutingChannel = orgaTeam.getChannels().get(TeamChannelType.SCOUTING);
    if (scoutingChannel == null) return null;

    final Team team = participator.getTeam();
    final TextChannel textChannel = (TextChannel) scoutingChannel.getChannel();
    if (participator.getMessageId() == null) {
      textChannel.sendMessageEmbeds(new MatchLogBuilder(match, orgaTeam.getTeam()).getLog())
          .queue(message -> {
            textChannel.createThreadChannel(Const.THREAD_CHANNEL_START + team.getAbbreviation() + " (" + team.getId() + ")", message.getIdLong()).queue(thread::set);
            participator.setMessageId(message.getIdLong());
          });
    } else {
      textChannel.retrieveMessageById(participator.getMessageId()).queue(
          message -> thread.set(message.getStartedThread()),
          throwable -> new DevInfo(participator.getMessageId() + " konnte nicht gefunden werden.").with(Console.class).severe(throwable));
    }
    return thread.get();
  }

  public void sendCustom(IReplyCallback event, ScoutingType type, ScoutingGameType gameType, Integer days, Integer page) {
    if (participator.getTeam() == null) return;

    final EmbedBuilder builder = new EmbedBuilder()
        .setTitle(type.getTitleStart() + participator.getTeam().getName())
        .setDescription("Lineup: opgg und porofessor coming soon\nTyp: ");
    new ScoutingEmbedHandler(participator, gameType, days, page).get(type).forEach(builder::addField);
    event.replyEmbeds(builder.build()).queue();
  }

  public void update() {
    forceUpdate();
    final Participator ourTeam = match.getParticipator(orgaTeam.getTeam());
    final Team team = participator.getTeam();
    if (team == null) return;

    if (participator.getTeam().getOrgaTeam() != null) {
      final Scouting opponentScouting = ScoutingManager.forTeam(ourTeam.getTeam().getOrgaTeam());
      if (opponentScouting != null) opponentScouting.forceUpdate();
    }
    if (!(match instanceof LeagueMatch) || match.getStatus().ordinal() >= EventStatus.SCHEDULING_CONFIRM.ordinal()) {
      ourTeam.getEvent().updateScheduledEvent();
    }
  }

  private void forceUpdate() {
    sendLog();
    send(Scouting.ScoutingType.LINEUP);
    send(Scouting.ScoutingType.OVERVIEW);
    TeamInfoManager.fromTeam(orgaTeam).updateAll();
  }

  public void sendLog() {
    if (thread == null) return;
    thread.getParentChannel().asGuildMessageChannel().retrieveMessageById(participator.getMessageId())
        .queue(message -> message.editMessageEmbeds(new MatchLogBuilder(match, orgaTeam.getTeam()).getLog()).queue());
  }

  public void send(ScoutingType type) {
    send(type, ScoutingGameType.TEAM_GAMES, 365, 1);
  }

  public void send(ScoutingType type, ScoutingGameType gameType, Integer days, Integer page) {
    if (participator.getTeam() == null) return;

    final TeamChannel scoutingChannel = orgaTeam.getChannels().get(TeamChannelType.SCOUTING);
    if (scoutingChannel == null) return;
    final TextChannel textChannel = (TextChannel) scoutingChannel.getChannel();
    final ScoutingGameType finalGameType = gameType;
    final Integer finalDays = days;
    final Integer finalPage = page;
    textChannel.getThreadChannels().stream()
        .filter(threadChannel -> threadChannel.getName().contains(String.valueOf(participator.getTeam().getId())))
        .findFirst().ifPresent(threadChannel -> handleEmbed(threadChannel, type, finalGameType, finalDays, finalPage));
  }

  private void handleEmbed(ThreadChannel threadChannel, ScoutingType type, ScoutingGameType gameType, int days, int page) {
    if (participator.getTeam() == null) return;

    if (threadChannel.isArchived()) threadChannel.getManager().setArchived(false).queue();
    final Message msg = MessageHistory.getHistoryFromBeginning(threadChannel).complete().getRetrievedHistory().stream()
        .filter(message -> !message.getEmbeds().isEmpty())
        .filter(message -> message.getEmbeds().stream().anyMatch(embed -> embed.getTitle() != null && embed.getTitle().startsWith(type.getTitleStart())))
        .findFirst().orElse(null);

    final MatchResult matchResult = match.getResult().ofTeam(participator.getTeam());
    final String winPercent = Util.avoidNull(matchResult, "no data", MatchResult::getWinPercent);
    final EmbedBuilder builder = new EmbedBuilder()
        .setTitle(type.getTitleStart() + participator.getTeam().getName())
        .setDescription("Datum: " + TimeFormat.DEFAULT_FULL.of(match.getStart()) + "\nWinchance: " + winPercent + "\nErwartetes Lineup: opgg und porofessor coming soon\nTyp: " + match.getClass().getSimpleName())
        .setFooter("zuletzt aktualisiert " + TimeFormat.DEFAULT_FULL.now());
    new ScoutingEmbedHandler(participator, gameType, days, page).get(type).forEach(builder::addField);
    final MessageEmbed embed = builder.build();

    if (msg == null) threadChannel.sendMessageEmbeds(embed).queue();
    else msg.editMessageEmbeds(embed).queue();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof final Scouting scouting)) return false;
    return Objects.equals(orgaTeam, scouting.orgaTeam) && Objects.equals(participator, scouting.participator) && Objects.equals(match, scouting.match);
  }

  @Override
  public int hashCode() {
    return Objects.hash(orgaTeam, participator, match);
  }

  @RequiredArgsConstructor
  @Getter
  public enum ScoutingType {
    CHAMPIONS("Champions von "),
    HISTORY("Games von "),
    LINEUP("Lineup von "),
    MATCHUPS("Natchups von "),
    OVERVIEW("Übersicht von "),
    PLAYER_HISTORY("Matchhistory von "),
    SCHEDULE("Schedule von ");
    private final String titleStart;

    public static ScoutingType fromKey(String key) {
      return Arrays.stream(ScoutingType.values())
          .filter(type -> type.getTitleStart().split(" ")[0].equals(key))
          .findFirst().orElse(null);
    }
  }

}
