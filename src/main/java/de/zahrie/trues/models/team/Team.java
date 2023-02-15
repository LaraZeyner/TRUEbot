package de.zahrie.trues.models.team;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

import de.zahrie.trues.models.community.OrgaTeam;
import de.zahrie.trues.models.coverage.Participator;
import de.zahrie.trues.models.player.Player;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DiscriminatorFormula;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "team", schema = "prm", indexes = {@Index(name = "idx_channel", columnList = "orga_team", unique = true)})
@DiscriminatorFormula("IF(team_id IS NULL, '0', '1')")
public class Team implements Serializable {
  @Serial
  private static final long serialVersionUID = -8929555475128771601L;

  @Id
  @Column(name = "team_id")
  private int prmId;

  @Column(name = "t_id", columnDefinition = "SMALLINT UNSIGNED NOT NULL")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  // TODO Dies hier soll der neue Index werden
  private int id;

  @Column(name = "team_name", nullable = false, length = 100)
  private String name;

  @Column(name = "team_abbr", nullable = false, length = 50)
  private String abbreviation;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "refresh", nullable = false)
  private Calendar refresh;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "orga_team")
  @ToString.Exclude
  private OrgaTeam orgaTeam;

  @Column(name = "highlight", nullable = false)
  private boolean highlight = false;

  @Column(name = "last_team_mmr")
  private Integer lastMMR;
  @OneToMany(mappedBy = "team")
  @ToString.Exclude
  private Set<Participator> participators = new LinkedHashSet<>();

  @OneToMany(mappedBy = "team")
  @ToString.Exclude
  private Set<Player> players = new LinkedHashSet<>();

}
