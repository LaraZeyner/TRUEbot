package de.zahrie.trues.api.logging;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.discord.user.DiscordUser;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@DiscriminatorValue("member")
public class ServerLog extends OrgaLog implements Serializable {
  @Serial
  private static final long serialVersionUID = -5911622197578986648L;


  @Enumerated(EnumType.STRING)
  @Column(name = "action", nullable = false, length = 50)
  private ServerLogAction action;

  public ServerLog(DiscordUser target, String details, ServerLogAction action) {
    this(null, target, details, action);
  }

  public ServerLog(DiscordUser invoker, DiscordUser target, String details, ServerLogAction action) {
    super(invoker, target, details);
    this.action = action;
  }

  public enum ServerLogAction {
    APPLICATION_CREATED,
    SERVER_JOIN,
    SERVER_LEAVE
  }
}
