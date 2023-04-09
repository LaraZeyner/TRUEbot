package de.zahrie.trues.discord.scouting.teaminfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelType;
import de.zahrie.trues.api.coverage.league.model.PRMLeague;
import de.zahrie.trues.api.coverage.lineup.LineupManager;
import de.zahrie.trues.api.coverage.lineup.MatchLineup;
import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import de.zahrie.trues.api.coverage.match.MatchFactory;
import de.zahrie.trues.api.coverage.match.log.MatchLogBuilder;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.match.model.PRMMatch;
import de.zahrie.trues.api.coverage.match.model.TournamentMatch;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.player.model.AbstractRank;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.player.model.Rank;
import de.zahrie.trues.api.coverage.season.OrgaCupSeason;
import de.zahrie.trues.api.coverage.season.SeasonFactory;
import de.zahrie.trues.api.coverage.team.leagueteam.LeagueTeam;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.api.coverage.team.model.Standing;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.discord.builder.embed.EmbedFieldBuilder;
import de.zahrie.trues.util.Util;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

@AllArgsConstructor
public class TeamInfo {
  private final OrgaTeam orgaTeam;
  private Message message;

  public TeamInfo(OrgaTeam orgaTeam) {
    this(orgaTeam, findOrCreate(orgaTeam));
  }

  private static Message findOrCreate(OrgaTeam orgaTeam) {
    final MessageChannel messageChannel = (MessageChannel) orgaTeam.getChannelOf(TeamChannelType.INFO).getChannel();
    return MessageHistory.getHistoryFromBeginning(messageChannel).complete().getRetrievedHistory().stream()
        .filter(message -> !message.getEmbeds().isEmpty()).findFirst().orElse(null);
  }

  public void updateAll() {
    if (message == null) create();
    else message.editMessageEmbeds(getList()).queue();
  }

  private void create() {
    final MessageChannel messageChannel = (MessageChannel) orgaTeam.getChannelOf(TeamChannelType.INFO).getChannel();
    messageChannel.sendMessageEmbeds(getList()).queue(message1 -> this.message = message1);
  }

  private List<MessageEmbed> getList() {
    return List.of(getOverview(), getScheduling(), getNextMatch(), getDivision(), getInternCup());
  }


  private MessageEmbed getDivision() {
    final Team team = orgaTeam.getTeam();
    if (!(team instanceof PRMTeam prmTeam) || prmTeam.getCurrentLeague() == null) {
      return new EmbedBuilder().setTitle("keine Division").setDescription("Das Team ist nicht für einen Prime League Split angemeldet").build();
    }
    final PRMLeague league = (PRMLeague) prmTeam.getCurrentLeague().getLeague();
    final EmbedBuilder builder = new EmbedBuilder()
        .setTitle(league.getStage().getSeason().getName() + " - " + league.getName(), league.getUrl())
        .setDescription("Aktuelle Gruppe im Prime League Split");
    final Set<LeagueTeam> signups = league.getSignups();

    new EmbedFieldBuilder<>(signups.stream().sorted(Comparator.comparing(LeagueTeam::getExpectedScore).reversed()).toList())
        .add("Teamname", l -> l.getTeam().getName())
        .add("Standing", l -> l.getScore().toString())
        .add("Prognose", l -> l.getExpectedScore().toString()).build().forEach(builder::addField);

    final Map<Playday, List<Match>> playdayMatches = new HashMap<>();
    for (final TournamentMatch match : league.getMatches()) {
      if (!playdayMatches.containsKey(match.getPlayday())) playdayMatches.put(match.getPlayday(), new ArrayList<>());
      playdayMatches.get(match.getPlayday()).add(match);
    }
    playdayMatches.keySet().stream().sorted().forEach(playday -> new EmbedFieldBuilder<>(playdayMatches.get(playday).stream().sorted().toList())
        .add("Spielwoche " + playday.getIdx(), match -> TimeFormat.WEEKLY.of(match.getStart()))
        .add("Standing", match -> match.getHomeAbbr() + " vs " + match.getGuestAbbr())
        .add("Prognose", Match::getExpectedResult).build().forEach(builder::addField));
    final int correct = (int) league.getMatches().stream().filter(match -> match.getResultHandler().wasAcurate(match)).count();
    final int incorrect = (int) league.getMatches().stream().filter(match -> Boolean.FALSE.equals(match.getResultHandler().wasAcurate(match))).count();
    builder.addField("Fehlerrate", new Standing(correct, incorrect).getWinrate().toString(), false);
    return builder.build();
  }

  private MessageEmbed getInternCup() {
    final OrgaCupSeason lastSeason = SeasonFactory.getLastInternSeason();
    final OrgaCupSeason upcomingSeason = SeasonFactory.getUpcomingInternSeason();
    final OrgaCupSeason currentSeason = Util.nonNull(SeasonFactory.getLastPRMSeason()).getRange().hasEnded() ? upcomingSeason : lastSeason;
    return new EmbedBuilder()
        .setTitle(currentSeason == null ? "keine Season" : (currentSeason.getFullName() + " - TRUE-Cup - " + currentSeason.getSignupStatusForTeam(orgaTeam.getTeam())))
        .setDescription("Aktueller Spielplan im TRUE-Cup")
        .setFooter("zuletzt aktualisiert " + TimeFormat.DEFAULT.now())
        .build();
    // TODO (Abgie) 05.04.2023: Complete Embed
  }

  private MessageEmbed getNextMatch() {
    final Match nextMatch = MatchFactory.getNextMatch(orgaTeam.getTeam());
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
    final MatchLineup lineup = LineupManager.getMatch(match).getLineup(participator);
    final List<Player> players = lineup.getLineup().stream().map(Lineup::getPlayer).toList();
    return List.of(
        new MessageEmbed.Field(title, players.stream().map(Player::getSummonerName).collect(Collectors.joining("\n")), false),
        new MessageEmbed.Field("Elo (" + lineup.getAverageElo() + ")", players.stream().map(Player::getLastRank).map(Rank::toString).collect(Collectors.joining("\n")), true)
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
    final double averageMMR = orgaTeam.getMemberships().stream().filter(membership -> membership.getRole().equals(TeamRole.MAIN))
        .mapToInt(membership -> membership.getUser().getPlayer().getLastRelevantRank().getMMR()).average().orElse(0);
    final AbstractRank teamRank = Rank.fromMMR((int) averageMMR);
    new EmbedFieldBuilder<>(orgaTeam.getMemberships().stream().toList())
        .add("Position", Membership::getPositionString)
        .add("Spieler (og.gg)", membership -> membership.getUser().getMention())
        .add("Elo (" + teamRank + ")", membership -> membership.getUser().getMention())
        .build().forEach(builder::addField);


    return builder.build();
    // TODO (Abgie) 05.04.2023: Complete Embed
  }

  private MessageEmbed getScheduling() {
    return new EmbedBuilder()
        .setTitle("Terminplanung")
        .setDescription("Terminplanung für " + orgaTeam.getName())
        .setFooter("zuletzt aktualisiert " + TimeFormat.DEFAULT.now())
        .build();
    // TODO (Abgie) 05.04.2023: Complete Embed
  }
}
