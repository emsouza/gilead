/*
 * Copyright 2007 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sf.gilead.proxy.gwt;

import java.io.PrintWriter;

import net.sf.gilead.pojo.base.ILightEntity;
import net.sf.gilead.proxy.xml.AdditionalCode;
import net.sf.gilead.proxy.xml.AdditionalCodeReader;
import net.sf.gilead.proxy.xml.Attribute;
import net.sf.gilead.proxy.xml.Method;

import org.apache.commons.lang3.StringUtils;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Proxy generator for GWT
 *
 * @author bruno.marchesson
 */
public abstract class AbstractGwtProxyGenerator extends Generator {
    // ----
    // Attribute
    // ----
    /**
     * Associated additional code file path
     */
    protected String _additionalCodePath;

    /**
     * Additional code
     */
    protected AdditionalCode _additionalCode;

    // -------------------------------------------------------------------------
    //
    // Constructor
    //
    // -------------------------------------------------------------------------
    /**
     * Constructor
     */
    protected AbstractGwtProxyGenerator(String filePath) {
        _additionalCodePath = filePath;
    }

    // -------------------------------------------------------------------------
    //
    // Generator interface
    //
    // -------------------------------------------------------------------------
    /*
     * (non-Javadoc)
     * @see com.google.gwt.core.ext.Generator#generate(com.google.gwt.core.ext.TreeLogger ,
     * com.google.gwt.core.ext.GeneratorContext, java.lang.String)
     */
    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        try {
            // Get target class
            //
            TypeOracle typeOracle = context.getTypeOracle();
            JClassType requestedClass = typeOracle.getType(typeName);

            // Do not generate proxy for a class already implementing
            // ILightEntity interface
            if (isLazyPojo(requestedClass) == true) {
                // LOGGER is not compatible from GWT 1.4 to GWT 1.5 !
                // logger.log(TreeLogger.INFO,
                // requestedClass.getClass().getName() +
                // " is already a lazy pojo : proxy not needed.",
                // null);
                return null;
            }

            // Read additional data information
            //
            if (_additionalCode == null) {
                _additionalCode = AdditionalCodeReader.readFromFile(_additionalCodePath);
            }

            // Compute proxy information
            //
            String packageName = requestedClass.getPackage().getName();
            String proxyClassName = requestedClass.getSimpleSourceName() + _additionalCode.getSuffix();
            String qualifiedProxyClassName = packageName + "." + proxyClassName;
            String className = requestedClass.getName();

            // LOGGER is not compatible from GWT 1.4 to GWT 1.5 !
            // logger.log(TreeLogger.INFO,
            // "Generating proxy " + qualifiedProxyClassName +
            // " for class " + className,
            // null);

            // Create source writer
            //
            SourceWriter sourceWriter = getSourceWriter(logger, context, packageName, proxyClassName, className, _additionalCode);
            if (sourceWriter != null) {
                generateProxy(logger, sourceWriter, _additionalCode);
                sourceWriter.commit(logger);

                // LOGGER is not compatible from GWT 1.4 to GWT 1.5 !
                // logger.log(TreeLogger.INFO, "Proxy generation OK", null);
            }
            return qualifiedProxyClassName;
        } catch (Exception ex) {
            // LOGGER is not compatible from GWT 1.4 to GWT 1.5 !
            // logger.log(TreeLogger.ERROR, "Proxy generation error", ex);

            throw new UnableToCompleteException();
        }
    }

    // -------------------------------------------------------------------------
    //
    // Internal method
    //
    // -------------------------------------------------------------------------
    /**
     * Create the needed source writer
     */
    protected SourceWriter getSourceWriter(TreeLogger logger, GeneratorContext context, String packageName, String className, String superclassName,
            AdditionalCode additionalCode) {
        PrintWriter printWriter = context.tryCreate(logger, packageName, className);
        if (printWriter == null) {
            // Could not create print writer (the new type already exists)
            //
            return null;
        }

        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, className);

        // Add superclass
        //
        composerFactory.setSuperclass(superclassName);

        // Add implemented interface if needed
        //
        if (StringUtils.isEmpty(additionalCode.getImplementedInterface()) == false) {
            composerFactory.addImplementedInterface(additionalCode.getImplementedInterface());
        }

        // Create source writer
        //
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
        //
        if (additionalCode.getAttributes() != null) {
            for (Attribute attribute : additionalCode.getAttributes()) {
                generateAttribute(sourceWriter, attribute);

            }
        }

        // Generate methods
        //
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
        //
        if (StringUtils.isEmpty(attribute.getJavadoc()) == false) {
            sourceWriter.beginJavaDocComment();
            sourceWriter.println(attribute.getJavadoc());
            sourceWriter.endJavaDocComment();
        }

        // Add attribute
        //
        sourceWriter.println(attribute.toJava5String());
        //
    }

    /**
     * Generates an additional attribute
     * 
     * @param sourceWriter
     * @param attribute
     */
    protected void generateMethod(SourceWriter sourceWriter, Method method) {
        // Javadoc comment if needed
        //
        if (StringUtils.isEmpty(method.getJavadoc()) == false) {
            sourceWriter.beginJavaDocComment();
            sourceWriter.println(method.getJavadoc());
            sourceWriter.endJavaDocComment();
        }

        // Add Signature and code
        //
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
        //
        String superclassName = clazz.getSuperclass().getQualifiedSourceName();
        if ((net.sf.gilead.pojo.java5.LightEntity.class.getCanonicalName().equals(superclassName))
                || (net.sf.gilead.pojo.java5.legacy.LightEntity.class.getCanonicalName().equals(superclassName))
                || (net.sf.gilead.pojo.gwt.LightEntity.class.getCanonicalName().equals(superclassName))) {
            return true;
        }

        // Check implemented interfaces
        //
        JClassType[] interfaceList = clazz.getImplementedInterfaces();
        for (JClassType element : interfaceList) {
            String interfaceName = element.getQualifiedSourceName();
            if (ILightEntity.class.getName().equals(interfaceName)) {
                // ILightEntity
                //
                return true;
            }

        }

        // Not lazy pojo
        //
        return false;
    }
}
