package de.zahrie.trues.api.coverage.participator.model;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannel;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelType;
import de.zahrie.trues.api.coverage.match.log.MatchLogBuilder;
import de.zahrie.trues.api.coverage.match.model.LeagueMatch;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.discord.util.Nunu;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jetbrains.annotations.Nullable;

public record ScheduledEventHandler(Participator participator) {

  @Nullable
  public ScheduledEvent getEvent() {
    if (participator.getDiscordEventId() == null) return null;
    return Nunu.getInstance().getGuild().getScheduledEventById(participator.getDiscordEventId());
  }
  public void updateScheduledEvent() {
    if (participator.getTeamId() == null) return;
    if (participator.getMatch().getStart().isBefore(LocalDateTime.now())) return;

    final Team team = participator.getTeam();
    assert team != null;
    final OrgaTeam orgaTeam = team.getOrgaTeam();
    if (orgaTeam == null) return;

    final TeamChannel teamChannel = orgaTeam.getChannels().get(TeamChannelType.PRACTICE);
    if (teamChannel == null) return;

    final ScheduledEvent event = getEvent();
    if (event == null) {
      createScheduledEvent();
      return;
    }

    event.getManager().setName(title()).setLocation(practiceChannel()).setStartTime(start())
        .setDescription(description())
        .setEndTime(end())
        .queue();
  }

  private void createScheduledEvent() {
    Nunu.getInstance().getGuild().createScheduledEvent(title(), practiceChannel(), start())
        .setDescription(description())
        .setEndTime(end())
        .queue(scheduledEvent -> participator.setDiscordEventId(scheduledEvent.getIdLong()));
  }

  private String description() {
    String description = new MatchLogBuilder(participator.getMatch(), participator.getTeam()).toString();
    if (participator.getMatch() instanceof LeagueMatch leagueMatch)
      description += "https://www.primeleague.gg/leagues/matches/" + leagueMatch.getMatchId();
    return description;
  }

  private OffsetDateTime start() {
    return participator.getMatch().getStart().atZone(ZoneId.systemDefault()).toOffsetDateTime();
  }

  private OffsetDateTime end() {
    return participator.getMatch().getExpectedTimeRange().getEndTime().atZone(ZoneId.systemDefault()).toOffsetDateTime();
  }

  private String title() {
    return participator.getMatch().toString();
  }

  private GuildChannel practiceChannel() {
    final Team team = participator.getTeam();
    assert team != null;
    final TeamChannel teamChannel = team.getOrgaTeam().getChannels().get(TeamChannelType.PRACTICE);
    assert teamChannel != null;
    return teamChannel.getChannel();
  }
}
