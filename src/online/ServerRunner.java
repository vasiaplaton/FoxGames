package online;

import java.io.*;

public class ServerRunner {
    public static void main(String[] args) throws IOException {
        Server server = new Server(4004);
        server.start();
    }
}
