package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.Searchable;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableList;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableLists;

public class ChampionSpell extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.staticdata.ChampionSpell> {
    private static final long serialVersionUID = 766914644995245142L;

    private final Supplier<SearchableList<Image>> alternativeImages = Suppliers.memoize(() -> {
        if(coreData.getAlternativeImages() == null) {
            return null;
        }
        final List<Image> alternativeImages = new ArrayList<>(coreData.getAlternativeImages().size());
        for(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.Image image : coreData.getAlternativeImages()) {
            alternativeImages.add(new Image(image));
        }
        return SearchableLists.unmodifiableFrom(alternativeImages);
    });

    private final Supplier<List<Double>> cooldowns = Suppliers.memoize(() -> {
        if(coreData.getCooldowns() == null) {
            return null;
        }
        return Collections.unmodifiableList(coreData.getCooldowns());
    });

    private final Supplier<List<Integer>> costs = Suppliers.memoize(() -> {
        if(coreData.getCosts() == null) {
            return null;
        }
        return Collections.unmodifiableList(coreData.getCosts());
    });

    private final Supplier<List<List<Double>>> effects = Suppliers.memoize(() -> {
        if(coreData.getEffects() == null) {
            return null;
        }
        final List<List<Double>> views = new ArrayList<>(coreData.getEffects().size());
        for(final List<Double> effect : coreData.getEffects()) {
            views.add(effect != null ? Collections.unmodifiableList(effect) : null);
        }
        return Collections.unmodifiableList(views);
    });

    private final Supplier<Image> image = Suppliers.memoize(() -> {
        if(coreData.getImage() == null) {
            return null;
        }
        return new Image(coreData.getImage());
    });

    private final Supplier<List<String>> levelUpEffects = Suppliers.memoize(() -> {
        if(coreData.getLevelUpEffects() == null) {
            return null;
        }
        return Collections.unmodifiableList(coreData.getLevelUpEffects());
    });

    private final Supplier<List<String>> levelUpKeywords = Suppliers.memoize(() -> {
        if(coreData.getLevelUpKeywords() == null) {
            return null;
        }
        return Collections.unmodifiableList(coreData.getLevelUpKeywords());
    });

    private final Supplier<List<Integer>> ranges = Suppliers.memoize(() -> {
        if(coreData.getRanges() == null) {
            return null;
        }
        return Collections.unmodifiableList(coreData.getRanges());
    });

    private final Supplier<SearchableList<SpellVariables>> variables = Suppliers.memoize(() -> {
        if(coreData.getVariables() == null) {
            return null;
        }
        final List<SpellVariables> variables = new ArrayList<>(coreData.getVariables().size());
        for(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.SpellVariables vars : coreData.getVariables()) {
            variables.add(new SpellVariables(vars));
        }
        return SearchableLists.unmodifiableFrom(variables);
    });

    public ChampionSpell(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.ChampionSpell coreData) {
        super(coreData);
    }

    public SearchableList<Image> getAlternativeImages() {
        return alternativeImages.get();
    }

    public List<Double> getCooldowns() {
        return cooldowns.get();
    }

    public List<Integer> getCosts() {
        return costs.get();
    }

    public String getDescription() {
        return coreData.getDescription();
    }

    public List<List<Double>> getEffects() {
        return effects.get();
    }

    public Image getImage() {
        return image.get();
    }

    @Searchable(String.class)
    public String getKey() {
        return coreData.getKey();
    }

    public List<String> getLevelUpEffects() {
        return levelUpEffects.get();
    }

    public List<String> getLevelUpKeywords() {
        return levelUpKeywords.get();
    }

    public int getMaxRank() {
        return coreData.getMaxRank();
    }

    @Searchable(String.class)
    public String getName() {
        return coreData.getName();
    }

    public List<Integer> getRanges() {
        return ranges.get();
    }

    public String getResource() {
        return coreData.getResource();
    }

    public String getResourceDescription() {
        return coreData.getResourceDescription();
    }

    public String getSanitizedDescription() {
        return coreData.getSanitizedDescription();
    }

    public String getSanitizedTooltip() {
        return coreData.getSanitizedTooltip();
    }

    public String getTooltip() {
        return coreData.getTooltip();
    }

    public SearchableList<SpellVariables> getVariables() {
        return variables.get();
    }
}
