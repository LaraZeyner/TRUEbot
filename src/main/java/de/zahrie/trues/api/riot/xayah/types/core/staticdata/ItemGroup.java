package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class ItemGroup extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.staticdata.ItemGroup> {
    private static final long serialVersionUID = 2558683247172514177L;

    public ItemGroup(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.ItemGroup coreData) {
        super(coreData);
    }

    public String getKey() {
        return coreData.getKey();
    }

    public int getMax() {
        return coreData.getMax();
    }
}
