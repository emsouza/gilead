package net.sf.gilead.core.beanlib.mapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.gilead.core.beanlib.ClassMapper;

/**
 * Class mapper based on explicitly parameterized classes
 *
 * @author Olaf Kock, Florian Siebert
 */
public class ExplicitClassMapper implements ClassMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExplicitClassMapper.class);

    /**
     * The maps of the domain class to their target class correspondence.
     */
    private Map<Class<?>, Class<?>> domainToTargetMap = new HashMap<>();

    private Map<Class<?>, Class<?>> targetToDomainMap = new HashMap<>();

    /**
     * Constructor
     */
    public ExplicitClassMapper() {}

    /**
     * Add an association between source- and Clone-Class<?>, where source is the domain class that shall be cloned to
     * the given clone class, in order to be transferable via gwt to the client.
     *
     * @param sourceclass your hibernate-domain-class
     * @param targetclass the class to be transferred to the gwt client
     */
    public void addAssociation(Class<?> sourceclass, Class<?> targetclass) {
        domainToTargetMap.put(sourceclass, targetclass);
        targetToDomainMap.put(targetclass, sourceclass);
    }

    /**
     * Set the associations between source- and Clone-Class<?>. All previous association get deleted.
     *
     * @author Norman Maurer
     * @param mappings map with sourcClass as key and targetClass as value
     */
    public void setAssociations(Map<Class<?>, Class<?>> mappings) {
        domainToTargetMap.clear();
        targetToDomainMap.clear();
        Iterator<Class<?>> iter = mappings.keySet().iterator();

        while (iter.hasNext()) {
            Class<?> srcClassName = iter.next();
            Class<?> targetClassName = mappings.get(srcClassName);

            addAssociation(srcClassName, targetClassName);
        }
    }

    @Override
    public Class<?> getTargetClass(Class<?> sourceClass) {
        Class<?> result = domainToTargetMap.get(sourceClass);
        LOGGER.trace("Target class for " + sourceClass.getCanonicalName() + ": " + (result == null ? "null" : result.getCanonicalName()));
        return result;
    }

    @Override
    public Class<?> getSourceClass(Class<?> targetClass) {
        Class<?> result = targetToDomainMap.get(targetClass);
        LOGGER.trace("Source class for " + targetClass.getCanonicalName() + ": " + (result == null ? "null" : result.getCanonicalName()));
        return result;
    }
}
