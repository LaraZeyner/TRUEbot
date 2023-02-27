package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class ItemTree extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.staticdata.ItemTree> {
    private static final long serialVersionUID = 2093865180429319652L;

    private final Supplier<List<String>> tags = Suppliers.memoize(new Supplier<List<String>>() {
        @Override
        public List<String> get() {
            if(coreData.getTags() == null) {
                return null;
            }
            return Collections.unmodifiableList(coreData.getTags());
        }
    });

    public ItemTree(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.ItemTree coreData) {
        super(coreData);
    }

    public String getHeader() {
        return coreData.getHeader();
    }

    public List<String> getTags() {
        return tags.get();
    }
}
