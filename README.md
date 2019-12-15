# Hedwig

Hedwig is a TFTP client for JAVA.

## Installation

## Transfer a file with Hedwig

The first thing to do to transfer a file is to open a `DatagramSocket`.

```
    try (DatagramSocket socket = new MulticastSocket()){
       [...]
    }
```
Then create a `com.code.fauch.hedwig.TFTP` instance with the socket.

```
    new TFTP(socket)
```
You are now ready to upload a file using the `put` method or download a file using the `get` method.
 
### Put a file with Hedwig

Open an input stream on the file to upload then call the method `TFTP.put()` with the following arguments:

* the remote host
* the remote port
* the input stream
* the expected remote file name
* some options

Here is an example to transfer a source file on the local host to a destination file on the remote host.

```
    public static void main(String[] args) throws IOException, TFTPException {
        final Path file = Paths.get(args[0]);
        System.out.println("Sending " + file + " ...");
        try (DatagramSocket socket = new MulticastSocket()){
            socket.setSoTimeout(10);
            try(InputStream input = Files.newInputStream(file)) {
                new TFTP(socket).put(
                    InetAddress.getLocalHost(), 
                    69, 
                    input, 
                    "file.txt", 
                    "octet", 
                    Option.blksize(8), 
                    Option.timeout(10)
                );
            }
        }
    }
```
## Get a file with Hedwig

Open an output stream on the file to write then call the method `TFTP.get()` with the following arguments:

* the remote host
* the remote port
* the output stream
* the name of the remote file to download
* some options

Here is an example to transfer a destination file on the remote host to a file on the local host.

```
    public static void main(String[] args) throws IOException, TFTPException {
        final Path file = Paths.get(args[1]);
        try (DatagramSocket socket = new MulticastSocket()){
            socket.setSoTimeout(10);
            System.out.println("Reading " + file + " ...");
            try(OutputStream output = Files.newOutputStream(file)) {
                new TFTP(socket).get(
                    InetAddress.getLocalHost(), 
                    69, 
                    output, 
                    "file.txt", 
                    "octet", 
                    Option.blksize(8), 
                    Option.timeout(10)
                );
            }
        }
    }
```