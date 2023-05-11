package de.zahrie.trues.api.coverage.team.model;

import java.time.LocalDateTime;
import java.util.List;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.season.signup.SeasonSignup;

public interface ATeam {
  String getName();
  void setName(String name);
  String getAbbreviation();
  void setAbbreviation(String abbreviation);
  LocalDateTime getRefresh();
  void setRefresh(LocalDateTime refresh);
  OrgaTeam getOrgaTeam();
  void setOrgaTeam(OrgaTeam orgaTeam);
  boolean isHighlight();
  void setHighlight(boolean highlight);
  Integer getLastMMR();
  void setLastMMR(Integer mmr);
  List<Participator> getParticipators();
  List<SeasonSignup> getSignups();
  List<Player> getPlayers();
}
