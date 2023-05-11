package de.zahrie.trues.api.coverage.league.model;

import java.util.Comparator;
import java.util.List;

import de.zahrie.trues.api.coverage.match.model.LeagueMatch;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.coverage.team.leagueteam.LeagueTeam;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Id;
import de.zahrie.trues.api.database.query.Query;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table("coverage_group")
public abstract class League implements ALeague, Id {
  private int id; // coverage_id
  protected final Stage stage; // stage
  protected final String name; // group_name

  public List<LeagueMatch> getMatches() {
    return new Query<>(LeagueMatch.class).where("coverage_group", this).entityList();
  }

  public List<LeagueTeam> getLeagueTeams() {
    return new Query<>(LeagueTeam.class).where("league", this).entityList();
  }

  public List<LeagueTeam> getSignups() {
    return new Query<>(LeagueTeam.class).where("league", this).entityList().stream().toList();
  }

  @Override
  public int compareTo(@NotNull ALeague o) {
    return Comparator.comparing(ALeague::getStage).compare(this, o);
  }
}
