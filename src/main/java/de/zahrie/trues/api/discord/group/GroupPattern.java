package de.zahrie.trues.api.discord.group;

import de.zahrie.trues.api.discord.permissible.PermissionPattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.Permission;

@EqualsAndHashCode(callSuper = true)
@Data
public class GroupPattern extends PermissionPattern {
  public static final GroupPattern DEFAULT = new GroupPattern(Permission.NICKNAME_CHANGE).addPattern(CHANNEL_INTERACT_TALK);
  public static final GroupPattern PINGABLE = new GroupPattern();
  public static final GroupPattern ORGA_MEMBER = new GroupPattern().addPattern(CHANNEL_INTERACT_ADVANCED);
  public static final GroupPattern STAFF = new GroupPattern().addPattern(GUILD_MODERATE).addPattern(CHANNEL_INTERACT_MODERATE);
  public static final GroupPattern ADMIN = new GroupPattern().addPattern(GUILD_ADMINISTRATE).addPattern(CHANNEL_INTERACT_MODERATE);
  public static final GroupPattern CONTENT = new GroupPattern().addPattern(CONTENT_CREATION);

  public GroupPattern(Permission... permissions) {
    super(permissions);
  }

  public GroupPattern addPattern(PermissionPattern pattern) {
    permissions.addAll(pattern.getPermissions());
    return this;
  }

}
