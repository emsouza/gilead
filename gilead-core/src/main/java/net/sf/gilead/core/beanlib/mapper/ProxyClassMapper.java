package net.sf.gilead.core.beanlib.mapper;

import net.sf.gilead.core.PersistenceUtil;
import net.sf.gilead.core.beanlib.ClassMapper;
import net.sf.gilead.proxy.AdditionalCodeManager;
import net.sf.gilead.proxy.ProxyManager;
import net.sf.gilead.proxy.xml.AdditionalCode;

/**
 * Class mapper for domain and proxy
 *
 * @author bruno.marchesson
 */
public class ProxyClassMapper implements ClassMapper {

    /**
     * The associated persistence util
     */
    protected PersistenceUtil persistenceUtil;

    /**
     * @return the associated Persistence Util
     */
    public PersistenceUtil getPersistenceUtil() {
        return persistenceUtil;
    }

    /**
     * @param persistenceUtil the persistenceUtil to set
     */
    public void setPersistenceUtil(PersistenceUtil persistenceUtil) {
        this.persistenceUtil = persistenceUtil;
    }

    @Override
    public Class<?> getSourceClass(Class<?> targetClass) {
        return ProxyManager.getInstance().getSourceClass(targetClass);
    }

    @Override
    public Class<?> getTargetClass(Class<?> sourceClass) {
        Class<?> proxyClass = ProxyManager.getInstance().getProxyClass(sourceClass);

        if (proxyClass == null) {
            // Force proxy generation for persistent class
            if (persistenceUtil == null) {
                throw new RuntimeException("Missing PersistenceUtil in ProxyClassMapper : please fill this member...");
            }
            if (persistenceUtil.isPersistentClass(sourceClass)) {
                AdditionalCode additionalCode = AdditionalCodeManager.getInstance().getAdditionalCode(ProxyManager.JAVA_5_LAZY_POJO);
                proxyClass = ProxyManager.getInstance().generateProxyClass(sourceClass, additionalCode);
            }
        }
        return proxyClass;
    }
}