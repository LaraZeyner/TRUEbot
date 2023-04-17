package de.zahrie.trues.discord.scouting.teaminfo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.database.QueryBuilder;
import lombok.extern.java.Log;

@Log
public class TeamInfoManager {
  private static final Map<OrgaTeam, TeamInfo> infos = new HashMap<>();
  private static final Set<OrgaTeam> toUpdate = new HashSet<>();

  public static TeamInfo fromTeam(OrgaTeam orgaTeam) {
    TeamInfo info = infos.getOrDefault(orgaTeam, null);
    if (info != null) return info;

    log.info("Lade " + orgaTeam.getName());
    info = new TeamInfo(orgaTeam);
    infos.put(orgaTeam, info);
    return info;
  }

  static void addTeam(OrgaTeam orgaTeam) {
    toUpdate.add(orgaTeam);
  }

  private static void load(OrgaTeam orgaTeam) {
    final TeamInfo info = fromTeam(orgaTeam);
    if (info.getMessage() == null) info.create();
    else info.getMessage().editMessageEmbeds(info.getList()).queue();
    info.setLastUpdate(LocalDateTime.now());
  }

  public static void loadAllData() {
    for (OrgaTeam fromOrgaTeam : QueryBuilder.hql(OrgaTeam.class, "FROM OrgaTeam").list()) {
      final TeamInfo info = fromTeam(fromOrgaTeam);
      if (Duration.between(info.getLastUpdate(), LocalDateTime.now()).get(ChronoUnit.SECONDS) >= 24*3600) toUpdate.add(fromOrgaTeam);
    }
    toUpdate.forEach(TeamInfoManager::load);
    toUpdate.clear();
  }
}