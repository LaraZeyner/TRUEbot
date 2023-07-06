package de.zahrie.trues.discord.notify;

import java.time.LocalTime;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.team.model.AbstractTeam;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.util.Util;
import lombok.Getter;

@Getter
public class MatchNotifier extends Notifier {
  private final Participator participator;

  public MatchNotifier(LocalTime localTime, DiscordUser discordUser, Participator participator) {
    super(localTime, discordUser);
    this.participator = participator;
  }

  @Override
  public void sendNotification() {
    final OrgaTeam orgaTeam = Util.avoidNull(participator.getTeam(), null, AbstractTeam::getOrgaTeam);
    handleNotification(orgaTeam, "Match " + participator.getMatch().getMatchup(), participator.getMatch().getExpectedTimeRange());
  }
}
