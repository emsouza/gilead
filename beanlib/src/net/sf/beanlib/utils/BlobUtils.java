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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

import net.sf.beanlib.BeanlibException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

/**
 * Blob Utilities.
 * 
 * @author Joe D. Velopar
 */
public class BlobUtils {
	private Logger log = Logger.getLogger(this.getClass());
	
	public byte[] toByteArray(Blob fromBlob) {
		return toByteArray(fromBlob, 4000);
	}
	
	public byte[] toByteArray(Blob fromBlob, int bufferSize) {
		if (fromBlob == null)
			return ArrayUtils.EMPTY_BYTE_ARRAY;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			return toByteArrayImpl(fromBlob, baos, bufferSize);
		} catch (SQLException e) {
			log.error("", e);
			throw new BeanlibException(e);
		} catch (IOException e) {
			log.error("", e);
			throw new BeanlibException(e);
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException ex) {
					log.warn("", ex);
				}
			}
		}
	}

	private byte[] toByteArrayImpl(Blob fromBlob, ByteArrayOutputStream baos, int bufferSize)
			throws SQLException, IOException 
    {
		byte[] buf = new byte[bufferSize];
		InputStream is = fromBlob.getBinaryStream();
		try {
			for (;;) {
				int dataSize = is.read(buf);

				if (dataSize == -1)
					break;
				baos.write(buf, 0, dataSize);
			}
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
					log.warn("", ex);
				}
			}
		}
		return baos.toByteArray();
	}
}
