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
/**
 * <p>
 * The main API for TFTP transfer.
 * </p>
 * <h3>Put a file</h3>
 * <p>
 * To put a file, open a socket, open an input stream on the file to transfer, and then start the transfer by instantiating
 * a <code>TFTP</code> object with the socket and calling the <code>put</code> method on it with following arguments:
 * <ul>
 * <li>the destination host</li>
 * <li>the destination port</li>
 * <li>the intput stream</li>
 * <li>the expected name of the remote file</li>
 * <li>the sending mode: "netascii", "octet", "mail"</li>
 * <li>some options</li>
 * </ul>
 * </p>
 * <pre>
 *      try (DatagramSocket socket = new MulticastSocket()){
 *          try(InputStream input = Files.newInputStream(file)) {
 *              new TFTP(socket).put(InetAddress.getLocalHost(), 69, input, "file.txt", "octet", Option.blksize(8), Option.timeout(10));
 *          }
 *      }
 * </pre>
 * <h3>Get a file</h3>
 * <p>
 * To get a file, open a socket, open an output stream on the file to write, and then start the transfer by instantiating
 * a <code>TFTP</code> object with the socket and calling the <code>get</code> method on it with following arguments:
 * <ul>
 * <li>the destination host</li>
 * <li>the destination port</li>
 * <li>the output stream</li>
 * <li>the name of the remote file</li>
 * <li>the sending mode: "netascii", "octet", "mail"</li>
 * <li>some options</li>
 * </ul>
 * </p>
 * <pre>
 *      try (DatagramSocket socket = new MulticastSocket()){
 *          try(OutputStream output = Files.newOutputStream(file)) {
 *              new TFTP(socket).get(InetAddress.getLocalHost(), 69, output, "file.txt", "octet", Option.blksize(8), Option.timeout(10));
 *          }
 *      }
 * </pre> 
 * 
 * @author c.fauch
 *
 */
package com.code.fauch.hedwig;
