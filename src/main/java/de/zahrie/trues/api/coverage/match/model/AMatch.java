package de.zahrie.trues.api.coverage.match.model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.ABetable;
import de.zahrie.trues.api.coverage.match.MatchResult;
import de.zahrie.trues.api.coverage.match.log.EventStatus;
import de.zahrie.trues.api.coverage.match.log.MatchLog;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.team.model.TeamBase;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.discord.notify.NotificationManager;

public interface AMatch extends ABetable {
  Playday getPlayday(); // matchday
  MatchFormat getFormat(); // coverage_format
  LocalDateTime getStart(); // coverage_start
  void setStart(LocalDateTime start);
  short getRateOffset(); // rate_offset
  EventStatus getStatus(); // status
  void setStatus(EventStatus status);
  String getLastMessage(); // last_message
  void setLastMessage(String lastMessage);
  boolean isActive(); // active
  MatchResult getResult(); // result
  void setResult(MatchResult result);
  Participator[] getParticipators();
  List<MatchLog> getLogs();

  default List<MatchLog> determineLog() {
    return new Query<MatchLog>().where("coverage", this).entityList();
  }

  default void handleNotifications() {
    if (getStart().isBefore(LocalDateTime.now().plusDays(1))) {
      Arrays.stream(getParticipators()).filter(participator -> participator.getTeam() != null)
          .filter(participator -> participator.getTeam().getOrgaTeam() != null).forEach(NotificationManager::addNotifiersFor);
    }
  }

  default Participator getHome() {
    return getParticipators()[0];
  }

  default String getHomeAbbr() {
    return getHome().getAbbreviation();
  }

  default String getHomeName() {
    return getHome().getName();
  }

  default String getMatchup() {
    return getHomeName() + " vs. " + getGuestName();
  }

  default Participator getGuest() {
    return getParticipators()[1];
  }

  default String getGuestAbbr() {
    return getGuest().getAbbreviation();
  }

  default String getGuestName() {
    return getGuest().getName();
  }

  String getExpectedResult();

  default Participator getOpponent(TeamBase team) {
    final Participator participator = getParticipator(team);
    return participator == null ? null : getParticipator(!participator.isHome());
  }

  default Participator getParticipator(TeamBase team) {
    return Arrays.stream(getParticipators()).filter(participator -> participator.getTeam().equals(team)).findFirst().orElse(null);
  }

  default TeamBase getOpponentOf(TeamBase team) {
    return getOpponent(team).getTeam();
  }

  default List<OrgaTeam> getOrgaTeams() {
    return Arrays.stream(getParticipators()).map(Participator::getTeam).map(TeamBase::getOrgaTeam).filter(Objects::nonNull).toList();
  }

  default boolean isOrgagame() {
    return !getOrgaTeams().isEmpty();
  }

  default Participator getParticipator(boolean home) {
    return getParticipators()[home ? 0 : 1];
  }

  default boolean checkAddParticipatingTeam(Participator participator, TeamBase team) {
    final Participator currentParticipator = getParticipator(team);
    if (currentParticipator != null) {
      if (currentParticipator.isHome() == participator.isHome()) return false;
      currentParticipator.delete();
    }
    return true;
  }

  default Participator addParticipator(TeamBase team, boolean home) {
    final Participator existing = getParticipator(team);
    if (existing != null) return getParticipator(team);

    if (isOrgagame()) {
      team.setRefresh(getStart());
      if (this instanceof PRMMatch) team.setHighlight(true);
    }
    getParticipator(home).setTeam(team);
    handleNotifications();

    return getParticipator(home);
  }

  String getTypeString();

  default boolean isRunning() {
    return getResult().toString().equals("-:-");
  }

  default TimeRange getExpectedTimeRange() {
    return new TimeRange(getStart(), getFormat().getPRMDuration());
  }
}
