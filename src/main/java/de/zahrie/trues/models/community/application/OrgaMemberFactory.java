package de.zahrie.trues.models.community.application;

import java.util.List;

import de.zahrie.trues.api.discord.member.DiscordMember;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.models.community.OrgaTeam;

/**
 * Created by Lara on 06.03.2023 for TRUEbot
 */
public class OrgaMemberFactory {
  public static List<OrgaMember> getCurrentTeams(DiscordMember member) {
    return Database.Find.findList(OrgaMember.class, new String[]{"member"}, new Object[]{member}, "ofMember");
  }

  public static OrgaMember getMostImportantTeam(DiscordMember member) {
    return getCurrentTeams(member).stream().sorted().findFirst().orElse(null);
  }

  public static OrgaMember getOfTeam(DiscordMember member, OrgaTeam team) {
    return getCurrentTeams(member).stream().filter(member1 -> member1.getOrgaTeam().equals(team)).findFirst().orElse(null);
  }

  public static OrgaMember getOfTeam(OrgaTeam team, TeamRole role, TeamPosition position) {
    return team.getOrgaMembers().stream().filter(member -> member.getRole().equals(role))
        .filter(member -> member.getPosition().equals(position)).findFirst().orElse(null);
  }

  public static OrgaMember getOfRole(DiscordMember member, TeamRole role) {
    // TODO (Abgie) 15.03.2023: never used
    return member.getApps().stream().filter(app -> app.getRole().equals(role)).findFirst().orElse(null);
  }

  public static List<OrgaMember> getCaptainRoles(DiscordMember member) {
    return Database.Find.findList(OrgaMember.class, new String[]{"member"}, new Object[]{member}, "ofMemberCaptain");
  }

  public static OrgaMember getCaptainsOfTeam(DiscordMember member, TeamRole role) {
    // TODO (Abgie) 15.03.2023: never used
    return member.getApps().stream().filter(app -> app.getRole().equals(role)).findFirst().orElse(null);
  }
}
