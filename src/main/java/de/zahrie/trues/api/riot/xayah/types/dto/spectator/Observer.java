package de.zahrie.trues.api.riot.xayah.types.dto.spectator;

import java.io.Serial;

import de.zahrie.trues.api.riot.xayah.types.dto.DataObject;

public class Observer extends DataObject {
    @Serial
    private static final long serialVersionUID = -9110057560473353917L;
    private String encryptionKey;

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
        final Observer other = (Observer)obj;
        if(encryptionKey == null) {
          return other.encryptionKey == null;
        } else return encryptionKey.equals(other.encryptionKey);
    }

    /**
     * @return the encryptionKey
     */
    public String getEncryptionKey() {
        return encryptionKey;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (encryptionKey == null ? 0 : encryptionKey.hashCode());
        return result;
    }

    /**
     * @param encryptionKey
     *        the encryptionKey to set
     */
    public void setEncryptionKey(final String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }
}
