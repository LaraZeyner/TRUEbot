package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class MasteryTree extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.staticdata.MasteryTree> {
    @Serial
    private static final long serialVersionUID = -1554786890607091192L;

    private final Supplier<List<MasteryTreeTier>> cunning = Suppliers.memoize(() -> {
        if(coreData.getCunning() == null) {
            return null;
        }
        final List<MasteryTreeTier> cunning = new ArrayList<>(coreData.getCunning().size());
        for(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.MasteryTreeTier tier : coreData.getCunning()) {
            cunning.add(new MasteryTreeTier(tier));
        }
        return Collections.unmodifiableList(cunning);
    });

    private final Supplier<List<MasteryTreeTier>> ferocity = Suppliers.memoize(() -> {
        if(coreData.getFerocity() == null) {
            return null;
        }
        final List<MasteryTreeTier> ferocity = new ArrayList<>(coreData.getFerocity().size());
        for(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.MasteryTreeTier tier : coreData.getFerocity()) {
            ferocity.add(new MasteryTreeTier(tier));
        }
        return Collections.unmodifiableList(ferocity);
    });

    private final Supplier<List<MasteryTreeTier>> resolve = Suppliers.memoize(() -> {
        if(coreData.getResolve() == null) {
            return null;
        }
        final List<MasteryTreeTier> resolve = new ArrayList<>(coreData.getResolve().size());
        for(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.MasteryTreeTier tier : coreData.getResolve()) {
            resolve.add(new MasteryTreeTier(tier));
        }
        return Collections.unmodifiableList(resolve);
    });

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
