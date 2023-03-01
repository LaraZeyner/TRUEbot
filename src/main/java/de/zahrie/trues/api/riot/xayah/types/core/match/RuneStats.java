package de.zahrie.trues.api.riot.xayah.types.core.match;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.Searchable;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.ReforgedRune;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.Versions;

public class RuneStats extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.match.RuneStats> {
    private static final long serialVersionUID = 3663530937677122757L;

    private final Supplier<ReforgedRune> rune = Suppliers.memoize(() -> {
        if(coreData.getId() == 0) {
            return null;
        }
        final String version = Versions.withPlatform(Platform.withTag(coreData.getPlatform())).get().getBestMatch(coreData.getVersion());
        return ReforgedRune.withId(coreData.getId()).withPlatform(Platform.withTag(coreData.getPlatform())).withVersion(version).get();
    });

    private final Supplier<List<Integer>> variables = Suppliers.memoize(() -> {
        if(coreData.getVariables() == null) {
            return null;
        }
        return Collections.unmodifiableList(coreData.getVariables());
    });

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
