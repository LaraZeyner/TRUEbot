package de.zahrie.trues.discord;

import de.zahrie.trues.api.discord.Willump;
import de.zahrie.trues.models.community.OrgaTeam;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

public final class Nunu extends Willump {
  public static void run() {
    Willump.run();
  }

  public static class DiscordRole {
    public static Role getRole(OrgaTeam team) {
      final String name = team.getTeam().getName();
      return getRole(name);
    }

    public static Role getRole(String name) {
      return Nunu.guild.getRolesByName(name, true).stream().findFirst().orElse(null);
    }

    public static Role getRole(long id) {
      return Nunu.guild.getRoleById(id);
    }

    public static void addRole(Member member, Role role) {
      Nunu.guild.addRoleToMember(member, role).queue();
    }

    public static void removeRole(Member member, Role role) {
      Nunu.guild.removeRoleFromMember(member, role).queue();
    }
  }

  public static class DiscordChannel {

    public static AudioChannel getChannel(Member member) {
      final GuildVoiceState voiceState = member.getVoiceState();
      return voiceState == null ? null : voiceState.getChannel();
    }
    public static VoiceChannel getChannel(long id) {
      return Nunu.guild.getVoiceChannelById(id);
    }

    public static boolean limitTo(VoiceChannel voiceChannel, int amount) {
      if (amount < 0 || amount > 99) {
        return false;
      }
      voiceChannel.getManager().setUserLimit(amount).queue();
      return true;
    }

    public static void move(Member member, AudioChannel channel) {
      Nunu.guild.moveVoiceMember(member, channel).queue();
    }

    public static VoiceChannel getChannelLike(String regex, Member invoker) {
      return Nunu.guild.getVoiceChannelCache().stream()
          .filter(voiceChannel -> voiceChannel.getName().toLowerCase().contains(regex.toLowerCase()))
          .filter(voiceChannel -> invoker.getPermissions(voiceChannel).contains(Permission.VOICE_CONNECT))
          .findFirst().orElse(null);
    }
  }
}