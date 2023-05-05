package de.zahrie.trues.api.coverage.lineup.model;

import java.io.Serial;
import java.util.Comparator;
import java.util.Objects;

import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.player.model.PlayerBase;
import de.zahrie.trues.api.database.connector.Database;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.riot.matchhistory.performance.Lane;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * Lineup wird submitted ({@code ordered} = Command | {@code not ordered} = matchlog)
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@Table("coverage_lineup")
public class Lineup implements Entity<Lineup>, Comparable<Lineup> {
  @Serial
  private static final long serialVersionUID = 3196905801592447600L;

  private int id;
  private final Participator participator; // coverage_team
  private final PlayerBase player; // player
  private Lane lane; // lineup_id

  public void setLane(Lane lane) {
    this.lane = lane;
    new Query<>().col("lineup_id", lane).update(id);
  }

  public Lineup(Participator participator, PlayerBase player, Lane lane) {
    this.participator = participator;
    this.player = player;
    this.lane = lane;
  }

  public static Lineup get(Object[] objects) {
    return new Lineup(
        (int) objects[0],
        new Query<Participator>().entity(objects[1]),
        new Query<Player>().entity(objects[2]),
        new SQLEnum<Lane>().of(objects[3])
    );
  }

  @Override
  public Lineup create() {
    return new Query<Lineup>().key("coverage_team", participator).key("player", player)
        .col("lineup_id", lane).insert(this, l -> participator.getLineups().add(l));
  }

  @Override
  public void delete() {
    participator.getLineups().remove(this);
    new Query<Lineup>().delete(getId());
  }

  @Override
  public void forceDelete() {
    delete();
    Database.connection().commit();
  }

  @Override
  public int compareTo(@NotNull Lineup o) {
    return Comparator.comparing(Lineup::getParticipator).thenComparing(Lineup::getLane).compare(this, o);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof final Lineup lineup)) return false;
    if (getId() != 0) return getId() == lineup.getId();
    return Objects.equals(getParticipator(), lineup.getParticipator()) && getLane() == lineup.getLane()
        && Objects.equals(getPlayer(), lineup.getPlayer());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getParticipator(), getLane(), getPlayer());
  }
}
