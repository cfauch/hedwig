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
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * TFTP response ACK, OACK, DATA, ERROR
 * 
 * @author c.fauch
 *
 */
public final class Response extends AbsPacket {

    /**
     * The block target of the response
     */
    private final int block;

    /**
     * The optional data of the response.
     */
    private final byte[] data;
    
    /**
     * Options.
     */
    private final Map<String, Option> options;
    
    /**
     * Constructor.
     * 
     * @param op Operation READ or WRITE (not null)
     * @param block the block number of the data packet.
     * @param data the data of the packet: error msg for ERROR, file chunk for DATA (may be empty)
     * @param host the destination host name
     * @param port the destination port
     * @param options
     */
    private Response(final EOperation op, final int block, final byte[] data, final InetAddress host, final int port,
            final Map<String, Option> opts) {
        super(op, host, port);
        this.block = block;
        this.data = data;
        this.options = opts;
    }
    
    /**
     * Builds and returns a new ERROR response.
     * 
     * @param error the error code (not null)
     * @param msg the error message
     * @param host the destination host name
     * @param port the destination port
     * @return the corresponding ERROR response
     * @throws UnsupportedEncodingException 
     */
    public static Response error(final EError error, final String msg, final InetAddress host, final int port) throws UnsupportedEncodingException {
        return new Response(
                EOperation.ERROR, 
                Objects.requireNonNull(error, "missing error").getCode(), 
                msg == null ? new byte[0] : (msg + "\0").getBytes("US-ASCII"), 
                host, 
                port,
                Collections.emptyMap()
        );
    }

    /**
     * Builds and returns a new DATA response.
     * 
     * @param the block number
     * @param the chunk data (not null)
     * @param host the destination host name
     * @param port the destination port
     * @return the corresponding ERROR response
     * @throws UnsupportedEncodingException 
     */
    public static Response data(final int block, final byte[] data, final InetAddress host, final int port) {
        return new Response(
                EOperation.DATA, 
                block, 
                Objects.requireNonNull(data, "missing data"), 
                host, 
                port,
                Collections.emptyMap()
        );
    }
    
    /**
     * Builds and returns a new ACK response
     * 
     * @param block the block number
     * @param host the destination host name
     * @param port the destination port
     * @return the corresponding ACK error
     */
    public static Response ack(final int block, final InetAddress host, final int port) {
        return new Response(EOperation.ACK, block, new byte[0], host, port, Collections.emptyMap());
    }

    /**
     * Builds and returns a new OACK response
     * https://tools.ietf.org/html/rfc1782
     * 
     * @param block the block number
     * @param host the destination host name
     * @param port the destination port
     * @return the corresponding ACK error
     */
    public static Response oack(final int block, final InetAddress host, final int port, final Option... options) {
        final HashMap<String, Option> opts = new HashMap<String, Option>(options.length);
        for (Option opt : options) {
            opts.put(opt.getLabel(), opt);
        }
        return new Response(EOperation.OACK, block, new byte[0], host, port, opts);
    }

    /**
     * Read a response from datagram packet.
     * 
     * @param packet the datagram packet (not null)
     * @return the corresponding response
     * @throws UnsupportedEncodingException
     * @throws TFTPException if the response was a TFTP error.
     */
    public static Response from(final DatagramPacket packet) throws UnsupportedEncodingException, TFTPException {
        byte[] content = Arrays.copyOf(Objects.requireNonNull(packet, "missing packet").getData(), packet.getLength());
        final ByteBuffer buffer = ByteBuffer.wrap(content);
        final EOperation op = EOperation.from(buffer.getShort());
        short block = 0;
        if (op == EOperation.ERROR) {
            throw TFTPException.from(buffer);
        } if (op == EOperation.OACK) {
            final HashMap<String, Option> opts = new HashMap<>();
            Option opt = Option.decode(buffer);
            while(opt != null) {
                opts.put(opt.getLabel(), opt);
                opt = Option.decode(buffer);
            }
            return new Response(
                    op, 
                    Short.toUnsignedInt(block), 
                    new byte[0],
                    packet.getAddress(), 
                    packet.getPort(), 
                    opts);
        } else {
            block = buffer.getShort();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            return new Response(
                    op, 
                    Short.toUnsignedInt(block), 
                    data,
                    packet.getAddress(), 
                    packet.getPort(), 
                    Collections.emptyMap());
        }
    }
    
    /**
     * @return the block
     */
    public int getBlock() {
        return block;
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Returns the tsize option
     * 
     * @return the tszize option or null if this option is not defined
     */
    public Option getTSize() {
        return this.options.get(Option.TSIZE);
    }
    
    /**
     * Returns the timeout option.
     * 
     * @return the timeout option or null if this option is not defined
     */
    public Option getTimeout() {
        return this.options.get(Option.TIMEOUT);
    }
    
    /**
     * Returns the blksize option.
     * 
     * @return the blksize option or null if this option is not defined.
     */
    public Option getBlksize() {
        return this.options.get(Option.BLKSIZE);
    }
    
    @Override
    byte[] encode() throws UnsupportedEncodingException {
        if (getOperation() != EOperation.OACK) {
            final ByteBuffer buffer = ByteBuffer.allocate(4 + this.data.length);
            buffer.putShort(getOperation().getCode());
            buffer.putShort((short) this.block);
            if (getOperation() == EOperation.DATA || getOperation() == EOperation.ERROR) {
                buffer.put(this.data);
            }
            return buffer.array();
        } else {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (Option opt : this.options.values()) {
                final byte[] buff = opt.encode();
                out.write(buff, 0, buff.length);
            }
            final byte[] opts = out.toByteArray();
            final ByteBuffer buffer = ByteBuffer.allocate(2 + opts.length);
            buffer.putShort(getOperation().getCode());
            buffer.put(opts);
            return buffer.array();
        }
    }

}
