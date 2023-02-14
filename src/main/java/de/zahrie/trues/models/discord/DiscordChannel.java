package de.zahrie.trues.models.discord;

import de.zahrie.trues.models.discord.permission.ChannelPermissionPattern;
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

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "DiscordChannel")
@Table(name = "discord_channel")
public class DiscordChannel implements Serializable {
  private static final long serialVersionUID = 3260875961946049435L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", columnDefinition = "TINYINT UNSIGNED not null")
  private short id;

  @Column(name = "channel_name")
  private String name;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "channel_pattern", nullable = false)
  private ChannelPermissionPattern pattern;

}