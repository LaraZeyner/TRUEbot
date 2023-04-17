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
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "route_group")
  @ToString.Exclude
  private League league;

  @Enumerated(EnumType.STRING)
  @Column(name = "route_type", length = 6)
  private RouteType type;

  @Column(name = "route_value", columnDefinition = "TINYINT UNSIGNED")
  private Short value;

  @Override
  public String toString() {
    if (type == null) return "n.A.";
    return switch (type) {
      case LOSER -> "Loser M" + value;
      case PLACE -> value + ". " + league.getName();
      case SEEDED -> "Seed " + value;
      case WINNER -> "WInner M" + value;
    };
  }

  public enum RouteType {
    LOSER,
    PLACE,
    SEEDED,
    WINNER
  }
}
