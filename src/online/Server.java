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

    volatile boolean lock = false;


    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void start(boolean with_bots) throws IOException {
        System.out.println("Game server started on port: "+serverSocket.getLocalPort());


        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            while (lock) Thread.onSpinWait();
            lock = true;
            for (int i = rooms.size() - 1; i >= 0; i--) {
                Room room = rooms.get(i);
                if(!room.roomAlive(60000)) {
                    System.out.println("kill room:" + i);
                    rooms.remove(i);
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
                Side side = ConvertUtils.parseSide(req.toCharArray()[1]);
                // new room
                System.out.println("creating new room");
                out.println("h" + rooms.size());
                Room room = new Room(side, with_bots);
                switch (side){
                    case FOX_TURN -> room.setFox(in, out);
                    case GOOSE_TURN -> room.setGoose(in, out);
                }

                while (lock) Thread.onSpinWait();
                lock = true;
                rooms.add(room);
                lock = false;

                System.out.println("created");
            } else if(req.toCharArray()[0] == 'c') {

                int roomId = Integer.parseInt(req.substring(1));
                System.out.println("connecting to exist room:" + roomId);

                if(roomId >= rooms.size()) {
                    System.out.println("illegal room num");
                    continue;
                }
                while (lock) Thread.onSpinWait();
                lock = true;
                Room room = rooms.get(roomId);
                lock = false;

                boolean res;
                if(room == null || room.getFreeSide() == null) {
                    System.out.println("cant connect");
                    if(room == null){
                        System.out.println("room is null");
                    }
                    else if(room.getFreeSide() == null) {
                        System.out.println("room is filled");
                    }
                    res = false;
                }
                else {
                    System.out.println("connect, free side:" + room.getFreeSide());
                    switch (room.getFreeSide()) {
                        case FOX_TURN ->  room.setFox(in, out);
                        case GOOSE_TURN -> room.setGoose(in, out);
                    }
                    res = true;
                }
                out.println("c" + ConvertUtils.toBoolean(res));
            }
            else {
                System.out.println("illegal connect params");
            }

        }
    }
}
