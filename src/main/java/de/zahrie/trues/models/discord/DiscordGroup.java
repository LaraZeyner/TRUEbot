package de.zahrie.trues.models.discord;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import de.zahrie.trues.models.discord.member.DiscordMember;
import de.zahrie.trues.models.discord.permission.PermissionPattern;
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
@Table(name = "discord_group",
        indexes = { @Index(name = "group_name", columnList = "group_name", unique = true),
                @Index(name = "discord_group_id", columnList = "discord_group_id", unique = true) })
public class DiscordGroup implements Serializable {
  @Serial
  private static final long serialVersionUID = 7892470699010252111L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "scope_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @Column(name = "discord_group_id", nullable = false)
  private long roleId;

  @Column(name = "group_name", nullable = false, length = 100)
  private String name;

  @Column(name = "group_desc", length = 100)
  private String description;

  @Column(name = "hierarchical", nullable = false)
  private boolean isHierarchical = false;

  @ManyToOne(fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "grant_requirement", nullable = true)
  @ToString.Exclude
  private DiscordGroup grantRequired;

  @Enumerated(EnumType.STRING)
  @Column(name = "group_type", length = 25)
  private GroupType groupType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "server_permission")
  @ToString.Exclude
  //TODO should be not null
  private PermissionPattern serverPattern;

  @OneToMany(mappedBy = "highestGroup")
  @ToString.Exclude
  private Set<DiscordMember> members = new LinkedHashSet<>();

}