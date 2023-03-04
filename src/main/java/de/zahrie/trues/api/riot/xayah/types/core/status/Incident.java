package de.zahrie.trues.api.riot.xayah.types.core.status;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class Incident extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.status.Incident> {
    @Serial
    private static final long serialVersionUID = 7833049943482732990L;

    private final Supplier<List<Message>> updates = Suppliers.memoize(() -> {
        if(coreData.getUpdates() == null) {
            return null;
        }
        final List<Message> updates = new ArrayList<>(coreData.getUpdates().size());
        for(final de.zahrie.trues.api.riot.xayah.types.data.status.Message update : coreData.getUpdates()) {
            updates.add(new Message(update));
        }
        return Collections.unmodifiableList(updates);
    });

    public Incident(final de.zahrie.trues.api.riot.xayah.types.data.status.Incident coreData) {
        super(coreData);
    }

    public DateTime getCreated() {
        return coreData.getCreated();
    }

    public long getId() {
        return coreData.getId();
    }

    public List<Message> getUpdates() {
        return updates.get();
    }

    public boolean isActive() {
        return coreData.isActive();
    }
}
