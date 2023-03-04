package de.zahrie.trues.api.riot.xayah.types.dto.staticdata;

import java.io.Serial;
import java.util.List;

import de.zahrie.trues.api.riot.xayah.types.dto.DataObject;

public class ReforgedRuneSlot extends DataObject {
    @Serial
    private static final long serialVersionUID = -2552096507422652655L;
    private List<ReforgedRune> runes;

    @Override
    public boolean equals(final Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final ReforgedRuneSlot other = (ReforgedRuneSlot)obj;
        if(runes == null) {
          return other.runes == null;
        } else return runes.equals(other.runes);
    }

    /**
     * @return the runes
     */
    public List<ReforgedRune> getRunes() {
        return runes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (runes == null ? 0 : runes.hashCode());
        return result;
    }

    /**
     * @param runes
     *        the runes to set
     */
    public void setRunes(final List<ReforgedRune> runes) {
        this.runes = runes;
    }
}
