package net.sf.gilead.proxy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.gilead.proxy.xml.AdditionalCode;
import net.sf.gilead.proxy.xml.AdditionalCodeReader;

public class AdditionalCodeManager {

    /**
     * Unique instance of the singleton
     */
    private static AdditionalCodeManager INSTANCE = null;

    /**
     * @return the instance of singleton
     */
    public static AdditionalCodeManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AdditionalCodeManager();
        }
        return INSTANCE;
    }

    /**
     * Map of additional code
     */
    private Map<String, AdditionalCode> _additionalCodeMap;

    /**
     * Constructor
     */
    protected AdditionalCodeManager() {
        _additionalCodeMap = new HashMap<>();

        // additional code
        try {
            // Java 5
            AdditionalCode additionalCode = AdditionalCodeReader.readFromFile(ProxyManager.JAVA_5_LAZY_POJO);
            _additionalCodeMap.put(ProxyManager.JAVA_5_LAZY_POJO, additionalCode);
        } catch (IOException ex) {
            // Should not happen
            throw new RuntimeException("Error reading proxy file", ex);
        }
    }

    /**
     * @return the additional code associated with the argument className, or null if any
     */
    public AdditionalCode getAdditionalCodeFor(String className) {
        // Search for suffix
        for (AdditionalCode additionalCode : _additionalCodeMap.values()) {
            if (className.endsWith(additionalCode.getSuffix())) {
                return additionalCode;
            }
        }

        // Suffix not found, so no additional code is associated to this class
        return null;
    }

    /**
     * Compute the source class name from the proxy class name and the additional code
     * 
     * @param proxyName
     * @param additionalCode
     * @return
     */
    public String getSourceClassName(String proxyName, AdditionalCode additionalCode) {
        return proxyName.substring(0, proxyName.length() - additionalCode.getSuffix().length());
    }

    /**
     * Get additional code from the file name
     * 
     * @param suffix
     * @return
     */
    public AdditionalCode getAdditionalCode(String fileName) {
        return _additionalCodeMap.get(fileName);
    }
}
