package de.zahrie.trues.api.riot.xayah.types.data.staticdata;

import java.io.Serial;

import org.joda.time.Duration;

import de.zahrie.trues.api.riot.xayah.types.data.CoreData;

public class Patches extends CoreData.ListProxy<Patch> {
    @Serial
    private static final long serialVersionUID = 2330525115460288768L;
    private String platform;
    private java.util.Map<String, Duration> shifts;

    public Patches() {
        super();
    }

    public Patches(final int initialCapacity) {
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
        final Patches other = (Patches)obj;
        if(platform == null) {
            if(other.platform != null) {
                return false;
            }
        } else if(!platform.equals(other.platform)) {
            return false;
        }
        if(shifts == null) {
          return other.shifts == null;
        } else return shifts.equals(other.shifts);
    }

    /**
     * @return the platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * @return the shifts
     */
    public java.util.Map<String, Duration> getShifts() {
        return shifts;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (platform == null ? 0 : platform.hashCode());
        result = prime * result + (shifts == null ? 0 : shifts.hashCode());
        return result;
    }

    /**
     * @param platform
     *        the platform to set
     */
    public void setPlatform(final String platform) {
        this.platform = platform;
    }

    /**
     * @param shifts
     *        the shifts to set
     */
    public void setShifts(final java.util.Map<String, Duration> shifts) {
        this.shifts = shifts;
    }
}
