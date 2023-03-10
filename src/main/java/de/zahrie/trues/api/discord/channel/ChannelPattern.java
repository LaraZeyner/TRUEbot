package de.zahrie.trues.api.discord.channel;

import de.zahrie.trues.api.discord.permissible.PermissionPattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.Permission;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChannelPattern extends PermissionPattern {
  //TODO (Abgie) 08.03.2023:
  public static final ChannelPattern STANDARD_ACCESS = new ChannelPattern();
  public static final ChannelPattern ANNOUNCEMENT = new ChannelPattern(Permission.NICKNAME_CHANGE).addPattern(CHANNEL_INTERACT_TALK);
  public static final ChannelPattern ORGA_MEMBER = new ChannelPattern().addPattern(CHANNEL_INTERACT_ADVANCED);
  public static final ChannelPattern STAFF = new ChannelPattern().addPattern(GUILD_MODERATE).addPattern(CHANNEL_INTERACT_MODERATE);
  public static final ChannelPattern ADMIN = new ChannelPattern().addPattern(GUILD_ADMINISTRATE).addPattern(CHANNEL_INTERACT_MODERATE);
  public static final ChannelPattern CONTENT = new ChannelPattern().addPattern(CONTENT_CREATION);

  public ChannelPattern(Permission... permissions) {
    super(permissions);
  }

  public ChannelPattern addPattern(PermissionPattern pattern) {
    permissions.addAll(pattern.getPermissions());
    return this;
  }

}
