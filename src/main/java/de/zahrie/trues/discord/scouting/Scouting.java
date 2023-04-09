package de.zahrie.trues.discord.scouting;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannel;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelType;
import de.zahrie.trues.api.coverage.match.log.MatchLogBuilder;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import de.zahrie.trues.util.Const;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public record Scouting(OrgaTeam orgaTeam, Participator participator, Match match, ThreadChannel thread) {

  public Scouting(OrgaTeam orgaTeam, Participator participator, Match match) {
    this(orgaTeam, participator, match, determineThreadChannel(orgaTeam, participator, match));
  }

  private static ThreadChannel determineThreadChannel(OrgaTeam orgaTeam, Participator participator, Match match) {
    final AtomicReference<ThreadChannel> thread = new AtomicReference<>();
    final TeamChannel scoutingChannel = orgaTeam.getTeamChannels().stream().filter(teamChannel -> teamChannel.getTeamChannelType().equals(TeamChannelType.SCOUTING)).findFirst().orElse(null);
    if (scoutingChannel == null) return null;
    final Team team = participator.getTeam();
    final TextChannel textChannel = (TextChannel) scoutingChannel.getChannel();
    if (participator.getMessageId() == null) {
      textChannel.sendMessageEmbeds(new MatchLogBuilder(match, orgaTeam.getTeam()).getLog())
          .queue(message -> {
            textChannel.createThreadChannel(Const.THREAD_CHANNEL_START + team.getAbbreviation() + " (" + team.getId() + ")", message.getIdLong()).queue();
            participator.setMessageId(message.getIdLong());
          });
      Database.save(participator);
    }
    textChannel.retrieveMessageById(participator.getMessageId()).queue(message -> thread.set(message.getStartedThread()));
    return thread.get();
  }

  public void sendCustom(IReplyCallback event, ScoutingType type, ScoutingGameType gameType, Integer days, Integer page) {
    final EmbedBuilder builder = new EmbedBuilder()
        .setTitle(type.getTitleStart() + participator.getTeam().getName())
        .setDescription("Lineup: opgg und porofessor coming soon\nTyp: ");
    new ScoutingEmbedHandler(participator, gameType, days, page).get(type).forEach(builder::addField);
    event.replyEmbeds(builder.build()).queue();
  }

  public void update() {
    sendLog();
    send(Scouting.ScoutingType.LINEUP);
    send(Scouting.ScoutingType.OVERVIEW);
  }

  public void sendLog() {
    thread.getParentChannel().asGuildMessageChannel().retrieveMessageById(participator.getMessageId())
        .queue(message -> message.editMessageEmbeds(new MatchLogBuilder(match, orgaTeam.getTeam()).getLog()).queue());
  }

  public void send(ScoutingType type) {
    send(type, ScoutingGameType.TEAM_GAMES, 365, 1);
  }

  public void send(ScoutingType type, ScoutingGameType gameType, Integer days, Integer page) {
    final TeamChannel scoutingChannel = orgaTeam.getTeamChannels().stream().filter(teamChannel -> teamChannel.getTeamChannelType().equals(TeamChannelType.SCOUTING)).findFirst().orElse(null);
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
    final Message msg = MessageHistory.getHistoryFromBeginning(threadChannel).complete().getRetrievedHistory().stream()
        .filter(message -> !message.getEmbeds().isEmpty())
        .filter(message -> message.getEmbeds().stream().anyMatch(embed -> embed.getTitle() != null && embed.getTitle().startsWith(type.getTitleStart())))
        .findFirst().orElse(null);

    final EmbedBuilder builder = new EmbedBuilder()
        .setTitle(type.getTitleStart() + participator.getTeam().getName())
        .setDescription("Datum: " + TimeFormat.DEFAULT.of(match.getStart()) + "\nWinchance: coming soon\nErwartetes Lineup: opgg und porofessor coming soon\nTyp: ")
        .setFooter("zuletzt aktualisiert " + TimeFormat.DEFAULT.now());
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
