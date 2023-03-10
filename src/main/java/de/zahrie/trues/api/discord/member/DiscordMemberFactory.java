package de.zahrie.trues.api.discord.member;

import de.zahrie.trues.database.Database;
import net.dv8tion.jda.api.entities.Member;

/**
 * Created by Lara on 04.03.2023 for TRUEbot
 */
public class DiscordMemberFactory {
  public static DiscordMember getMember(Member member) {
    final long memberId = member.getIdLong();
    return Database.Find.find(DiscordMember.class, memberId);
  }
}
