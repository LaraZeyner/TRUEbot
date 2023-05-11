package de.zahrie.trues.api.coverage.season.signup;

import java.io.Serial;
import java.util.Comparator;
import java.util.List;

import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
@Table("season_signup")
public class SeasonSignup implements Entity<SeasonSignup>, Comparable<SeasonSignup> {
  @Serial
  private static final long serialVersionUID = 4493211805830610407L;

  private int id; // season_signup_id
  private final Season season; // season
  private final PRMTeam team; // team
  private final String info; // signup_info

  public SeasonSignup(Season season, PRMTeam team, String info) {
    this.season = season;
    this.team = team;
    this.info = info;
  }

  public static SeasonSignup get(List<Object> objects) {
    return new SeasonSignup(
        (int) objects.get(0),
        new Query<>(Season.class).entity(objects.get(1)),
        new Query<>(PRMTeam.class).entity(objects.get(2)),
        (String) objects.get(3)
    );
  }

  @Override
  public SeasonSignup create() {
    return new Query<>(SeasonSignup.class).key("season", season).key("team", team)
        .col("signup_info", info).insert(this);
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public int compareTo(@NotNull SeasonSignup o) {
    return Comparator.comparing(SeasonSignup::getSeason).compare(this, o);
  }
}
