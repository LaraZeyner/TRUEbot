package de.zahrie.trues.api.coverage.team.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.models.community.OrgaTeam;
import de.zahrie.trues.util.database.Database;
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
  private Integer prmId;

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

  public Team(String name, String abbreviation) {
    this.name = name;
    this.abbreviation = abbreviation;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Team)) return false;
    if (this.prmId != null) return this.prmId.equals(((Team) obj).getPrmId());
    return this.id == ((Team) obj).getId();
  }

  public void refresh(Calendar start) {
    final Calendar calendar = Calendar.getInstance();
    calendar.setTime(start.getTime());
    calendar.add(Calendar.DATE, 70);
    this.setRefresh(calendar);
    Database.save(this);
  }

  public void setRefresh(Calendar refresh) {
    if (refresh.after(this.refresh)) {
      this.refresh = refresh;
    }
  }

  public boolean isNotOrgaTeam() {
    return this.orgaTeam == null;
  }

  public void setHighlight(boolean highlight) {
    this.highlight = highlight;
    Database.save(this);
  }
}
