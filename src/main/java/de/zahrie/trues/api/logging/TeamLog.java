package de.zahrie.trues.api.logging;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.discord.user.DiscordUser;
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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@DiscriminatorValue("team")
public class TeamLog extends OrgaLog implements Serializable {
  @Serial
  private static final long serialVersionUID = 560693874046983660L;


  @Enumerated(EnumType.STRING)
  @Column(name = "action", nullable = false, length = 50)
  private TeamLogAction action;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "team", nullable = false)
  @ToString.Exclude
  private OrgaTeam team;

  public TeamLog(DiscordUser target, String details, TeamLogAction action, OrgaTeam team) {
    this(null, target, details, action, team);
  }

  public TeamLog(DiscordUser invoker, DiscordUser target, String details, TeamLogAction action, OrgaTeam team) {
    super(invoker, target, details);
    this.action = action;
    this.team = team;
  }

  public enum TeamLogAction {
    LINEUP_JOIN,
    LINEUP_LEAVE
  }
}
