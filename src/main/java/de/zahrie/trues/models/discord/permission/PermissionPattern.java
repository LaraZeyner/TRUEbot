package de.zahrie.trues.models.discord.permission;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

import de.zahrie.trues.models.discord.DiscordGroup;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
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
@Table(name = "discord_permission_pattern")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("null")
@DiscriminatorColumn(name = "discord_group")
public class PermissionPattern implements Serializable {
  @Serial
  private static final long serialVersionUID = 7413625678786848170L;

  @Id
  @Column(name = "pattern_name", nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "permission", nullable = false)
  private DiscordPermissionType permission;

  @OneToMany(mappedBy = "serverPattern")
  @ToString.Exclude
  private Set<DiscordGroup> serverGroups;
}