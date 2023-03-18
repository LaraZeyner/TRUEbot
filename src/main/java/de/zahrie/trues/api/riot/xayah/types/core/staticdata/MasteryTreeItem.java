package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import java.io.Serial;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class MasteryTreeItem extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.staticdata.MasteryTreeItem> {
    @Serial
    private static final long serialVersionUID = 5712211737463684969L;

    private final Supplier<Mastery> mastery = Suppliers.memoize(() ->
        coreData.getId() == 0 ? null : Mastery.withId(coreData.getId())
            .withPlatform(Platform.withTag(coreData.getPlatform()))
            .withVersion(coreData.getVersion())
            .withLocale(coreData.getLocale()).get())::get;

    private final Supplier<Mastery> prerequisite = Suppliers.memoize(() ->
        coreData.getPrerequisiteId() == 0 ? null : Mastery.withId(coreData.getPrerequisiteId())
            .withPlatform(Platform.withTag(coreData.getPlatform()))
            .withVersion(coreData.getVersion())
            .withLocale(coreData.getLocale()).get())::get;

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
