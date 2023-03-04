package de.zahrie.trues.api.riot.xayah.types.dto.staticdata;

import java.io.Serial;

import de.zahrie.trues.api.riot.xayah.types.dto.DataObject;

public class Languages extends DataObject.ListProxy<String> {
    @Serial
    private static final long serialVersionUID = -7967427470896414374L;
    private String platform;

    public Languages() {
        super();
    }

    public Languages(final int initialCapacity) {
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
        final Languages other = (Languages)obj;
        if(platform == null) {
          return other.platform == null;
        } else return platform.equals(other.platform);
    }

    /**
     * @return the platform
     */
    public String getPlatform() {
        return platform;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (platform == null ? 0 : platform.hashCode());
        return result;
    }

    /**
     * @param platform
     *        the platform to set
     */
    public void setPlatform(final String platform) {
        this.platform = platform;
    }
}
