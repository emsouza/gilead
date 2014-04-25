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

/**
 * String Utilities.
 * 
 * @author Joe D. Velopar
 */
public class StringUtils {
	public static final String[][] EMPTY_2D_ARRAY = {};

	public static String capitalize(String name) {
		if (name == null || name.length() == 0) {
			return name;
		}
		char chars[] = name.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}

    /** 
     * Returns the bytes from the given String of ascii characters fast,
     * using the least amount of memory in the process.
     * 
     * @throws IllegalArgumentException if the input string is not ASCII.
     */
    public static byte[] getBytesFromAsciiString(String asciiString) 
    {
        return getBytesFromAsciiString(asciiString, true);
    }
    
    /** 
     * Returns the bytes from the given String of ascii characters fast.
     * 
     * @throws IllegalArgumentException if the input string is not ASCII.
     */
    public static byte[] getBytesFromAsciiString(String asciiString, boolean leastMemory) 
    {
        final int len = asciiString.length();
        byte[] ba = new byte[len];
        char c = 0;
        
        if (leastMemory) {
            for (int i=0; i < len; i++) {
                c = asciiString.charAt(i);
                if (c >>> 8 != 0)
                    throw new IllegalArgumentException("Input string is not ASCII: " + asciiString);
                ba[i] = (byte)c;
            }
        }
        else {
            char[] ca = asciiString.toCharArray();
            
            for (int i=0; i < len; i++) {
                c = ca[i];
                
                if (c >>> 8 != 0)
                    throw new IllegalArgumentException("Input string is not ASCII: " + asciiString);
                ba[i] = (byte)ca[i];
            }
        }
        return ba;
    }
//    public boolean isEmpty(String s) {
//        return s == null || s.trim().length() == 0;
//    }
}
