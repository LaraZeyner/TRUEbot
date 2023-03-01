package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class MasteryTreeItem extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.staticdata.MasteryTreeItem> {
    private static final long serialVersionUID = 5712211737463684969L;

    private final Supplier<Mastery> mastery = Suppliers.memoize(() -> {
        if(coreData.getId() == 0) {
            return null;
        }
        return Mastery.withId(coreData.getId()).withPlatform(Platform.withTag(coreData.getPlatform())).withVersion(coreData.getVersion())
            .withLocale(coreData.getLocale()).get();
    });

    private final Supplier<Mastery> prerequisite = Suppliers.memoize(() -> {
        if(coreData.getPrerequisiteId() == 0) {
            return null;
        }
        return Mastery.withId(coreData.getPrerequisiteId()).withPlatform(Platform.withTag(coreData.getPlatform())).withVersion(coreData.getVersion())
            .withLocale(coreData.getLocale()).get();
    });

    public MasteryTreeItem(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.MasteryTreeItem coreData) {
        super(coreData);
    }

    public Mastery getMastery() {
        return mastery.get();
    }

    public Mastery getPrerequisite() {
        return prerequisite.get();
    }
}
