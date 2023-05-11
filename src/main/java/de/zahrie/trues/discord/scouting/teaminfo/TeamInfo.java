package de.zahrie.trues.discord.scouting.teaminfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import de.zahrie.trues.api.calendar.Calendar;
import de.zahrie.trues.api.calendar.EventCalendar;
import de.zahrie.trues.api.calendar.scheduling.TeamTrainingScheduleHandler;
import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelType;
import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.league.model.PRMLeague;
import de.zahrie.trues.api.coverage.match.log.MatchLogBuilder;
import de.zahrie.trues.api.coverage.match.model.LeagueMatch;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.match.model.PRMMatch;
import de.zahrie.trues.api.coverage.participator.TeamLineupBase;
import de.zahrie.trues.api.coverage.participator.model.Lineup;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.player.model.PlayerRank;
import de.zahrie.trues.api.coverage.player.model.Rank;
import de.zahrie.trues.api.coverage.season.OrgaCupSeason;
import de.zahrie.trues.api.coverage.season.PRMSeason;
import de.zahrie.trues.api.coverage.season.SeasonFactory;
import de.zahrie.trues.api.coverage.stage.model.GroupStage;
import de.zahrie.trues.api.coverage.stage.model.PlayoffStage;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.coverage.team.leagueteam.LeagueTeam;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.api.coverage.team.model.Standing;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.query.Condition;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.api.discord.builder.embed.EmbedFieldBuilder;
import de.zahrie.trues.util.Util;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

@AllArgsConstructor
@Getter
public class TeamInfo {
  private final OrgaTeam orgaTeam;
  private Message message;
  @Setter
  private LocalDateTime lastUpdate;

  public TeamInfo(OrgaTeam orgaTeam) {
    this(orgaTeam, findOrCreate(orgaTeam), LocalDateTime.now().minusDays(2));
  }

  private static Message findOrCreate(OrgaTeam orgaTeam) {
    final MessageChannel messageChannel = (MessageChannel) orgaTeam.getChannels().get(TeamChannelType.INFO).getChannel();
    return MessageHistory.getHistoryFromBeginning(messageChannel).complete().getRetrievedHistory().stream()
        .filter(message -> !message.getEmbeds().isEmpty()).findFirst().orElse(null);
  }

  public void updateAll() {
    TeamInfoManager.addTeam(orgaTeam);
  }

  void create() {
    final MessageChannel messageChannel = (MessageChannel) orgaTeam.getChannels().get(TeamChannelType.INFO).getChannel();
    messageChannel.sendMessageEmbeds(getList()).queue(message1 -> this.message = message1);
  }

  List<MessageEmbed> getList() {
    return List.of(getOverview(), getScheduling(), getNextMatch(), getDivision(), getInternCup());
  }


  private MessageEmbed getDivision() {
    final PRMSeason currentSeason = SeasonFactory.getCurrentPRMSeason();
    final Team team = orgaTeam.getTeam();
    if (!(team instanceof PRMTeam prmTeam)) {
      return new EmbedBuilder().setTitle("keine Division").setDescription("Das Team ist nicht auf Prime League registriert.").build();
    }

    final PRMLeague lastLeague = prmTeam.getLastLeague();
    if (lastLeague == null) {
      return new EmbedBuilder().setTitle("keine Division").setDescription("Das Team hat nie Prime League gespielt.").build();
    }

    final String signupStatus = Util.avoidNull(currentSeason, "", season -> " - " + season.getSignupStatusForTeam((PRMTeam) orgaTeam.getTeam()));
    final var builder = new EmbedBuilder()
        .setTitle(lastLeague.getStage().getSeason().getName() + " - " + lastLeague.getName() + signupStatus, lastLeague.getUrl());
    final String descriptionPrefix = lastLeague.getStage().getSeason().equals(currentSeason) ? "aktuelle" : "letzte";
    builder.setDescription(descriptionPrefix + " Gruppe im Prime League Split");

    final List<LeagueTeam> signups = lastLeague.getSignups();
    new EmbedFieldBuilder<>(signups.stream().sorted(Comparator.comparing(LeagueTeam::getExpectedScore).reversed()).toList())
        .add("Teamname", l -> l.getTeam().getName())
        .add("Standing", l -> l.getScore().toString())
        .add("Prognose", l -> l.getExpectedScore().toString()).build().forEach(builder::addField);

    final Map<Playday, List<LeagueMatch>> playdayMatches = new HashMap<>();
    for (final LeagueMatch match : lastLeague.getMatches()) {
      if (!playdayMatches.containsKey(match.getPlayday())) playdayMatches.put(match.getPlayday(), new ArrayList<>());
      playdayMatches.get(match.getPlayday()).add(match);
    }
    playdayMatches.keySet().stream().sorted().forEach(playday -> new EmbedFieldBuilder<>(playdayMatches.get(playday).stream().sorted().toList())
        .add("Spielwoche " + playday.getIdx(), match -> TimeFormat.WEEKLY.of(match.getStart()))
        .add("Standing", match -> match.getHomeAbbr() + " vs " + match.getGuestAbbr())
        .add("Prognose", Match::getExpectedResult).build().forEach(builder::addField));
    final int correct = (int) lastLeague.getMatches().stream().filter(match -> match.getResult().wasAcurate(match)).count();
    final int incorrect = (int) lastLeague.getMatches().stream().filter(match -> Boolean.FALSE.equals(match.getResult().wasAcurate(match))).count();
    builder.addField("Fehlerrate", new Standing(correct, incorrect).getWinrate().toString(), false);
    return builder.build();
  }

