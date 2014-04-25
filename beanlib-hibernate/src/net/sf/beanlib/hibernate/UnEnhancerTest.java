/*
 * Copyright 2009 The Apache Software Foundation.
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
package net.sf.beanlib.hibernate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * @author Hanson Char
 */
public class UnEnhancerTest {
    @Test
    public void test() {
        assertTrue(UnEnhancer.isDefaultCheckCGLib());
        assertTrue(UnEnhancer.isCheckCGLib());

        UnEnhancer.setCheckCGLibForThisThread(false);
        try {
            assertTrue(UnEnhancer.isDefaultCheckCGLib());
            assertFalse(UnEnhancer.isCheckCGLib());
        } finally {
            UnEnhancer.clearThreadLocal();
        }

        UnEnhancer.setCheckCGLibForThisThread(true);
        try {
            assertTrue(UnEnhancer.isDefaultCheckCGLib());
            assertTrue(UnEnhancer.isCheckCGLib());
        } finally {
            UnEnhancer.clearThreadLocal();
        }
        
        UnEnhancer.setDefaultCheckCGLib(false);
        assertFalse(UnEnhancer.isDefaultCheckCGLib());
        assertFalse(UnEnhancer.isCheckCGLib());

        UnEnhancer.setCheckCGLibForThisThread(false);
        try {
            assertFalse(UnEnhancer.isDefaultCheckCGLib());
            assertFalse(UnEnhancer.isCheckCGLib());
        } finally {
            UnEnhancer.clearThreadLocal();
        }

        UnEnhancer.setCheckCGLibForThisThread(true);
        try {
            assertFalse(UnEnhancer.isDefaultCheckCGLib());
            assertTrue(UnEnhancer.isCheckCGLib());
        } finally {
            UnEnhancer.clearThreadLocal();
        }
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(new Object(){}.getClass().getEnclosingClass());
    }
}
