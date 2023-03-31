package de.zahrie.trues.api.discord.group;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
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
@Entity(name = "CustomDiscordRole")
@Table(name = "discord_role")
@NamedQuery(name = "CustomDiscordRole.fromDiscordId", query = "FROM CustomDiscordRole WHERE discordId = :discordId")
public class CustomDiscordGroup implements Serializable {
  @Serial
  private static final long serialVersionUID = -2307398301886813719L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", columnDefinition = "TINYINT UNSIGNED not null")
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

  public CustomDiscordGroup(long discordId, String name, GroupType type, boolean fixed, OrgaTeam team) {
    this.discordId = discordId;
    this.name = name;
    this.type = type;
    this.fixed = fixed;
    this.team = team;
  }

  public Role getRole() {
    return Nunu.DiscordRole.getRole(discordId);
  }

  public IPermissionContainer getChannel() {
    return (IPermissionContainer) Nunu.DiscordChannel.getChannel(discordId);
  }

  public void updatePermissions() {
    if (getRole() != null) {
      getRole().getManager().setPermissions(type.getPattern().getAllowed()).queue();
    }
  }
}
