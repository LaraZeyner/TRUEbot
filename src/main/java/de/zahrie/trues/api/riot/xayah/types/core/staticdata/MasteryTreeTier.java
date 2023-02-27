package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import com.google.common.base.Function;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class MasteryTreeTier extends OriannaObject.ListProxy<MasteryTreeItem, de.zahrie.trues.api.riot.xayah.types.data.staticdata.MasteryTreeItem, de.zahrie.trues.api.riot.xayah.types.data.staticdata.MasteryTreeTier> {
    private static final long serialVersionUID = 2705400673641683393L;

    public MasteryTreeTier(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.MasteryTreeTier coreData) {
        super(coreData, new Function<de.zahrie.trues.api.riot.xayah.types.data.staticdata.MasteryTreeItem, MasteryTreeItem>() {
            @Override
            public MasteryTreeItem apply(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.MasteryTreeItem item) {
                return new MasteryTreeItem(item);
            }
        });
    }
}
