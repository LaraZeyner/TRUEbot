package de.zahrie.trues.api.discord.util;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.discord.user.DiscordUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

public final class Nunu extends Willump {
  private static final Nunu instance = new Nunu();

  public static Nunu getInstance() {
    return instance;
  }

  public static class DiscordRole {
    public static Role getRole(OrgaTeam team) {
      final String name = team.getTeam().getName();
      return getRole(name);
    }

    public static Role getRole(String name) {
      return Nunu.getInstance().getGuild().getRolesByName(name, true).stream().findFirst().orElse(null);
    }

    public static Role getRole(long id) {
      // TODO (Abgie) 15.03.2023: never used
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

    public static AudioChannel getVoiceChannel(Member member) {
      final GuildVoiceState voiceState = member.getVoiceState();
      return voiceState == null ? null : voiceState.getChannel();
    }
    public static VoiceChannel getVoiceChannel(long id) {
      return Nunu.getInstance().getGuild().getVoiceChannelById(id);
    }

    public static GuildChannel getChannel(long id) {
      return Nunu.getInstance().getGuild().getGuildChannelById(id);
    }

    public static boolean limitTo(VoiceChannel voiceChannel, int amount) {
      if (amount < 0 || amount > 99) {
        return false;
      }
      voiceChannel.getManager().setUserLimit(amount).queue();
      return true;
    }

    public static void move(Member member, AudioChannel channel) {
      Nunu.getInstance().getGuild().moveVoiceMember(member, channel).queue();
    }

    public static VoiceChannel getChannelLike(String regex, Member invoker) {
      // TODO (Abgie) 15.03.2023: never used
      return Nunu.getInstance().getGuild().getVoiceChannelCache().stream()
          .filter(voiceChannel -> voiceChannel.getName().toLowerCase().contains(regex.toLowerCase()))
          .filter(voiceChannel -> invoker.getPermissions(voiceChannel).contains(Permission.VOICE_CONNECT))
          .findFirst().orElse(null);
    }
  }

  public static class DiscordMessager {
    public static void dm(DiscordUser user, String content) {
      user.getMember().getUser().openPrivateChannel()
          .flatMap(privateChannel -> privateChannel.sendMessage(content))
          .queue();
    }
  }
}
