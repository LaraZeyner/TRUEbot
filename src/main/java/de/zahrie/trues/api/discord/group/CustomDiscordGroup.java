package de.zahrie.trues.api.discord.group;

import java.io.Serial;
import java.util.List;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.discord.util.Nunu;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;

@Getter
@Setter
@Table("discord_group")
public class CustomDiscordGroup implements Entity<CustomDiscordGroup> {
  @Serial
  private static final long serialVersionUID = 2036772268090427497L;

  private int id; // discord_group_id
  private final long discordId; // discord_id
  private String name; // role_name
  private final GroupType type; // role_type
  private final boolean fixed; // fixed
  @Getter(AccessLevel.NONE)
  private int orgaTeamId;
  @Setter(AccessLevel.NONE)
  private OrgaTeam team;

  public OrgaTeam getOrgaTeam() {
    if (team == null) this.team = new Query<>(OrgaTeam.class).where("team", this).entity();
    return team;
  }

  public CustomDiscordGroup(long discordId, String name, GroupType type, boolean fixed, OrgaTeam team) {
    this.discordId = discordId;
    this.name = name;
    this.type = type;
    this.fixed = fixed;
    this.team = team;
  }

  public CustomDiscordGroup(int id, long discordId, String name, GroupType type, boolean fixed, int orgaTeamId) {
    this.id = id;
    this.discordId = discordId;
    this.name = name;
    this.type = type;
    this.fixed = fixed;
    this.orgaTeamId = orgaTeamId;
  }

  public static CustomDiscordGroup get(List<Object> objects) {
    return new CustomDiscordGroup(
        (int) objects.get(0),
        (long) objects.get(1),
        (String) objects.get(2),
        new SQLEnum<>(GroupType.class).of(objects.get(3)),
        (boolean) objects.get(4),
        new Query<>(OrgaTeam.class).where("team_role", objects.get(0)).id()
    );
  }

  @Override
  public CustomDiscordGroup create() {
    final CustomDiscordGroup discordGroup = new Query<>(CustomDiscordGroup.class).key("discord_id", discordId)
        .col("role_name", name).col("role_type", type).col("fixed", fixed)
        .insert(this);
    if (team != null) team.setGroup(discordGroup);
    return discordGroup;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setName(String name) {
    if (!this.name.equals(name)) new Query<>(CustomDiscordGroup.class).col("role_name", name).update(id);
    this.name = name;
  }

  public void setOrgaTeamId(int orgaTeamId) {
    if (orgaTeamId != this.orgaTeamId) team = null;
    this.orgaTeamId = orgaTeamId;
  }

  public Role determineRole() {
    return Nunu.getInstance().getGuild().getRoleById(discordId);
  }

  public IPermissionContainer getChannel() {
    return (IPermissionContainer) Nunu.DiscordChannel.getChannel(discordId);
  }

  public void updatePermissions() {
    if (determineRole() != null) {
      determineRole().getManager().setPermissions(type.getPattern().getAllowed()).queue();
    }
  }
}
