package de.zahrie.trues.api.coverage.season;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@DiscriminatorValue("intern")
public class OrgaCupSeason extends Season implements Serializable {
  public static String getRules() {
    return """
        Du darfst nur in Teams spielen, die sich nicht begegnen.
        Stand-ins dürfen höchstens 100 LP über dem ersetzten Spieler sein.
        
        Gruppenspiele: Single-Round-Robin 'Two-Games'
        Elimination: 'Best of Three' (Finale Bo5)
        Einigt euch vor dem offiziellen Termin auf einen Termin.
        Nicht ausgespielte Spiele werden 0:0 gewertet.

        Lineup-Deadline: 24 Stunden vorher
        Lobbyname: 'TRUE M<id> TEAM1 vs TEAM2 G<1-5>' Passwort: 'truecup'
        """;
  }

  @Serial
  private static final long serialVersionUID = 3498814029985658723L;
}
