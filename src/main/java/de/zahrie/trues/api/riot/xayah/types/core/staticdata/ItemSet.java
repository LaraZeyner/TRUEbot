package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import java.util.HashMap;
import java.util.List;

import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class ItemSet extends OriannaObject.MapProxy<Item, Integer, Integer, Integer, de.zahrie.trues.api.riot.xayah.types.data.staticdata.ItemSet> {
    private static final long serialVersionUID = -8917142329487068965L;

    private static java.util.Map<Item, Integer> convert(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.ItemSet coreData) {
        final List<Item> keys = Items.withIds(coreData.keySet()).withPlatform(Platform.withTag(coreData.getPlatform())).withVersion(coreData.getVersion())
            .withLocale(coreData.getLocale()).get();
        final java.util.Map<Item, Integer> data = new HashMap<>();
        for(final Item key : keys) {
            data.put(key, coreData.get(key.getId()));
        }
        return data;
    }

    public ItemSet(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.ItemSet coreData) {
        super(coreData, convert(coreData));
    }

    public String getType() {
        return coreData.getType();
    }

    public boolean isRecMath() {
        return coreData.isRecMath();
    }
}
