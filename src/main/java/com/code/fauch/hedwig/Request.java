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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * READ/WRITE TFTP request.
 * 
 * @author c.fauch
 *
 */
final class Request extends AbsPacket {

    /**
     * The operation target file name.
     */
    private final String fileName;

    /**
     * The transfer mode.
     */
    private final String mode;
    
    /**
     * The request option (may be empty).
     */
    private final Option[] options;
    
    /**
     * Constructor.
     * 
     * @param op Operation READ or WRITE (not null)
     * @param fileName the remote file name
     * @param mode the transfert mode: "netascii", "octet", "mail"
     * @param host the destination host name
     * @param port the destination port
     * @param opts the request options (not null)
     */
    private Request(final EOperation op, final String fileName, final String mode, final InetAddress host, final int port,
            final Option[] opts) {
        super(op, host, port);
        this.fileName = fileName;
        this.mode = mode;
        this.options = opts;
    }

    /**
     * Builds and returns a new request to write a remote file.
     * 
     * @param fileName the name of the remote file to write
     * @param mode the transfert mode: "netascii", "octet", "mail"
     * @param host the destination host name
     * @param port the destination port
     * @param opts the request options (not null)
     * @return the corresponding WRITE request
     */
    public static Request write(final String fileName, final String mode, final InetAddress host, final int port,
            final Option... opts) {
        return new Request(EOperation.WRITE, fileName, mode, host, port, opts);
    }
    
    /**
     * Builds and returns a new request to read a remote file.
     * 
     * @param fileName the name of the remote file to read
     * @param mode the transfert mode: "netascii", "octet", "mail"
     * @param host the destination host name
     * @param port the destination port
     * @param opts the request options (not null)
     * @return the corresponding READ request
     */
    public static Request read(final String fileName, final String mode, final InetAddress host, final int port,
            final Option... opts) {
        return new Request(EOperation.READ, fileName, mode, host, port, opts);
    }
    
    @Override
    byte[] encode() throws UnsupportedEncodingException {
        final byte[] modeEnc = (this.mode + "\0").getBytes("US-ASCII");
        final byte[] fileEnc = (this.fileName + "\0").getBytes("US-ASCII");
        final byte[] optsEnc = encodeOpts();
        final ByteBuffer buffer = ByteBuffer.allocate(2 + modeEnc.length + fileEnc.length + optsEnc.length);
        buffer.putShort(getOperation().getCode());
        buffer.put(fileEnc);
        buffer.put(modeEnc);
        buffer.put(optsEnc);
        return buffer.array();
    }

    /**
     * Encode each options.
     * 
     * @return the corresponding byte sequence.
     * @throws UnsupportedEncodingException 
     */
    private byte[] encodeOpts() throws UnsupportedEncodingException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (Option opt : this.options) {
            final byte[] buff = opt.encode();
            out.write(buff, 0, buff.length);
        }
        return out.toByteArray();
    }

    @Override
    public String toString() {
        return "Request [fileName=" + fileName + ", mode=" + mode + ", options=" + Arrays.toString(options) + "]";
    }
    
}
