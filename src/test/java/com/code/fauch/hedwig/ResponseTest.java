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
 * TU for response
 * 
 * @author c.fauch
 *
 */
public class ResponseTest {

    @Test
    public void testEncodeError() throws UnsupportedEncodingException, UnknownHostException {
        final Response resp = Response.error(
                EError.OPTION_NEGOTIATION_FAILED, 
                "error message", 
                InetAddress.getByName("localhost"), 
                65535
        );
        final ByteBuffer buff = ByteBuffer.wrap(resp.encode());
        Assert.assertEquals(EOperation.ERROR.getCode(), buff.getShort());
        Assert.assertEquals(EError.OPTION_NEGOTIATION_FAILED.getCode(), buff.getShort());
        final byte[] dst = new byte[14];
        buff.get(dst, 0, dst.length);
        Assert.assertEquals("error message\0", new String(dst, "US-ASCII"));
        Assert.assertEquals(0, buff.remaining());
    }

    @Test(expected = NullPointerException.class)
    public void testEncodeMissingError() throws UnsupportedEncodingException, UnknownHostException {
        Response.error(
                null, 
                "error message", 
                InetAddress.getByName("localhost"), 
                65535
        );
    }

    @Test
    public void testEncodeData() throws UnsupportedEncodingException, UnknownHostException {
        final Response resp = Response.data(
                1, 
                "test".getBytes(),
                InetAddress.getByName("localhost"), 
                65535
        );
        final ByteBuffer buff = ByteBuffer.wrap(resp.encode());
        Assert.assertEquals(EOperation.DATA.getCode(), buff.getShort());
        Assert.assertEquals(1, buff.getShort());
        final byte[] dst = new byte[4];
        buff.get(dst, 0, dst.length);
        Assert.assertEquals("test", new String(dst));
        Assert.assertEquals(0, buff.remaining());
    }

    @Test(expected = NullPointerException.class)
    public void testEncodeNullData() throws UnsupportedEncodingException, UnknownHostException {
        Response.data(
                1, 
                null,
                InetAddress.getByName("localhost"), 
                65535
        );
    }

    @Test
    public void testDecodeData() throws UnknownHostException, UnsupportedEncodingException, TFTPException {
        final Response resp = Response.data(
                1, 
                "test".getBytes(),
                InetAddress.getByName("localhost"), 
                65535
        );
        final byte[] buff = resp.encode();
        Response dec = Response.from(new DatagramPacket(buff, buff.length));
        Assert.assertEquals(EOperation.DATA, dec.getOperation());
        Assert.assertEquals(1, dec.getBlock());
        Assert.assertEquals("test", new String(dec.getData()));
    }

    @Test(expected = TFTPException.class)
    public void testDecodeError() throws UnknownHostException, UnsupportedEncodingException, TFTPException {
        final Response resp = Response.error(
                EError.OPTION_NEGOTIATION_FAILED, 
                "error message", 
                InetAddress.getByName("localhost"), 
                65535
        );
        final byte[] buff = resp.encode();
        Response.from(new DatagramPacket(buff, buff.length));
    }

    @Test
    public void testDecodeAck() throws UnknownHostException, UnsupportedEncodingException, TFTPException {
        final Response resp = Response.ack(1, InetAddress.getByName("localhost"), 65535);
        final byte[] buff = resp.encode();
        final Response dec = Response.from(new DatagramPacket(buff, buff.length));
        Assert.assertEquals(EOperation.ACK, dec.getOperation());
        Assert.assertEquals(1, dec.getBlock());
        Assert.assertArrayEquals(new byte[0], dec.getData());
    }

    @Test
    public void testDecodeOAck() throws UnknownHostException, UnsupportedEncodingException, TFTPException {
        final Response resp = Response.oack(1, InetAddress.getByName("localhost"), 65535, Option.blksize(1024), Option.timeout(10));
        final byte[] buff = resp.encode();
        final Response dec = Response.from(new DatagramPacket(buff, buff.length));
        Assert.assertEquals(EOperation.OACK, dec.getOperation());
        Assert.assertEquals(0, dec.getBlock());
        Assert.assertArrayEquals(new byte[0], dec.getData());
        Assert.assertEquals(1024, dec.getBlksize().getValue());
        Assert.assertEquals(10, dec.getTimeout().getValue());
        Assert.assertNull(dec.getTSize());
    }

    @Test(expected=NullPointerException.class)
    public void testDecodeNull() throws UnknownHostException, UnsupportedEncodingException, TFTPException {
        Response.from(null);
    }

}
