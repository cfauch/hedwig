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

import org.junit.Assert;
import org.junit.Test;


/**
 * TU for Options
 * 
 * @author c.fauch
 *
 */
public class OptionTest {

    @Test
    public void testEncodeBlksize() throws UnsupportedEncodingException {
        final byte[] enc = Option.blksize(1024).encode();
        final ByteBuffer buff = ByteBuffer.wrap(enc);
        final byte[] lbl = new byte[8];
        buff.get(lbl, 0, lbl.length);
        Assert.assertEquals("blksize\0", new String(lbl, "US-ASCII"));
        final byte[] val = new byte[5];
        buff.get(val, 0, val.length);
        Assert.assertEquals("1024\0", new String(val, "US-ASCII"));
    }

    @Test
    public void testEncodeTimeout() throws UnsupportedEncodingException {
        final byte[] enc = Option.timeout(10).encode();
        final ByteBuffer buff = ByteBuffer.wrap(enc);
        final byte[] lbl = new byte[8];
        buff.get(lbl, 0, lbl.length);
        Assert.assertEquals("timeout\0", new String(lbl, "US-ASCII"));
        final byte[] val = new byte[3];
        buff.get(val, 0, val.length);
        Assert.assertEquals("10\0", new String(val, "US-ASCII"));
    }

    @Test
    public void testEncodeTSize() throws UnsupportedEncodingException {
        final byte[] enc = Option.tsize(673312).encode();
        final ByteBuffer buff = ByteBuffer.wrap(enc);
        final byte[] lbl = new byte[6];
        buff.get(lbl, 0, lbl.length);
        Assert.assertEquals("tsize\0", new String(lbl, "US-ASCII"));
        final byte[] val = new byte[7];
        buff.get(val, 0, val.length);
        Assert.assertEquals("673312\0", new String(val, "US-ASCII"));
    }

    @Test
    public void testDecode() throws UnsupportedEncodingException {
        final Option opt = Option.blksize(1024);
        final byte[] content = opt.encode();
        final Option dec = Option.decode(ByteBuffer.wrap(content));
        Assert.assertNotNull(dec);
        Assert.assertEquals(opt.getLabel(), dec.getLabel());
        Assert.assertEquals(opt.getValue(), dec.getValue());
    }

    @Test(expected=NullPointerException.class)
    public void testDecodeMissingBuffer() throws UnsupportedEncodingException {
        final Option dec = Option.decode(null);
        Assert.assertNotNull(dec);
    }

    @Test
    public void testDecodeEmptyBuffer() throws UnsupportedEncodingException {
        final Option dec = Option.decode(ByteBuffer.allocate(0));
        Assert.assertNull(dec);
    }

    @Test
    public void testDecodeIncompleteOption() throws UnsupportedEncodingException {
        final Option opt = Option.blksize(1024);
        final ByteBuffer buff = ByteBuffer.wrap(opt.encode());
        buff.position(9);
        final Option dec = Option.decode(buff);
        Assert.assertNull(dec);
    }

}
