package de.zahrie.trues.api.riot.matchhistory.champion;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
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
@Table(name = "champion")
@NamedQuery(name = "Champion.fromName", query = "FROM Champion WHERE name = :name")
public class Champion implements Serializable {

  @Serial
  private static final long serialVersionUID = -6533381080994832890L;


  @Id
  @Column(name = "champion_id", nullable = false, columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @Column(name = "champion_name", nullable = false, length = 16)
  private String name;
}