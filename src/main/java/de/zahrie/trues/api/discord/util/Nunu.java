package de.zahrie.trues.api.discord.util;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.util.Const;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

public final class Nunu extends Willump {
  private static final Nunu instance = new Nunu();

  public static Nunu getInstance() {
    return instance;
  }

  public static class DcMember {
    public static Member getMember(User user) {
      return getInstance().getGuild().getMember(user);
    }
  }

  public static class DiscordRole {
    public static Role getRole(OrgaTeam team) {
      return getRole(team.getRoleManager().getRoleName());
    }

    public static Role getRole(String name) {
      return Nunu.getInstance().getGuild().getRolesByName(name, true).stream().findFirst().orElse(null);
    }

    public static Role getRole(long id) {
      return Nunu.getInstance().getGuild().getRoleById(id);
    }

    public static void addRole(Member member, Role role) {
      Nunu.getInstance().getGuild().addRoleToMember(member, role).queue();
    }

    public static void removeRole(Member member, Role role) {
      Nunu.getInstance().getGuild().removeRoleFromMember(member, role).queue();
    }
  }

  public static class DiscordChannel {
    public static TextChannel getAdminChannel() {
      return Nunu.getInstance().getClient().getTextChannelById(Const.Channels.ADMIN_CHANNEL);
    }

    public static AudioChannel getVoiceChannel(Member member) {
      final GuildVoiceState voiceState = member.getVoiceState();
      return voiceState == null ? null : voiceState.getChannel();
    }

    public static GuildChannel getChannel(long id) {
      return Nunu.getInstance().getGuild().getGuildChannelById(id);
    }

    public static void move(Member member, AudioChannel channel) {
      Nunu.getInstance().getGuild().moveVoiceMember(member, channel).queue();
    }
  }
}
