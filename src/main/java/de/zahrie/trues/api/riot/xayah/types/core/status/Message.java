package de.zahrie.trues.api.riot.xayah.types.core.status;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class Message extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.status.Message> {
    private static final long serialVersionUID = 4358755936137676315L;

    private final Supplier<Map<String, Translation>> translations = Suppliers.memoize(new Supplier<Map<String, Translation>>() {
        @Override
        public Map<String, Translation> get() {
            if(coreData.getTranslations() == null) {
                return null;
            }
            final Map<String, Translation> translations = new HashMap<>(coreData.getTranslations().size());
            for(final String locale : coreData.getTranslations().keySet()) {
                translations.put(locale, new Translation(coreData.getTranslations().get(locale)));
            }
            return Collections.unmodifiableMap(translations);
        }
    });

    public Message(final de.zahrie.trues.api.riot.xayah.types.data.status.Message coreData) {
        super(coreData);
    }

    public String getAuthor() {
        return coreData.getAuthor();
    }

    public String getContent() {
        return coreData.getContent();
    }

    public DateTime getCreated() {
        return coreData.getCreated();
    }

    public String getHeading() {
        return coreData.getHeading();
    }

    public String getId() {
        return coreData.getId();
    }

    public String getSeverity() {
        return coreData.getSeverity();
    }

    public Map<String, Translation> getTranslations() {
        return translations.get();
    }

    public DateTime getUpdated() {
        return coreData.getUpdated();
    }
}