  private MessageEmbed getInternCup() {
    final OrgaCupSeason lastSeason = SeasonFactory.getLastInternSeason();
    final OrgaCupSeason currentSeason = SeasonFactory.getCurrentInternSeason();
    final EmbedBuilder builder = new EmbedBuilder()
        .setTitle(currentSeason == null ? "keine Season" : (currentSeason.getFullName() + " - TRUE-Cup - " + currentSeason.getSignupStatusForTeam((PRMTeam) orgaTeam.getTeam())))
        .setDescription("Aktueller Spielplan im TRUE-Cup")
        .setFooter("zuletzt aktualisiert " + TimeFormat.DEFAULT.now())
        .addField("Kurzregeln", OrgaCupSeason.RULES + "\nStand-in Slots verbleibend: " + orgaTeam.getStandins(), false);

    if (currentSeason == null) return builder
        .addField("Zeitpunkt", "keine Daten", false).addField("Cup-Phase", "keine Daten", true)
        .build();

    new EmbedFieldBuilder<>(currentSeason.getEvents())
        .add("Zeitpunkt", eventDTO -> eventDTO.getData().get(0))
        .add("Cup-Phase", eventDTO -> eventDTO.getData().get(1))
        .build().forEach(builder::addField);

    if (lastSeason == null) return builder.build();

    final GroupStage groupStage = (GroupStage) lastSeason.getStage(Stage.StageType.GROUP_STAGE);
    for (League league : groupStage.leagues()) {
      final List<LeagueTeam> signups = league.getSignups();
      new EmbedFieldBuilder<>(signups.stream().sorted().toList())
          .add(league.getName(), l -> l.getTeam().getName())
          .add("Standing", l -> l.getScore().toString())
          .add("Prognose", l -> l.getExpectedScore().toString()).build().forEach(builder::addField);
    }
    getFieldsForStage(groupStage, "Gruppenspiele").forEach(builder::addField);

    final PlayoffStage playoffStage = (PlayoffStage) lastSeason.getStage(Stage.StageType.PLAYOFF_STAGE);
    getFieldsForStage(playoffStage, "Endrunde").forEach(builder::addField);

    final List<Match> games = orgaTeam.getTeam().getMatches().getMatchesOf(lastSeason).stream().filter(Match::isRunning).toList();
    getFieldsOfGames("kommende Spiele", games);

    return builder.build();
  }

  private List<MessageEmbed.Field> getFieldsForStage(Stage stage, String name) {
    final List<Match> games = orgaTeam.getTeam().getMatches().getMatchesOf(stage);
    return getFieldsOfGames(name, games);
  }

  private List<MessageEmbed.Field> getFieldsOfGames(String name, List<Match> games) {
    if (games.isEmpty()) {
      return List.of(new MessageEmbed.Field("Gruppenspiele", "keine Spiele verfügbar", false));
    }
    return new EmbedFieldBuilder<>(games)
        .add(name, match -> TimeFormat.WEEKLY.of(match.getStart()))
        .add("Standing", match -> match.getHomeAbbr() + " vs " + match.getGuestAbbr())
        .add("Prognose", Match::getExpectedResult).build();
  }

  private MessageEmbed getNextMatch() {
    final Match nextMatch = orgaTeam.getTeam().getMatches().getNextMatch(true);
    final String matchType = nextMatch == null ? "kein Match" : (nextMatch.getTypeString() + " gegen " + nextMatch.getOpponentOf(orgaTeam.getTeam()).getName());
    final String url = nextMatch instanceof PRMMatch primeMatch ? primeMatch.get().getURL() : null;
    final EmbedBuilder builder = new EmbedBuilder()
        .setTitle("Nächstes Match: " + matchType, url)
        .setDescription(nextMatch == null ? "kein Match" : TimeFormat.DISCORD.of(nextMatch.getStart()))
        .setFooter("zuletzt aktualisiert " + TimeFormat.DEFAULT.now());
    if (nextMatch != null) {
      determineMatchLineupFields("Gegner", nextMatch, orgaTeam.getTeam()).forEach(builder::addField);
      determineMatchLineupFields("euer Team", nextMatch, orgaTeam.getTeam()).forEach(builder::addField);
      new MatchLogBuilder(nextMatch, orgaTeam.getTeam()).getFields().forEach(builder::addField);
    }
    return builder.build();
  }

