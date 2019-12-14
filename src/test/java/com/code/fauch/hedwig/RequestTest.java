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
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;


/**
 * TU for Request.
 * 
 * @author c.fauch
 *
 */
public class RequestTest {

    @Test
    public void testWriteNominal() throws UnknownHostException, UnsupportedEncodingException {
        final Request req = Request.write("fileName", "octet", InetAddress.getByName("localhost"), 65535);
        final DatagramPacket packet = req.build();
        Assert.assertEquals(packet.getPort(), req.getPort());
        Assert.assertEquals(packet.getAddress(), req.getHost());
        final byte[] content = packet.getData();
        Assert.assertEquals(packet.getLength(), content.length);
        final ByteBuffer buff = ByteBuffer.wrap(content);
        Assert.assertEquals(EOperation.WRITE.getCode(), buff.getShort());
        final byte[] dst = new byte[9];
        buff.get(dst, 0, dst.length);
        Assert.assertEquals("fileName\0", new String(dst, "US-ASCII"));
        final byte[] mode = new byte[6];
        buff.get(mode, 0, mode.length);
        Assert.assertEquals("octet\0", new String(mode, "US-ASCII"));
        Assert.assertEquals(0, buff.remaining());
    }

    @Test
    public void testWriteMissingFileName() throws Exception {
        final Request req = Request.write(null, "octet", InetAddress.getByName("localhost"), 65535);
        final DatagramPacket packet = req.build();
        Assert.assertEquals(packet.getPort(), req.getPort());
        Assert.assertEquals(packet.getAddress(), req.getHost());
        final byte[] content = packet.getData();
        Assert.assertEquals(packet.getLength(), content.length);
        final ByteBuffer buff = ByteBuffer.wrap(content);
        Assert.assertEquals(2, buff.getShort());
        final byte[] dst = new byte[5];
        buff.get(dst, 0, dst.length);
        Assert.assertEquals("null\0", new String(dst, "US-ASCII"));
        final byte[] mode = new byte[6];
        buff.get(mode, 0, mode.length);
        Assert.assertEquals("octet\0", new String(mode, "US-ASCII"));
        Assert.assertEquals(0, buff.remaining());
    }
    
    @Test
    public void testWriteMissingMode() throws UnknownHostException, UnsupportedEncodingException {
        final Request req = Request.write("fileName", null, InetAddress.getByName("localhost"), 65535);
        final DatagramPacket packet = req.build();
        Assert.assertEquals(packet.getPort(), req.getPort());
        Assert.assertEquals(packet.getAddress(), req.getHost());
        final byte[] content = packet.getData();
        Assert.assertEquals(packet.getLength(), content.length);
        final ByteBuffer buff = ByteBuffer.wrap(content);
        Assert.assertEquals(2, buff.getShort());
        final byte[] dst = new byte[9];
        buff.get(dst, 0, dst.length);
        Assert.assertEquals("fileName\0", new String(dst, "US-ASCII"));
        final byte[] mode = new byte[5];
        buff.get(mode, 0, mode.length);
        Assert.assertEquals("null\0", new String(mode, "US-ASCII"));
        Assert.assertEquals(0, buff.remaining());
    }
    
    @Test
    public void testWriteMissingHost() throws UnknownHostException, UnsupportedEncodingException {
        final Request req = Request.write("fileName", "octet", null, 65535);
        final DatagramPacket packet = req.build();
        Assert.assertEquals(packet.getPort(), req.getPort());
        Assert.assertNull(req.getHost());
        final byte[] content = packet.getData();
        Assert.assertEquals(packet.getLength(), content.length);
        final ByteBuffer buff = ByteBuffer.wrap(content);
        Assert.assertEquals(2, buff.getShort());
        final byte[] dst = new byte[9];
        buff.get(dst, 0, dst.length);
        Assert.assertEquals("fileName\0", new String(dst, "US-ASCII"));
        final byte[] mode = new byte[6];
        buff.get(mode, 0, mode.length);
        Assert.assertEquals("octet\0", new String(mode, "US-ASCII"));
        Assert.assertEquals(0, buff.remaining());
    }

    @Test
    public void testWriteWithOptions() throws UnknownHostException, UnsupportedEncodingException {
        final Request req = Request.write("fileName", "octet", InetAddress.getByName("localhost"), 65535,
                Option.blksize(1024), Option.timeout(10));
        final DatagramPacket packet = req.build();
        Assert.assertEquals(packet.getPort(), req.getPort());
        Assert.assertEquals(packet.getAddress(), req.getHost());
        final byte[] content = packet.getData();
        Assert.assertEquals(packet.getLength(), content.length);
        final ByteBuffer buff = ByteBuffer.wrap(content);
        Assert.assertEquals(2, buff.getShort());
        final byte[] dst = new byte[9];
        buff.get(dst, 0, dst.length);
        Assert.assertEquals("fileName\0", new String(dst, "US-ASCII"));
        final byte[] mode = new byte[6];
        buff.get(mode, 0, mode.length);
        Assert.assertEquals("octet\0", new String(mode, "US-ASCII"));
        final Option blksize = Option.decode(buff);
        final Option timeout = Option.decode(buff);
        Assert.assertNotNull(blksize);
        Assert.assertEquals("blksize", blksize.getLabel());
        Assert.assertEquals(1024, blksize.getValue());
        Assert.assertNotNull(timeout);
        Assert.assertEquals("timeout", timeout.getLabel());
        Assert.assertEquals(10, timeout.getValue());
        Assert.assertEquals(0, buff.remaining());
    }

}
