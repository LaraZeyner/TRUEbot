package de.zahrie.trues.api.discord.user;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.group.GroupAssignReason;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "discord_user_group")
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

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "startTime", column = @Column(name = "assign_time", nullable = false)),
      @AttributeOverride(name = "endTime", column = @Column(name = "permission_end", nullable = false))
  })
  private TimeRange range;

  @Column(name = "permission_end", nullable = false)
  private LocalDateTime permissionEnd;

  @Enumerated(EnumType.STRING)
  @Column(name = "reason", nullable = false, length = 8)
  private GroupAssignReason reason = GroupAssignReason.ADD;

  @Column(name = "active", nullable = false)
  private boolean isActive = false;

  public DiscordUserGroup(DiscordUser user, DiscordGroup discordGroup, TimeRange range) {
    this.user = user;
    this.discordGroup = discordGroup;
    this.range = range;
  }
}