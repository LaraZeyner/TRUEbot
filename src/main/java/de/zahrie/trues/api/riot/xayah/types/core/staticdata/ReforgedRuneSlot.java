package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import com.google.common.base.Function;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class ReforgedRuneSlot extends OriannaObject.ListProxy<ReforgedRune, Integer, de.zahrie.trues.api.riot.xayah.types.data.staticdata.ReforgedRuneSlot> {
    private static final long serialVersionUID = 5586589043798659449L;

    public ReforgedRuneSlot(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.ReforgedRuneSlot coreData,
        final java.util.Map<Integer, ReforgedRune> runes) {
        super(coreData, id -> runes.get(id));
    }
}
