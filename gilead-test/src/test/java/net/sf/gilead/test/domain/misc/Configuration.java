package net.sf.gilead.test.domain.misc;

import java.io.Serializable;

/**
 * Server configuration
 * 
 * @author bruno.marchesson
 */
public class Configuration implements Serializable {

    private static final long serialVersionUID = 1151825415838912305L;

    /**
     * Configuration name
     */
    private String name;

    /**
     * The Spring context file
     */
    private String springContextFile;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the springContextFile
     */
    public String getSpringContextFile() {
        return springContextFile;
    }

    /**
     * @param springContextFile the springContextFile to set
     */
    public void setSpringContextFile(String springContextFile) {
        this.springContextFile = springContextFile;
    }
}
