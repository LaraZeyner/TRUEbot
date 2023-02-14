package de.zahrie.trues.models.discord.permission;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.models.discord.DiscordGroup;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "discord_permission")
public class CommandPermission implements Serializable {
  @Serial
  private static final long serialVersionUID = -4218162618980334182L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "permission_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @Deprecated
  @Column(name = "target_type", nullable = false, length = 25)
  private String target_type;

  @Column(name = "target", nullable = false, length = 25)
  private String command;

  @Column(name = "permission", nullable = false, length = 50)
  private String subCommand;

  @Column(name = "description", length = 250)
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "discord_group")
  @ToString.Exclude
  private DiscordGroup discordGroup;

  @Column(name = "perm_value", columnDefinition = "TINYINT(1) not null")
  private boolean isHierarchical;

}