package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.base.Suppliers;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.Searchable;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableList;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableLists;

public class ChampionSpell extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.staticdata.ChampionSpell> {
  @Serial
  private static final long serialVersionUID = 766914644995245142L;

  private final Supplier<SearchableList<Image>> alternativeImages = Suppliers.memoize(() ->
      coreData.getAlternativeImages() == null ? null : SearchableLists.unmodifiableFrom(coreData.getAlternativeImages().stream().map(Image::new).collect(Collectors.toCollection(() -> new ArrayList<>(coreData.getAlternativeImages().size())))))::get;

  private final Supplier<List<Double>> cooldowns = Suppliers.memoize(() ->
      coreData.getCooldowns() == null ? null : Collections.unmodifiableList(coreData.getCooldowns()))::get;

  private final Supplier<List<Integer>> costs = Suppliers.memoize(() ->
      coreData.getCosts() == null ? null : Collections.unmodifiableList(coreData.getCosts()))::get;

  private final Supplier<List<List<Double>>> effects = Suppliers.memoize(() ->
      coreData.getEffects() == null ? null : coreData.getEffects().stream().map(effect -> effect != null ? Collections.unmodifiableList(effect) : null).toList())::get;

  private final Supplier<Image> image = Suppliers.memoize(() ->
      coreData.getImage() == null ? null : new Image(coreData.getImage()))::get;

  private final Supplier<List<String>> levelUpEffects = Suppliers.memoize(() ->
      coreData.getLevelUpEffects() == null ? null : Collections.unmodifiableList(coreData.getLevelUpEffects()))::get;

  private final Supplier<List<String>> levelUpKeywords = Suppliers.memoize(() ->
      coreData.getLevelUpKeywords() == null ? null : Collections.unmodifiableList(coreData.getLevelUpKeywords()))::get;

  private final Supplier<List<Integer>> ranges = Suppliers.memoize(() ->
      coreData.getRanges() == null ? null : Collections.unmodifiableList(coreData.getRanges()))::get;

  private final Supplier<SearchableList<SpellVariables>> variables = Suppliers.memoize(() ->
      coreData.getVariables() == null ? null : SearchableLists.unmodifiableFrom(coreData.getVariables().stream().map(SpellVariables::new).collect(Collectors.toCollection(() -> new ArrayList<>(coreData.getVariables().size())))))::get;

  public ChampionSpell(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.ChampionSpell coreData) {
    super(coreData);
  }

  public SearchableList<Image> getAlternativeImages() {
    return alternativeImages.get();
  }

  public List<Double> getCooldowns() {
    return cooldowns.get();
  }

  public List<Integer> getCosts() {
    return costs.get();
  }

  public String getDescription() {
    return coreData.getDescription();
  }

  public List<List<Double>> getEffects() {
    return effects.get();
  }

  public Image getImage() {
    return image.get();
  }

  @Searchable(String.class)
  public String getKey() {
    return coreData.getKey();
  }

  public List<String> getLevelUpEffects() {
    return levelUpEffects.get();
  }

  public List<String> getLevelUpKeywords() {
    return levelUpKeywords.get();
  }

  public int getMaxRank() {
    return coreData.getMaxRank();
  }

  @Searchable(String.class)
  public String getName() {
    return coreData.getName();
  }

  public List<Integer> getRanges() {
    return ranges.get();
  }

  public String getResource() {
    return coreData.getResource();
  }

  public String getResourceDescription() {
    return coreData.getResourceDescription();
  }

  public String getSanitizedDescription() {
    return coreData.getSanitizedDescription();
  }

  public String getSanitizedTooltip() {
    return coreData.getSanitizedTooltip();
  }

  public String getTooltip() {
    return coreData.getTooltip();
  }

  public SearchableList<SpellVariables> getVariables() {
    return variables.get();
  }
}
