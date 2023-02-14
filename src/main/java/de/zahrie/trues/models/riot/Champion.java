package de.zahrie.trues.models.riot;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import de.zahrie.trues.models.riot.matchhistory.Performance;
import de.zahrie.trues.models.riot.matchhistory.Selection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

}