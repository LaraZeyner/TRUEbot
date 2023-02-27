package de.zahrie.trues.api.riot.xayah.types.data.match;

import java.util.List;

import de.zahrie.trues.api.riot.xayah.types.data.CoreData;

public class RuneStats extends CoreData {
    private static final long serialVersionUID = -461218494339044648L;
    private int id;
    private String platform, version;
    private List<Integer> variables;

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
        final RuneStats other = (RuneStats)obj;
        if(id != other.id) {
            return false;
        }
        if(platform == null) {
            if(other.platform != null) {
                return false;
            }
        } else if(!platform.equals(other.platform)) {
            return false;
        }
        if(variables == null) {
            if(other.variables != null) {
                return false;
            }
        } else if(!variables.equals(other.variables)) {
            return false;
        }
        if(version == null) {
            if(other.version != null) {
                return false;
            }
        } else if(!version.equals(other.version)) {
            return false;
        }
        return true;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * @return the variables
     */
    public List<Integer> getVariables() {
        return variables;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + (platform == null ? 0 : platform.hashCode());
        result = prime * result + (variables == null ? 0 : variables.hashCode());
        result = prime * result + (version == null ? 0 : version.hashCode());
        return result;
    }

    /**
     * @param id
     *        the id to set
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * @param platform
     *        the platform to set
     */
    public void setPlatform(final String platform) {
        this.platform = platform;
    }

    /**
     * @param variables
     *        the variables to set
     */
    public void setVariables(final List<Integer> variables) {
        this.variables = variables;
    }

    /**
     * @param version
     *        the version to set
     */
    public void setVersion(final String version) {
        this.version = version;
    }
}