  private List<MessageEmbed.Field> determineMatchLineupFields(String title, Match match, Team team) {
    final Participator participator = match.getParticipator(team);
    final TeamLineupBase lineup = participator.getTeamLineup();
    final List<Player> players = lineup.getFixedLineups().stream().map(Lineup::getPlayer).toList();
    return List.of(
        new MessageEmbed.Field(title, players.stream().map(Player::getSummonerName).collect(Collectors.joining("\n")), false),
        new MessageEmbed.Field("Elo (" + lineup.getAverageRank().toString() + ")", players.stream().map(Player::getLastRank).map(PlayerRank::toString).collect(Collectors.joining("\n")), true)
    );
  }

  private MessageEmbed getOverview() {
    String recordAndSeasons = "";
    String standingPRM = "keine PRM-Teilnahme";
    final String standingTRUE = orgaTeam.getPlace() == null ? "keine TRUE-Cup Teilnahme" : ("TRUE-Rating: Platz" + orgaTeam.getPlace());
    if (orgaTeam.getTeam() instanceof PRMTeam prmTeam) {
      recordAndSeasons = " - " + prmTeam.getRecord().toString();
      final LeagueTeam leagueTeam = prmTeam.getCurrentLeague();
      if (leagueTeam != null) {
        standingPRM = leagueTeam.toString();
      }
    }
    final EmbedBuilder builder = new EmbedBuilder()
        .setTitle(orgaTeam.getName() + " (" + orgaTeam.getAbbreviation() + recordAndSeasons + ")")
        .setDescription(standingPRM + " || " + standingTRUE)
        .setFooter("zuletzt aktualisiert " + TimeFormat.DEFAULT.now());
    final double averageMMR = orgaTeam.getMainMemberships().stream()
        .mapToInt(membership -> membership.getUser().getPlayer().getLastRelevantRank().getRank().getMMR()).average().orElse(0);
    final Rank teamRank = PlayerRank.fromMMR((int) averageMMR);

    new EmbedFieldBuilder<>(orgaTeam.getActiveMemberships().stream().sorted().toList())
        .add("Position", Membership::getPositionString)
        .add("Spieler (og.gg)", membership -> membership.getUser().getMention())
        .add("Elo (" + teamRank + ")", membership -> membership.getUser().getMention())
        .build().forEach(builder::addField);

    new EmbedFieldBuilder<>(orgaTeam.getScheduler().getCalendarEntries())
        .add("nächste Events", calendar -> calendar.getData().get(0))
        .add("Art", calendar -> calendar.getData().get(1))
        .add("Information", calendar -> calendar.getData().get(2))
        .build().forEach(builder::addField);

    new EmbedFieldBuilder<>(new TeamTrainingScheduleHandler(orgaTeam).getTeamAvailabilitySince(LocalDate.now()))
        .add("Trainingstage", TimeRange::display)
        .add("Dauer", TimeRange::duration)
        .add("geplant", timeRange -> timeRange.trainingReserved(orgaTeam))
        .build().forEach(builder::addField);

    final PRMSeason upcomingPRMSeason = SeasonFactory.getUpcomingPRMSeason();
    if (upcomingPRMSeason != null) {
      new EmbedFieldBuilder<>(upcomingPRMSeason.getEvents())
          .add("Zeitpunkt", eventDTO -> eventDTO.getData().get(0))
          .add("PRM-Phase", eventDTO -> eventDTO.getData().get(1))
          .build().forEach(builder::addField);
    }

    final List<EventCalendar> events = new Query<>(EventCalendar.class)
        .where(Condition.Comparer.GREATER_EQUAL, "calendar_end", LocalDateTime.now().plusDays(1)).entityList();
    if (!events.isEmpty()) {
      new EmbedFieldBuilder<>(events.subList(0, Math.min(10, events.size())))
          .add("Zeitpunkt", eventCalendar -> eventCalendar.getRange().displayRange())
          .add("Event", Calendar::getDetails)
          .build().forEach(builder::addField);
    }
    return builder.build();
  }

  private MessageEmbed getScheduling() {
    final EmbedBuilder builder = new EmbedBuilder()
        .setTitle("Terminplanung")
        .setDescription("Terminplanung für " + orgaTeam.getName())
        .setFooter("zuletzt aktualisiert " + TimeFormat.DEFAULT.now());

    final DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
    for (int i = 0; i < 7; i++) {
      new EmbedFieldBuilder<>(new TeamTrainingScheduleHandler(orgaTeam).ofDay(LocalDate.now().plusDays(i)))
          .add(dayOfWeek.plus(i).getDisplayName(TextStyle.FULL, Locale.GERMANY), list -> list.get(0))
          .add("Zeiten oder Ersatz", list -> list.get(1))
          .build().forEach(builder::addField);
    }
    for (int i = 1; i < 3; i++) {
      new EmbedFieldBuilder<>(new TeamTrainingScheduleHandler(orgaTeam).ofWeekStarting(LocalDate.now().plusDays(7 * i)))
          .add("Folgewoche " + i, list -> list.get(0))
          .add("Zeiten oder Ersatz", list -> list.get(1))
          .build().forEach(builder::addField);
    }
    return builder.build();
  }
}
