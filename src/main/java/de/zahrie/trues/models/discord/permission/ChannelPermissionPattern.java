package de.zahrie.trues.models.discord.permission;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import de.zahrie.trues.models.discord.DiscordChannel;
import de.zahrie.trues.models.discord.DiscordGroup;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("not null")
public class ChannelPermissionPattern extends PermissionPattern implements Serializable {
  @Serial
  private static final long serialVersionUID = -5849848718139199008L;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "group_scope_id", nullable = false)
  @ToString.Exclude
  private DiscordGroup group;

  @Column(name = "value")
  private Boolean value;

  @OneToMany(mappedBy = "pattern")
  @ToString.Exclude
  private Set<DiscordChannel> channels = new LinkedHashSet<>();

}