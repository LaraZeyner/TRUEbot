package de.zahrie.trues.api.community.orgateam.teamchannel;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.discord.channel.DiscordChannel;
import de.zahrie.trues.api.discord.channel.PermissionChannelType;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@DiscriminatorValue("not null")
public class TeamChannel extends DiscordChannel implements Serializable {
  @Serial
  private static final long serialVersionUID = -6665003217262393566L;

  @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "orga_team")
  @ToString.Exclude
  private OrgaTeam orgaTeam;

  @Enumerated(EnumType.STRING)
  @Column(name = "teamchannel_type")
  private TeamChannelType teamChannelType;

  public TeamChannel(long discordId, String name, PermissionChannelType permissionType, ChannelType channelType, OrgaTeam orgaTeam, TeamChannelType teamChannelType) {
    super(discordId, name, permissionType, channelType);
    this.orgaTeam = orgaTeam;
    this.teamChannelType = teamChannelType;
  }

  @Override
  public boolean updatePermission(Role role) {
    final Role teamRole = orgaTeam.getRoleManager().getRole();
    if (teamRole.equals(role)) {
      updateForGroup(DiscordGroup.TEAM_ROLE_PLACEHOLDER);
      return true;
    }
    return super.updatePermission(role);
  }

  @Override
  protected void updateForGroup(DiscordGroup group) {
    final Role role = orgaTeam.getRoleManager().getRole();
    uFr(role, group);
  }

}
