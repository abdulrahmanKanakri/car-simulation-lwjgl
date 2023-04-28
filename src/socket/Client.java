package socket;

import java.net.*;
import java.io.*;

public class Client implements Runnable {

    private static Client singleton;

    // initialize socket and input output streams
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;
    private final String address;
    private final int port;

    // constructor to put ip address and port
    private Client(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            System.out.println("Connected");

            // takes input from terminal
            input = new DataInputStream(System.in);

            // sends output to the socket
            out = new DataOutputStream(socket.getOutputStream());

        } catch (IOException u) {
            u.printStackTrace();
        }
    }

    public void sendMessage(String s) {
        try {
            out.writeUTF(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Client getInstance() {
        if (singleton == null) {
            singleton = new Client("127.0.0.1", 5000);
        }
        return singleton;
    }

    public void closeConnection () {
        // close the connection
        try {
            input.close();
            out.close();
            socket.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
}
