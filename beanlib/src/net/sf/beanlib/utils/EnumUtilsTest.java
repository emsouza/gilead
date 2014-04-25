/*
 * Copyright 2005 The Apache Software Foundation.
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
package net.sf.beanlib.utils;

import junit.framework.TestCase;

/**
 * @author Joe D. Velopar
 */
public class EnumUtilsTest extends TestCase
{
    public void test_StringEnum_ValidValues_External() {
        StringEnum[] values = StringEnum.values();
        
        for (StringEnum se : values) {
            String externalized = se.externalize();
            System.out.println("name()=" + se.name() + ", toExternal()=" + externalized);
            StringEnum e = EnumUtils.fromExternal(StringEnum.class, externalized);
            assertTrue(se == e);
        }
    }

    public void test_StringEnum_ValidValues_String() {
        StringEnum[] values = StringEnum.values();
        
        for (StringEnum se : values) {
            String s = se.toString();
            System.out.println("name()=" + se.name() + ", toString()=" + s);
            StringEnum e = EnumUtils.fromString(StringEnum.class, s);
            assertTrue(se == e);
        }
    }

    public void test_StringEnum_ValidValues_Ordinal() {
        StringEnum[] values = StringEnum.values();
        
        for (StringEnum se : values) {
            int ordinal = se.ordinal();
            System.out.println("name()=" + se.name() + ", ordinal()=" + ordinal);
            StringEnum e = EnumUtils.fromOrdinal(StringEnum.class, ordinal);
            assertTrue(se == e);
        }
    }
    
    public void test_StringEnum_InvalidValue_External() {
        assertNull(EnumUtils.fromExternal(StringEnum.class, ""));
        assertNull(EnumUtils.fromExternal(StringEnum.class, "xxx"));
    }

    public void test_StringEnum_InvalidValue_String() {
        assertNull(EnumUtils.fromString(StringEnum.class, ""));
        assertNull(EnumUtils.fromString(StringEnum.class, "xxx"));
    }

    public void test_StringEnum_InvalidValue_Ordinal() {
        assertNull(EnumUtils.fromOrdinal(StringEnum.class, -1));
        assertNull(EnumUtils.fromOrdinal(StringEnum.class, 99));
    }

    public void test_IntegerEnum_ValidValues_External() {
        IntegerEnum[] values = IntegerEnum.values();
        
        for (IntegerEnum se : values) {
            Integer externalized = se.externalize();
            System.out.println("name()=" + se.name() + ", toExterna()=" + externalized);
            IntegerEnum e = EnumUtils.fromExternal(IntegerEnum.class, externalized);
            assertTrue(se == e);
        }
    }

    public void test_IntegerEnum_ValidValues_String() {
        IntegerEnum[] values = IntegerEnum.values();
        
        for (IntegerEnum se : values) {
            String s = se.toString();
            System.out.println("name()=" + se.name() + ", toString()=" + s);
            IntegerEnum e = EnumUtils.fromString(IntegerEnum.class, s);
            assertTrue(se == e);
        }
    }

    public void test_IntegerEnum_ValidValues_Ordinal() {
        IntegerEnum[] values = IntegerEnum.values();
        
        for (IntegerEnum se : values) {
            int ordinal = se.ordinal();
            System.out.println("name()=" + se.name() + ", ordinal()=" + ordinal);
            IntegerEnum e = EnumUtils.fromOrdinal(IntegerEnum.class, ordinal);
            assertTrue(se == e);
        }
    }
    
    public void test_IntegerEnum_InvalidValue_External() {
        assertNull(EnumUtils.fromExternal(IntegerEnum.class, 777));
        assertNull(EnumUtils.fromExternal(IntegerEnum.class, 123));
        assertNull(EnumUtils.fromExternal(IntegerEnum.class, null));
    }
    
    public void test_IntegerEnum_InvalidValue_String() {
        assertNull(EnumUtils.fromString(IntegerEnum.class, "777"));
        assertNull(EnumUtils.fromString(IntegerEnum.class, "123"));
        assertNull(EnumUtils.fromString(IntegerEnum.class, null));
    }
    
    public void test_IntegerEnum_InvalidValue_Ordinal() {
        assertNull(EnumUtils.fromOrdinal(IntegerEnum.class, 777));
        assertNull(EnumUtils.fromOrdinal(IntegerEnum.class, 123));
        assertNull(EnumUtils.fromOrdinal(IntegerEnum.class, -1));
    }
}
