package de.zahrie.trues.api.riot.game;

import java.io.Serial;
import java.util.Comparator;
import java.util.List;

import de.zahrie.trues.api.database.connector.Listing;
import de.zahrie.trues.api.database.connector.SQLUtils;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.riot.Side;
import de.zahrie.trues.api.riot.champion.Champion;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@Table("selection")
@ExtensionMethod(SQLUtils.class)
public final class Selection implements Entity<Selection>, Comparable<Selection> {
  @Serial
  private static final long serialVersionUID = 1297903505337960151L;

  private int id; // selection_id
  private final Game game; // game
  private final Side side; // first
  private final SelectionType type; // type
  private final byte selectOrder; // select_order
  private final Champion champion; // champion

  public static Selection get(List<Object> objects) {
    return new Selection(
        (int) objects.get(0),
        new Query<>(Game.class).entity(objects.get(1)),
        new SQLEnum<>(Side.class).of(objects.get(2)),
        new SQLEnum<>(SelectionType.class).of(objects.get(3)),
        objects.get(4).byteValue(),
        new Query<>(Champion.class).entity(objects.get(5))
    );
  }

  @Override
  public Selection create() {
    return new Query<>(Selection.class).key("game", game).key("side", side).key("type", type).key("select_order", selectOrder)
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
