/*
 * Copyright 2019 Claire Fauch
 * Licensed under the Apache License, Version 2.0 (the "License");
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
package com.code.fauch.hedwig;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Encapsulates TFTP error into java exception.
 * 
 * @author c.fauch
 *
 */
public class TFTPException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * The TFTP error
     */
    private final EError err;

    /**
     * Constructor.
     * 
     * @param err the TFTP error
     * @param msg the error message
     */
    public TFTPException(final EError err, final String msg) {
        super(msg);
        this.err = err;
    }

    /**
     * Constructor.
     * 
     * @param err the TFTP error
     */
    public TFTPException(final EError err) {
        super();
        this.err = err;
    }

    /**
     * Returns the TFTP error
     * @return err
     */
    public EError getError() {
        return this.err;
    }

    /**
     * Extract error from buffer.
     * 
     * @param buffer the buffer to read
     * @return the corresponding exception
     * @throws UnsupportedEncodingException
     */
    static TFTPException from(final ByteBuffer buffer) throws UnsupportedEncodingException {
        final short block = buffer.getShort();
        final byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        if (data.length == 0) {
            return new TFTPException(EError.from(block));
        }
        return new TFTPException(EError.from(block), new String(data, "US-ASCII"));
    }
    
}
