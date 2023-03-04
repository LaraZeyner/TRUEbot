package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import java.io.Serial;

import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class MasteryTreeTier extends OriannaObject.ListProxy<MasteryTreeItem, de.zahrie.trues.api.riot.xayah.types.data.staticdata.MasteryTreeItem, de.zahrie.trues.api.riot.xayah.types.data.staticdata.MasteryTreeTier> {
    @Serial
    private static final long serialVersionUID = 2705400673641683393L;

    public MasteryTreeTier(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.MasteryTreeTier coreData) {
        super(coreData, MasteryTreeItem::new);
    }
}
