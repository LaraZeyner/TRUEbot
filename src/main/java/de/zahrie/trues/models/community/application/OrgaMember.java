package de.zahrie.trues.models.community.application;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.database.types.TimeCoverter;
import de.zahrie.trues.models.community.OrgaTeam;
import de.zahrie.trues.models.discord.member.DiscordMember;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "OrgaMember")
@Table(name = "orga_member", indexes = {@Index(name = "idx_app", columnList = "discord_user, lineup_role, lane, orga_team", unique = true)})
public class OrgaMember implements Serializable {
  @Serial
  private static final long serialVersionUID = -6006729315935528279L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "lineup_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "discord_user", nullable = false)
  @ToString.Exclude
  private DiscordMember member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "orga_team")
  @ToString.Exclude
  private OrgaTeam orgaTeam;

  @Enumerated(EnumType.STRING)
  @Column(name = "lineup_role", nullable = false, length = 8)
  private OrgaRole role;

  @Enumerated(EnumType.STRING)
  @Column(name = "lane", nullable = false, length = 15)
  private OrgaLane lane;

  @Column(name = "substitude", nullable = false)
  private boolean isSubstitude;

  @Column(name = "looking_for_team", nullable = false)
  private boolean isLookingForTeam;

  @Type(TimeCoverter.class)
  @Column(name = "app_timestamp")
  private Time appTimestamp = new Time();

  @Column(name = "app_accepted")
  private Boolean isAccepted;

  @Column(name = "app_notes", length = 2048)
  private String appNotes;

}
