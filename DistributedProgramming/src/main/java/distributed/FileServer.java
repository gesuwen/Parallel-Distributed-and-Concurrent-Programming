package edu.coursera.distributed;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A basic and very limited implementation of a file server that responds to GET
 * requests from HTTP clients.
 */
public final class FileServer {
    /**
     * Main entrypoint for the basic file server.
     *
     * @param socket Provided socket to accept connections on.
     * @param fs A proxy filesystem to serve files from. See the PCDPFilesystem
     *           class for more detailed documentation of its usage.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    public void run(final ServerSocket socket, final PCDPFilesystem fs)
            throws IOException {
        /*
         * Enter a spin loop for handling client requests to the provided
         * ServerSocket object.
         */
        while (true) {

            Socket connection = socket.accept();
            InputStream input = connection.getInputStream();
            OutputStream output = connection.getOutputStream();

            String filename = parseFileName(input);
            if (filename == null) {
                replyHttp404(output);
            } else {
                serveFilename(filename, fs, output);
            }

            output.flush();
            output.close();
            connection.close();
        }
    }

    private void serveFilename(String filename, final PCDPFilesystem fs, OutputStream output) {
        final String contents = fs.readFile(new PCDPPath(filename));
        if (null == contents) {
            replyHttp404(output);
        } else {
            final PrintStream printer = new PrintStream(output);

            printer.print("HTTP/1.0 200 OK\r\n");
            printer.print("Server: FileServer\r\n");
            printer.print("Content-Length: " + contents.length() + "\r\n");
            printer.print("\r\n");
            printer.print(contents);
            printer.print("\r\n");

            printer.flush();
            printer.close();
        }
    }

    private void replyHttp404(OutputStream output) {
        final PrintStream printer =  new PrintStream(output);

        printer.print("HTTP/1.0 404 Not Found\r\n");
        printer.print("Server: FileServer\r\n");
        printer.print("\r\n");

        printer.close();
    }

    private String parseFileName(InputStream input) {
        Scanner scanner = new Scanner(input).useDelimiter("\\r\\n");
        String line = scanner.next();

        Pattern pattern = Pattern.compile("GET (.+) HTTP/\\d.\\d");
        Matcher matcher = pattern.matcher(line);
        if (!matcher.find()) {
            return null;
        }

        return matcher.group(1);
    }
}