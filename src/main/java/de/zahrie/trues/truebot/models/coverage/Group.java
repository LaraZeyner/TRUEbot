package de.zahrie.trues.truebot.models.coverage;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "coverage_group", indexes = {
        @Index(name = "idx_coverage_group", columnList = "stage, group_name", unique = true) })
public class Group implements Serializable {

  @Serial
  private static final long serialVersionUID = -4755609416246322480L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "coverage_group_id", columnDefinition = "TINYINT UNSIGNED not null")
  private short id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "stage", nullable = false)
  @ToString.Exclude
  private Stage stage;

  @Column(name = "group_name", nullable = false, length = 25)
  private String name;

  @OneToMany(mappedBy = "group")
  @ToString.Exclude
  private Set<Event> coverages = new LinkedHashSet<>();

  @OneToMany(mappedBy = "routeGroup")
  @ToString.Exclude
  private Set<Participator> participators = new LinkedHashSet<>();

}