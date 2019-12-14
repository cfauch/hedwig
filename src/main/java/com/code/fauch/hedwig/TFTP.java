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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * TFTP client class.
 * 
 * @author c.fauch
 *
 */
public final class TFTP {

    /**
     * Maximum data size
     */
    private static final int DATA_SIZE = 512;

    /**
     * The currently open socket on which transfer should be done.
     */
    private final DatagramSocket socket;
    
    /**
     * Constructor.
     * 
     * @param socket the socket to use (not null).
     */
    public TFTP(final DatagramSocket socket) {
        this.socket = socket;
    }
    
    /**
     * Put a file.
     * 
     * @param host the destination host
     * @param port the destination port
     * @param input the input stream open on the file to send 
     * @param fileName the name of the resulting remote file
     * @param mode the send mode: "octet", "netascii", "mail"
     * @param options options: "blksize", "timeout", "tsize"
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws TFTPException
     */
    public void write(final InetAddress host, final int port, final InputStream input, final String fileName, 
            final String mode, final Option... options) throws UnsupportedEncodingException, IOException, TFTPException {
        final DatagramPacket rcvpacket = new DatagramPacket(new byte[DATA_SIZE + 4], DATA_SIZE + 4);
        send(Request.write(fileName, mode, host, port, options));
        Response resp = rcv(rcvpacket);
        final InetAddress host2Use = resp.getHost();
        final int port2Use = resp.getPort();
        final Option blksize = resp.getBlksize();
        final byte[] writeBuff = new byte[blksize == null ? DATA_SIZE : (int)blksize.getValue()];
        boolean goOn = true;
        int block = 0; // 0 to 65535
        while (goOn) {
            if (block == resp.getBlock()) { // Ack the current block
                if (goOn) { //Sending next data block
                    block = block >= 65535 ? 0 : (block + 1);
                    final int bytesRead = read(input, writeBuff);
                    if (bytesRead < writeBuff.length) {
                        goOn = false;
                    }
                    send(Response.data(block, Arrays.copyOf(writeBuff, bytesRead), host2Use, port2Use));
                }
            }
            //Waiting for response
            resp = rcv(rcvpacket);
        }
    }
    
    /**
     * Send a TFTP packet though the socket.
     * 
     * @param packet the TFTP packet to send (not null)
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    private void send(final AbsPacket packet) throws UnsupportedEncodingException, IOException {
        this.socket.send(packet.build());
    }
    
    /**
     * Read a TFTP response from the socket.
     * 
     * @param packet the datagram packet to use (not null)
     * @return the corresponding TFTP response
     * @throws IOException
     * @throws TFTPException
     */
    private Response rcv(final DatagramPacket packet) throws IOException, TFTPException {
        this.socket.receive(packet);
        return Response.from(packet);
    }
    
    /**
     * Fill given buffer with data read from input stream.
     * 
     * @param input the input stream to read (not null)
     * @param buffer the buffer to fill
     * @return the total number of bytes read, should be buffer size unless the
     * end of stream is reached.
     * @throws IOException
     */
    private static int read(final InputStream input, byte[] buffer) throws IOException {
        int offset = 0;
        int remaining = buffer.length;
        while (remaining > 0) {
            final int bytesRead = input.read(buffer, offset, remaining);
            if (bytesRead == -1) {
                break;
            }
            offset += bytesRead;
            remaining -= bytesRead;
        }
        return offset;
    }
    
}
