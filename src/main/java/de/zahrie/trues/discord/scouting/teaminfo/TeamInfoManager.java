package de.zahrie.trues.discord.scouting.teaminfo;

import java.util.HashMap;
import java.util.Map;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;

public class TeamInfoManager {
  private static final Map<OrgaTeam, TeamInfo> infos = new HashMap<>();

  public static TeamInfo fromTeam(OrgaTeam orgaTeam) {
    TeamInfo info = infos.getOrDefault(orgaTeam, null);
    if (info != null) return info;

    info = new TeamInfo(orgaTeam);
    infos.put(orgaTeam, info);
    return info;
  }
}
