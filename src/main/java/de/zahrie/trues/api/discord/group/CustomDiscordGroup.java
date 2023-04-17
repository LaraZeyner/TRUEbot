package de.zahrie.trues.api.discord.group;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.discord.util.Nunu;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "discord_group")
public class CustomDiscordGroup implements Serializable {
  @Serial
  private static final long serialVersionUID = -2307398301886813719L;

  public static CustomDiscordGroup build(long discordId, String name, GroupType type, boolean fixed) {
    final var discordGroup = new CustomDiscordGroup(discordId, name, type, fixed, null);
    Database.insert(discordGroup);
    return discordGroup;
  }

  public static CustomDiscordGroup build(long discordId, String name, GroupType type, boolean fixed, OrgaTeam team) {
    final var discordGroup = new CustomDiscordGroup(discordId, name, type, fixed, team);
    Database.insert(discordGroup);
    team.setGroup(discordGroup);
    Database.update(team);
    return discordGroup;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "role_id", columnDefinition = "TINYINT UNSIGNED not null")
  private short id;

  @Column(name = "discord_id", nullable = false)
  private long discordId;

  @Column(name = "role_name", nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "role_type", nullable = false)
  private GroupType type;

  @Column(name = "fixed", nullable = false)
  private boolean fixed;

  @OneToOne(mappedBy = "group")
  private OrgaTeam team;

  private CustomDiscordGroup(long discordId, String name, GroupType type, boolean fixed, OrgaTeam team) {
    this.discordId = discordId;
    this.name = name;
    this.type = type;
    this.fixed = fixed;
    this.team = team;
  }

  public long discordId() {
    try {
      return discordId;
    } catch (IllegalStateException exception) {
      return QueryBuilder.hql(CustomDiscordGroup.class, "FROM CustomDiscordGroup WHERE id = " + id).single().discordId();
    }
  }

  public Role determineRole() {
    final long discordId = discordId();
    return Nunu.getInstance().getGuild().getRoleById(discordId);
  }

  public IPermissionContainer getChannel() {
    return (IPermissionContainer) Nunu.DiscordChannel.getChannel(discordId());
  }

  public void updatePermissions() {
    if (determineRole() != null) {
      determineRole().getManager().setPermissions(type.getPattern().getAllowed()).queue();
    }
  }
}
