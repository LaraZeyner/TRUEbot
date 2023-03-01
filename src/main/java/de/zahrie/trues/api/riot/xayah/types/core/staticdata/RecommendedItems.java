package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import de.zahrie.trues.api.riot.xayah.types.common.GameMode;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.Searchable;

public class RecommendedItems extends OriannaObject.ListProxy<ItemSet, de.zahrie.trues.api.riot.xayah.types.data.staticdata.ItemSet, de.zahrie.trues.api.riot.xayah.types.data.staticdata.RecommendedItems> {
    private static final Map<String, de.zahrie.trues.api.riot.xayah.types.common.Map> MAP_CONVERSIONS = ImmutableMap.of("SR",
        de.zahrie.trues.api.riot.xayah.types.common.Map.SUMMONERS_RIFT, "TT", de.zahrie.trues.api.riot.xayah.types.common.Map.TWISTED_TREELINE, "HA",
        de.zahrie.trues.api.riot.xayah.types.common.Map.HOWLING_ABYSS, "CS", de.zahrie.trues.api.riot.xayah.types.common.Map.THE_CRYSTAL_SCAR);

    private static final long serialVersionUID = 7485737031964917542L;

    public RecommendedItems(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.RecommendedItems coreData) {
        super(coreData, set -> new ItemSet(set));
    }

    @Searchable(de.zahrie.trues.api.riot.xayah.types.common.Map.class)
    public de.zahrie.trues.api.riot.xayah.types.common.Map getMap() {
        return MAP_CONVERSIONS.get(coreData.getMap());
    }

    @Searchable(GameMode.class)
    public GameMode getMode() {
        return GameMode.valueOf(coreData.getMode());
    }

    @Searchable(String.class)
    public String getTitle() {
        return coreData.getTitle();
    }

    public String getType() {
        return coreData.getType();
    }

    public boolean isPriority() {
        return coreData.isPriority();
    }
}
