package online;

import game.Side;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private final ServerSocket serverSocket;

    private final List<Room> rooms = new ArrayList<>();

    boolean lock = false;


    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void start() throws IOException {
        System.out.println("Game server started on port: "+serverSocket.getLocalPort());


        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            lock = true;
            for (int i = rooms.size() - 1; i >= 0; i--) {
                Room room = rooms.get(i);
                rooms.remove(i);
                if(!room.roomAlive(1000)) {
                    try {
                        room.kill();
                    } catch (IOException e) {
                        System.out.println("can't close room");
                    }

                }
            }
            lock = false;
        }, 0, 100, TimeUnit.MILLISECONDS);


        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected from: " + clientSocket.getInetAddress());

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String req = in.readLine();
            if(req.toCharArray()[0] == 'h') {
                Side side = Room.parseSide(req.toCharArray()[1]);

                // new room
                System.out.println("creating new room");
                out.println("h" + rooms.size());
                Room room = new Room(side);
                switch (side){
                    case FOX_TURN -> room.setFox(in, out);
                    case GOOSE_TURN -> room.setGoose(in, out);
                }
                while (lock);
                rooms.add(room);

                Thread t = new Thread(room);
                t.start();
                System.out.println("created");
            } else {
                System.out.println("illegal connect params");
            }

        }
    }
}
