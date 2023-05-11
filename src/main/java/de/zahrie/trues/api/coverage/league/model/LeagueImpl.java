package de.zahrie.trues.api.coverage.league.model;

import java.io.Serial;
import java.util.List;

import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(value = "coverage_group", department = "other")
public class LeagueImpl extends League implements Entity<LeagueImpl> {
  @Serial
  private static final long serialVersionUID = -1878025702559463286L;

  public LeagueImpl(Stage stage, String name) {
    super(stage, name);
  }

  private LeagueImpl(int id, Stage stage, String name) {
    super(id, stage, name);
  }

  public static LeagueImpl get(List<Object> objects) {
    return new LeagueImpl(
        (int) objects.get(0),
        new Query<>(Stage.class).entity(objects.get(2)),
        (String) objects.get(3)
    );
  }

  @Override
  public LeagueImpl create() {
    return new Query<>(LeagueImpl.class)
        .key("stage", stage).key("group_name", name)
        .insert(this);
  }
}
