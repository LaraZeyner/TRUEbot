package de.zahrie.trues.api.coverage.league.model;

import java.io.Serial;

import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(value = "coverage_group", department = "other")
public class League extends LeagueBase implements Entity<League> {
  @Serial
  private static final long serialVersionUID = -1878025702559463286L;

  public League(Stage stage, String name) {
    super(stage, name);
  }

  private League(int id, Stage stage, String name) {
    super(id, stage, name);
  }

  public static League get(Object[] objects) {
    return new League(
        (int) objects[0],
        new Query<Stage>().entity(objects[2]),
        (String) objects[3]
    );
  }

  @Override
  public League create() {
    return new Query<League>()
        .key("stage", stage).key("group_name", name)
        .insert(this);
  }
}
