package de.zahrie.trues.discord.notify;

import java.time.LocalTime;

import de.zahrie.trues.api.calendar.TeamCalendar;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.discord.user.DiscordUser;
import lombok.Getter;

@Getter
public class TeamEventNotifier extends Notifier {
  private final TeamCalendar teamCalendar;

  public TeamEventNotifier(LocalTime localTime, DiscordUser discordUser, TeamCalendar teamCalendar) {
    super(localTime, discordUser);
    this.teamCalendar = teamCalendar;
  }

  @Override
  public void sendNotification() {
    final OrgaTeam orgaTeam = teamCalendar.getOrgaTeam();
    handleNotification(orgaTeam, "Event " + teamCalendar, teamCalendar.getRange());
  }
}
