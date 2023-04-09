package de.zahrie.trues.api.community.application;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import de.zahrie.trues.api.discord.user.DiscordUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
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
@Entity(name = "Application")
@Table(name = "application", indexes = {@Index(name = "idx_app", columnList = "discord_user, lineup_role, lane", unique = true)})
@NamedQuery(name = "Application.current", query = "SELECT user.mention, role || ' - ' || position, isWaiting FROM Application WHERE isWaiting is not null ORDER BY appTimestamp")
@NamedQuery(name = "Application.pending", query = "SELECT id || '. - ' || user.mention || ' (' || role || ' - ' || position || ')' FROM Application WHERE isWaiting = true ORDER BY appTimestamp")
public class Application implements Serializable {
  @Serial
  private static final long serialVersionUID = -6006729315935528279L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "application_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "discord_user", nullable = false)
  @ToString.Exclude
  private DiscordUser user;

  @Enumerated(EnumType.STRING)
  @Column(name = "lineup_role", nullable = false, length = 6)
  private TeamRole role;

  @Enumerated(EnumType.STRING)
  @Column(name = "lane", length = 15)
  private TeamPosition position;

  @Column(name = "app_timestamp")
  private LocalDateTime appTimestamp = LocalDateTime.now();

  /**
   * Kann drei Werte annehmen <br>
   * <code>true</code> = warte auf Vorstellungsgespräch <br>
   * <code>false</code> = Vorstellungsgespräch abgehalten <br>
   * <code>null</code> = abgelehnt
   */
  @Column(name = "app_accepted")
  private Boolean isWaiting = true;

  @Column(name = "app_notes", length = 2048)
  private String appNotes;

  public Application(DiscordUser user, TeamRole role, TeamPosition position) {
    this.user = user;
    this.role = role;
    this.position = position;
  }

  @Override
  public String toString() {
    return role.name() + " - " + position.name();
  }
}
