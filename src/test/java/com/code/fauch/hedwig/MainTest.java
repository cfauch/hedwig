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
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main example to put a file.
 * 
 * @author c.fauch
 *
 */
public final class MainTest {

    /**
     * @param args
     * @throws IOException 
     * @throws TFTPException 
     */
    public static void main(String[] args) throws IOException, TFTPException {
        final Path file = Paths.get(args[0]);
        System.out.println("Sending " + file + " ...");
        try (DatagramSocket socket = new MulticastSocket()){
            socket.setSoTimeout(10);
            try(InputStream input = Files.newInputStream(file)) {
                new TFTP(socket).write(InetAddress.getLocalHost(), 69, input, "file.txt", "octet", Option.blksize(8), Option.timeout(10));
            }
        }
    }

}
