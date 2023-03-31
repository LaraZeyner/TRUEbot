package de.zahrie.trues.api.discord.user;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.group.GroupAssignReason;
import de.zahrie.trues.database.types.TimeCoverter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "discord_user_group")
@NamedQuery(name = "DiscordUserGroup.toRemove", query = "FROM DiscordUserGroup WHERE isActive = true AND permissionEnd is not null AND permissionEnd > NOW()")
public class DiscordUserGroup implements Serializable {
  @Serial
  private static final long serialVersionUID = -763378764697829834L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "usergroup_id", nullable = false)
  private int id;


  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "discord_user", nullable = false)
  @ToString.Exclude
  private DiscordUser user;

  @Column(name = "discord_group", nullable = false)
  @Enumerated(EnumType.STRING)
  private DiscordGroup discordGroup;

  @Temporal(TemporalType.TIMESTAMP)
  @Type(TimeCoverter.class)
  @Column(name = "assign_time", nullable = false)
  private Time start = new Time();

  @Temporal(TemporalType.TIMESTAMP)
  @Type(TimeCoverter.class)
  @Column(name = "permission_end", nullable = false)
  private Time permissionEnd;

  @Enumerated(EnumType.STRING)
  @Column(name = "reason", nullable = false, length = 8)
  private GroupAssignReason reason = GroupAssignReason.ADD;

  @Column(name = "active", nullable = false)
  private boolean isActive = false;

  public DiscordUserGroup(DiscordUser user, DiscordGroup discordGroup, Time permissionStart, Time permissionEnd) {
    this.user = user;
    this.discordGroup = discordGroup;
    this.start = permissionStart;
    this.permissionEnd = permissionEnd;
  }
}