package de.zahrie.trues.api.riot.xayah.types.core.match;

import java.io.Serial;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class ParticipantTimeline extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.match.ParticipantTimeline> {
  @Serial
  private static final long serialVersionUID = -9013789487241071327L;

  private final Supplier<StatTotals> creepScore = Suppliers.memoize(() ->
      coreData.getCreepScore() == null ? null : new StatTotals(coreData.getCreepScore()))::get;

  private final Supplier<StatTotals> creepScoreDifference = Suppliers.memoize(() ->
      coreData.getCreepScoreDifference() == null ? null : new StatTotals(coreData.getCreepScoreDifference()))::get;

  private final Supplier<StatTotals> damageTaken = Suppliers.memoize(() ->
      coreData.getDamageTaken() == null ? null : new StatTotals(coreData.getDamageTaken()))::get;

  private final Supplier<StatTotals> damageTakenDifference = Suppliers.memoize(() ->
      coreData.getDamageTakenDifference() == null ? null : new StatTotals(coreData.getDamageTakenDifference()))::get;

  private final Supplier<StatTotals> experience = Suppliers.memoize(() ->
      coreData.getExperience() == null ? null : new StatTotals(coreData.getExperience()))::get;

  private final Supplier<StatTotals> experienceDifference = Suppliers.memoize(() ->
      coreData.getExperienceDifference() == null ? null : new StatTotals(coreData.getExperienceDifference()))::get;

  private final Supplier<StatTotals> gold = Suppliers.memoize(() ->
      coreData.getGold() == null ? null : new StatTotals(coreData.getGold()))::get;

  public ParticipantTimeline(final de.zahrie.trues.api.riot.xayah.types.data.match.ParticipantTimeline coreData) {
    super(coreData);
  }

  public StatTotals getCreepScore() {
    return creepScore.get();
  }

  public StatTotals getCreepScoreDifference() {
    return creepScoreDifference.get();
  }

  public StatTotals getDamageTaken() {
    return damageTaken.get();
  }

  public StatTotals getDamageTakenDifference() {
    return damageTakenDifference.get();
  }

  public StatTotals getExperience() {
    return experience.get();
  }

  public StatTotals getExperienceDifference() {
    return experienceDifference.get();
  }

  public StatTotals getGold() {
    return gold.get();
  }
}
