package de.zahrie.trues.api.riot.matchhistory.champion;

import java.io.Serial;

import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@Table("champion")
public class Champion implements Entity<Champion> {
  @Serial
  private static final long serialVersionUID = 6171714758229050668L;

  private int id; // champion_id
  private String name; // champion_name

  public static Champion get(Object[] objects) {
    return new Champion(
        (int) objects[0],
        (String) objects[1]
    );
  }

  @Override
  public Champion create() {
    return new Query<Champion>()
        .key("champion_id", id)
        .col("champion_name", name)
        .insert(this);
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }
}