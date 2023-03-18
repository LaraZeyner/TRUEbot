package de.zahrie.trues.api.coverage.participator;

import de.zahrie.trues.api.coverage.league.model.League;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.ToString;

@Embeddable
@Getter
public class ParticipatorRoute {
  // TODO (Abgie) 01.03.2023: never used
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "route_group")
  @ToString.Exclude
  private League league;

  @Enumerated(EnumType.STRING)
  @Column(name = "route_type", length = 6)
  private RouteType type;

  @Column(name = "route_value", columnDefinition = "TINYINT UNSIGNED")
  private Short value;

}
