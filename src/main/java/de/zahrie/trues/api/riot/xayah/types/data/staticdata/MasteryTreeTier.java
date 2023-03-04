package de.zahrie.trues.api.riot.xayah.types.data.staticdata;

import java.io.Serial;

import de.zahrie.trues.api.riot.xayah.types.data.CoreData;

public class MasteryTreeTier extends CoreData.ListProxy<MasteryTreeItem> {
    @Serial
    private static final long serialVersionUID = 589645418400098055L;

    public MasteryTreeTier() {
        super();
    }

    public MasteryTreeTier(final int initialCapacity) {
        super(initialCapacity);
    }
}
