package de.zahrie.trues.api.riot.xayah.types.core.status;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class Service extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.status.Service> {
    @Serial
    private static final long serialVersionUID = 7027647640617848231L;

    private final Supplier<List<Incident>> incidents = Suppliers.memoize(() -> {
        if(coreData.getIncidents() == null) {
            return null;
        }
        final List<Incident> incidents = new ArrayList<>(coreData.getIncidents().size());
        for(final de.zahrie.trues.api.riot.xayah.types.data.status.Incident incident : coreData.getIncidents()) {
            incidents.add(new Incident(incident));
        }
        return Collections.unmodifiableList(incidents);
    });

    public Service(final de.zahrie.trues.api.riot.xayah.types.data.status.Service coreData) {
        super(coreData);
    }

    public List<Incident> getIncidents() {
        return incidents.get();
    }

    public String getName() {
        return coreData.getName();
    }

    public String getStatus() {
        return coreData.getStatus();
    }
}
