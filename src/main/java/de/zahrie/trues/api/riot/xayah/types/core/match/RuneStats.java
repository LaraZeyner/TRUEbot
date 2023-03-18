package de.zahrie.trues.api.riot.xayah.types.core.match;

import java.io.Serial;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.Searchable;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.ReforgedRune;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.Versions;

public class RuneStats extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.match.RuneStats> {
  @Serial
  private static final long serialVersionUID = 3663530937677122757L;

  private final Supplier<ReforgedRune> rune = Suppliers.memoize(() ->
      coreData.getId() == 0 ? null : ReforgedRune.withId(coreData.getId()).withPlatform(Platform.withTag(coreData.getPlatform())).withVersion(Versions.withPlatform(Platform.withTag(coreData.getPlatform())).get().getBestMatch(coreData.getVersion())).get())::get;

  private final Supplier<List<Integer>> variables = Suppliers.memoize(() ->
      coreData.getVariables() == null ? null : Collections.unmodifiableList(coreData.getVariables()))::get;

  public RuneStats(final de.zahrie.trues.api.riot.xayah.types.data.match.RuneStats coreData) {
    super(coreData);
  }

  @Searchable({ReforgedRune.class, String.class, int.class})
  public ReforgedRune getRune() {
    return rune.get();
  }

  public List<Integer> getVariables() {
    return variables.get();
  }
}
