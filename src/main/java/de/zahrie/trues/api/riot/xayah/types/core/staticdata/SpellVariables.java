package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import java.io.Serial;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.Searchable;

public class SpellVariables extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.staticdata.SpellVariables> {
    @Serial
    private static final long serialVersionUID = -8759913162668284032L;

    private final Supplier<List<Double>> coefficients = Suppliers.memoize(() ->
        coreData.getCoefficients() == null ? null : Collections.unmodifiableList(coreData.getCoefficients()))::get;

    public SpellVariables(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.SpellVariables coreData) {
        super(coreData);
    }

    public List<Double> getCoefficients() {
        return coefficients.get();
    }

    public String getDynamic() {
        return coreData.getDynamic();
    }

    @Searchable(String.class)
    public String getKey() {
        return coreData.getKey();
    }

    public String getLink() {
        return coreData.getLink();
    }

    public String getRanksWith() {
        return coreData.getRanksWith();
    }
}
