package de.zahrie.trues.api.riot.matchhistory.champion;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import de.zahrie.trues.api.riot.matchhistory.performance.Performance;
import de.zahrie.trues.api.riot.matchhistory.selection.Selection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
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
@Table(name = "champion", schema = "prm")
@NamedQuery(name = "Champion.fromName", query = "FROM Champion WHERE name = :name")
public class Champion implements Serializable {

  @Serial
  private static final long serialVersionUID = -6533381080994832890L;


  @Id
  @Column(name = "champion_id", nullable = false, columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @Column(name = "champion_name", nullable = false, length = 16)
  private String name;

  @OneToMany(mappedBy = "champion")
  @ToString.Exclude
  private Set<Performance> performances_own = new LinkedHashSet<>();

  @OneToMany(mappedBy = "opponent")
  @ToString.Exclude
  private Set<Performance> performances_enemy = new LinkedHashSet<>();

  @OneToMany(mappedBy = "champion")
  @ToString.Exclude
  private Set<Selection> selections = new LinkedHashSet<>();

  public Champion(int id, String name) {
    this.id = id;
    this.name = name;
  }
}