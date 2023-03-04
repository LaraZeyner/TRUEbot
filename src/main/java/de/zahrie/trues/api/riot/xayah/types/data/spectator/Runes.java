package de.zahrie.trues.api.riot.xayah.types.data.spectator;

import java.io.Serial;

import de.zahrie.trues.api.riot.xayah.types.data.CoreData;

public class Runes extends CoreData.ListProxy<Integer> {
    @Serial
    private static final long serialVersionUID = -3762685297780616690L;
    private int primaryPath, secondaryPath;

    public Runes() {
        super();
    }

    public Runes(final int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public boolean equals(final Object obj) {
        if(this == obj) {
            return true;
        }
        if(!super.equals(obj)) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final Runes other = (Runes)obj;
        if(primaryPath != other.primaryPath) {
            return false;
        }
      return secondaryPath == other.secondaryPath;
    }

    /**
     * @return the primaryPath
     */
    public int getPrimaryPath() {
        return primaryPath;
    }

    /**
     * @return the secondaryPath
     */
    public int getSecondaryPath() {
        return secondaryPath;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + primaryPath;
        result = prime * result + secondaryPath;
        return result;
    }

    /**
     * @param primaryPath
     *        the primaryPath to set
     */
    public void setPrimaryPath(final int primaryPath) {
        this.primaryPath = primaryPath;
    }

    /**
     * @param secondaryPath
     *        the secondaryPath to set
     */
    public void setSecondaryPath(final int secondaryPath) {
        this.secondaryPath = secondaryPath;
    }
}
