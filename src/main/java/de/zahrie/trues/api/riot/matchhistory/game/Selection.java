package de.zahrie.trues.api.riot.matchhistory.game;

import java.io.Serial;
import java.util.Comparator;

import de.zahrie.trues.api.database.connector.Listing;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.riot.matchhistory.Side;
import de.zahrie.trues.api.riot.matchhistory.champion.Champion;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@Table("selection")
public final class Selection implements Entity<Selection>, Comparable<Selection> {
  @Serial
  private static final long serialVersionUID = 1297903505337960151L;

  private int id; // selection_id
  private final Game game; // game
  private final Side side; // first
  private final SelectionType type; // type
  private final byte selectOrder; // select_order
  private final Champion champion; // champion

  public static Selection get(Object[] objects) {
    return new Selection(
        (int) objects[0],
        new Query<Game>().entity(objects[1]),
        new SQLEnum<Side>().of(objects[2]),
        new SQLEnum<SelectionType>().of(objects[3]),
        (byte) objects[4],
        new Query<Champion>().entity(objects[5])
    );
  }

  @Override
  public Selection create() {
    return new Query<Selection>().key("game", game).key("side", side).key("type", type).key("select_order", selectOrder)
        .col("champion", champion)
        .insert(this);
  }

  @Override
  public int compareTo(@NotNull Selection o) {
    return Comparator.comparing(Selection::getGame)
        .thenComparing(Selection::getSide)
        .thenComparing(Selection::getType)
        .thenComparing(Selection::getSelectOrder).compare(this, o);
  }

  @Listing(Listing.ListingType.LOWER)
  public enum SelectionType {
    BAN, PICK
  }
}
