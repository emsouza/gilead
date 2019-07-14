package net.sf.gilead.proxy.gwt;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import java.io.PrintWriter;

import org.apache.commons.lang3.StringUtils;

import net.sf.gilead.pojo.base.ILightEntity;
import net.sf.gilead.proxy.xml.AdditionalCode;
import net.sf.gilead.proxy.xml.AdditionalCodeReader;
import net.sf.gilead.proxy.xml.Attribute;
import net.sf.gilead.proxy.xml.Method;

/**
 * Proxy generator for GWT
 *
 * @author bruno.marchesson
 */
public abstract class AbstractGwtProxyGenerator extends Generator {

    /**
     * Associated additional code file path
     */
    protected String additionalCodePath;

    /**
     * Additional code
     */
    protected AdditionalCode additionalCode;

    /**
     * Constructor
     */
    protected AbstractGwtProxyGenerator(String filePath) {
        additionalCodePath = filePath;
    }

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        try {
            // Get target class
            TypeOracle typeOracle = context.getTypeOracle();
            JClassType requestedClass = typeOracle.getType(typeName);

            // Do not generate proxy for a class already implementing ILightEntity interface
            if (isLazyPojo(requestedClass) == true) {
                // LOGGER is not compatible from GWT 1.4 to GWT 1.5 !
                // logger.info(requestedClass.getClass().getName() + "is already a lazy pojo : proxy not needed.");
                return null;
            }

            // Read additional data information
            if (additionalCode == null) {
                additionalCode = AdditionalCodeReader.readFromFile(additionalCodePath);
            }

            // Compute proxy information
            String packageName = requestedClass.getPackage().getName();
            String proxyClassName = requestedClass.getSimpleSourceName() + additionalCode.getSuffix();
            String qualifiedProxyClassName = packageName + "." + proxyClassName;
            String className = requestedClass.getName();

            // LOGGER is not compatible from GWT 1.4 to GWT 1.5 !
            // logger.info("Generating proxy " + qualifiedProxyClassName + " for class " + className);

            // Create source writer
            SourceWriter sourceWriter = getSourceWriter(logger, context, packageName, proxyClassName, className, additionalCode);
            if (sourceWriter != null) {
                generateProxy(logger, sourceWriter, additionalCode);
                sourceWriter.commit(logger);

                // LOGGER is not compatible from GWT 1.4 to GWT 1.5 !
                // logger.info("Proxy generation OK");
            }
            return qualifiedProxyClassName;
        } catch (Exception ex) {
            // LOGGER is not compatible from GWT 1.4 to GWT 1.5 !
            // logger.error("Proxy generation error", ex);
            throw new UnableToCompleteException();
        }
    }

    /**
     * Create the needed source writer
     */
    protected SourceWriter getSourceWriter(TreeLogger logger, GeneratorContext context, String packageName, String className, String superclassName,
            AdditionalCode additionalCode) {
        PrintWriter printWriter = context.tryCreate(logger, packageName, className);
        if (printWriter == null) {
            // Could not create print writer (the new type already exists)
            return null;
        }

        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, className);

        // Add superclass
        composerFactory.setSuperclass(superclassName);

        // Add implemented interface if needed
        if (StringUtils.isEmpty(additionalCode.getImplementedInterface()) == false) {
            composerFactory.addImplementedInterface(additionalCode.getImplementedInterface());
        }

        // Create source writer
        return composerFactory.createSourceWriter(context, printWriter);
    }

    /**
     * Add additional code to the instrumented class
     * 
     * @param logger
     * @param sourceWriter
     * @param additionalCode
     */
    protected void generateProxy(TreeLogger logger, SourceWriter sourceWriter, AdditionalCode additionalCode) {
        // Generate attribute
        if (additionalCode.getAttributes() != null) {
            for (Attribute attribute : additionalCode.getAttributes()) {
                generateAttribute(sourceWriter, attribute);

            }
        }

        // Generate methods
        if (additionalCode.getMethods() != null) {
            for (Method method : additionalCode.getMethods()) {
                generateMethod(sourceWriter, method);
            }
        }
    }

    /**
     * Generates an additional attribute
     * 
     * @param sourceWriter
     * @param attribute
     */
    protected void generateAttribute(SourceWriter sourceWriter, Attribute attribute) {
        // Javadoc comment if needed
        if (StringUtils.isEmpty(attribute.getJavadoc()) == false) {
            sourceWriter.beginJavaDocComment();
            sourceWriter.println(attribute.getJavadoc());
            sourceWriter.endJavaDocComment();
        }

        // Add attribute
        sourceWriter.println(attribute.toJava5String());
    }

    /**
     * Generates an additional attribute
     * 
     * @param sourceWriter
     * @param attribute
     */
    protected void generateMethod(SourceWriter sourceWriter, Method method) {
        // Javadoc comment if needed
        if (StringUtils.isEmpty(method.getJavadoc()) == false) {
            sourceWriter.beginJavaDocComment();
            sourceWriter.println(method.getJavadoc());
            sourceWriter.endJavaDocComment();
        }

        // Add Signature and code
        sourceWriter.println(method.computeJava5Signature());
        sourceWriter.println(method.getCode());
    }

    /**
     * Check if the argument class already implements ILightEntity
     * 
     * @param clazz
     * @return
     */
    protected boolean isLazyPojo(JClassType clazz) {
        // Check superclass name
        String superclassName = clazz.getSuperclass().getQualifiedSourceName();
        if ((net.sf.gilead.pojo.java5.LightEntity.class.getCanonicalName().equals(superclassName))
                || (net.sf.gilead.pojo.java5.legacy.LightEntity.class.getCanonicalName().equals(superclassName))
                || (net.sf.gilead.pojo.gwt.LightEntity.class.getCanonicalName().equals(superclassName))) {
            return true;
        }

        // Check implemented interfaces
        JClassType[] interfaceList = clazz.getImplementedInterfaces();
        for (JClassType element : interfaceList) {
            String interfaceName = element.getQualifiedSourceName();
            if (ILightEntity.class.getName().equals(interfaceName)) {
                // ILightEntity
                return true;
            }

        }

        // Not lazy pojo
        return false;
    }
}
