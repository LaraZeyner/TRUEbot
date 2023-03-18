package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import java.io.Serial;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class MasteryTree extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.staticdata.MasteryTree> {
  @Serial
  private static final long serialVersionUID = -1554786890607091192L;

  private final Supplier<List<MasteryTreeTier>> cunning = Suppliers.memoize(() ->
      coreData.getCunning() == null ? null : coreData.getCunning().stream().map(MasteryTreeTier::new).toList())::get;

  private final Supplier<List<MasteryTreeTier>> ferocity = Suppliers.memoize(() ->
      coreData.getFerocity() == null ? null : coreData.getFerocity().stream().map(MasteryTreeTier::new).toList())::get;

  private final Supplier<List<MasteryTreeTier>> resolve = Suppliers.memoize(() ->
      coreData.getResolve() == null ? null : coreData.getResolve().stream().map(MasteryTreeTier::new).toList())::get;

  public MasteryTree(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.MasteryTree coreData) {
    super(coreData);
  }

  public List<MasteryTreeTier> getCunning() {
    return cunning.get();
  }

  public List<MasteryTreeTier> getFerocity() {
    return ferocity.get();
  }

  public List<MasteryTreeTier> getResolve() {
    return resolve.get();
  }
}
