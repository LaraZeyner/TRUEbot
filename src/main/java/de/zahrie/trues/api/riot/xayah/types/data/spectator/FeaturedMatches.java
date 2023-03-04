package de.zahrie.trues.api.riot.xayah.types.data.spectator;

import java.io.Serial;

import org.joda.time.Duration;

import de.zahrie.trues.api.riot.xayah.types.data.CoreData;

public class FeaturedMatches extends CoreData.ListProxy<FeaturedMatch> {
    @Serial
    private static final long serialVersionUID = 2972903457763427690L;
    private String platform;
    private Duration refreshInterval;

    public FeaturedMatches() {
        super();
    }

    public FeaturedMatches(final int initialCapacity) {
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
        final FeaturedMatches other = (FeaturedMatches)obj;
        if(platform == null) {
            if(other.platform != null) {
                return false;
            }
        } else if(!platform.equals(other.platform)) {
            return false;
        }
        if(refreshInterval == null) {
          return other.refreshInterval == null;
        } else return refreshInterval.equals(other.refreshInterval);
    }

    /**
     * @return the platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * @return the refreshInterval
     */
    public Duration getRefreshInterval() {
        return refreshInterval;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (platform == null ? 0 : platform.hashCode());
        result = prime * result + (refreshInterval == null ? 0 : refreshInterval.hashCode());
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
     * @param refreshInterval
     *        the refreshInterval to set
     */
    public void setRefreshInterval(final Duration refreshInterval) {
        this.refreshInterval = refreshInterval;
    }
}
