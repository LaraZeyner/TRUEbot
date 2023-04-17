package de.zahrie.trues.api.discord.group;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.discord.util.Nunu;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
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
@Table(name = "discord_group")
@NamedQuery(name = "CustomDiscordRole.fromDiscordId", query = "FROM CustomDiscordRole WHERE discordId = :discordId")
public class CustomDiscordRole implements Serializable {
  @Serial
  private static final long serialVersionUID = -2307398301886813719L;

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
