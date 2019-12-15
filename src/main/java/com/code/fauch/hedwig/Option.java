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
import java.util.Objects;

/**
 * Option for READ and WRITE requests.
 * https://tools.ietf.org/html/rfc1350
 * 
 * @author c.fauch
 *
 */
public final class Option {
    
    static final String BLKSIZE = "blksize";
    static final String TIMEOUT = "timeout";
    static final String TSIZE = "tsize";
    
    /**
     * Label of the option: "blksize",  "timeout", "tsize"
     */
    private final String label;
    
    /**
     * The value of the option.
     */
    private final long value;

    /**
     * Constructor.
     * 
     * @param lbl the label of the option
     * @param value the value of the option
     */
    private Option(final String lbl, final long value) {
        this.label = lbl;
        this.value = value;
    }

    /**
     * Builds and returns 'tsize' option.
     * https://tools.ietf.org/html/rfc1784
     * 
     * @param value The size in bytes of the file to be transfered.
     * @return the corresponding option. 
     */
    public static Option tsize(final long value) {
        return new Option(TSIZE, value);
    }

    /**
     * Builds and returns 'timeout' option.
     * https://tools.ietf.org/html/rfc1784
     * 
     * @param value The number of seconds to wait before retransmitting a packet.
     * Valid values range between "1" and "255" octets, inclusive
     * @return the corresponding option. 
     */
    public static Option timeout(final long value) {
        return new Option(TIMEOUT, value);
    }

    /**
     * Builds and returns 'blksize' option.
     * https://tools.ietf.org/html/rfc1783
     * 
     * @param value size in bytes of expected data in TFTP data packets
     * Valid values range between "8" and "65464" octets, inclusive.
     * @return the corresponding option. 
     */
    public static Option blksize(final long value) {
        return new Option(BLKSIZE, value);
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the value
     */
    public long getValue() {
        return value;
    }

    /**
     * Encode the option.
     * 
     * @return the corresponding bytes sequence
     * @throws UnsupportedEncodingException 
     */
    byte[] encode() throws UnsupportedEncodingException {
        final byte[] optEnc = (this.label + "\0").getBytes("US-ASCII");
        final byte[] valEnc = (this.value + "\0").getBytes("US-ASCII");
        final ByteBuffer buffer = ByteBuffer.allocate(optEnc.length + valEnc.length);
        buffer.put(optEnc);
        buffer.put(valEnc);
        return buffer.array();
    }
    
    /**
     * Read option from byte buffer.
     * 
     * @param buffer the byte buffer (not null)
     * @return the corresponding option or null if not found in buffer. 
     * @throws UnsupportedEncodingException
     */
    static Option decode(final ByteBuffer buffer) throws UnsupportedEncodingException {
        byte[] lbl = null;
        int offset = Objects.requireNonNull(buffer, "buffer is missing").position();
        final int length = buffer.remaining();
        for (int i = 0; i < length; i++) {
            if (buffer.get() == 0) {
                if (lbl == null) {
                    lbl = new byte[i];
                    buffer.get(offset, lbl);
                } else {
                    final String name = new String(lbl, "US-ASCII");
                    final byte[] content = new byte[i - lbl.length - 1];
                    buffer.get(offset, content);
                    final String value = new String(content, "US-ASCII");
                    return new Option(name, Long.parseLong(value));
                }
                offset = buffer.position();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Option [label=" + label + ", value=" + value + "]";
    }
    
}
