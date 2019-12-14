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
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * This class describes a TFTP packet.
 * 
 * @author c.fauch
 *
 */
public abstract class AbsPacket {

    /**
     * The operation code associated with the TFTP packet.
     */
    private final EOperation operation;
    
    /**
     * The host name:
     * - destination host name if this packet is a request.
     * - source host name if this packet is response. 
     */
    private final InetAddress host;
    
    /**
     * The port number:
     * - destination port if this packet is a request
     * - source port if this packet is a response.
     */
    private final int port;
    
    /**
     * Constructor.
     * 
     * @param op the operation code (not null)
     * @param host the host name (not null)
     * @param port the port number
     */
    AbsPacket(final EOperation op, final InetAddress host, final int port) {
        this.operation = op;
        this.host = host;
        this.port = port;
    }

    /**
     * @return the operation
     */
    public EOperation getOperation() {
        return operation;
    }

    /**
     * @return the host
     */
    public InetAddress getHost() {
        return host;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }
    
    /**
     * Build a datagram packet from this TFTP packet.
     * 
     * @return the corresponding datagram packet ready to be sent.
     * @throws UnsupportedEncodingException 
     */
    public DatagramPacket build() throws UnsupportedEncodingException {
        final byte[] body = encode();
        return new DatagramPacket(body, body.length, this.host, this.port);
    }
    
    /**
     * Encode the payload this packet.
     * 
     * @return the encoded payload
     * @throws UnsupportedEncodingException
     */
    abstract byte[] encode() throws UnsupportedEncodingException;
    
}
