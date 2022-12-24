package online;

import game.Game;
import game.Side;
import geometry.Vec2;
import players.local.BotPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Room {
    private BufferedReader foxIn = null;
    private PrintWriter foxOut = null;

    private BufferedReader gooseIn = null;
    private PrintWriter gooseOut = null;

    private final Game game;

    private BotPlayer botPlayer = null;

    private final boolean debug = false;

    private boolean killed = false;

    long last_smt;

    public Room(Side side, boolean initWithBot) {
        game = new Game();
        last_smt = System.currentTimeMillis();

        if(initWithBot) {
            switch (side) {
                case FOX_TURN -> botPlayer = new BotPlayer(game, Side.GOOSE_TURN);
                case GOOSE_TURN -> botPlayer = new BotPlayer(game, Side.FOX_TURN);
            }
        }


    }

    void resetTime(){
        last_smt = System.currentTimeMillis();
    }

    void setFox(BufferedReader in, PrintWriter out){
        foxIn = in;
        foxOut = out;
        resetTime();
        Thread t = new Thread(() -> {
            try {
                process(Side.FOX_TURN);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
    }

    void setGoose(BufferedReader in, PrintWriter out){
        gooseIn = in;
        gooseOut = out;
        Thread t = new Thread(() -> {
            try {
                process(Side.GOOSE_TURN);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
    }

    Side getFreeSide(){
        if(botPlayer != null) return null;
        if(gooseOut == null) return Side.GOOSE_TURN;
        if(foxOut == null) return Side.FOX_TURN;
        return null;
    }

    public boolean roomAlive(long timeout){
        return System.currentTimeMillis() - last_smt < timeout;
    }

    public void process(Side side) throws IOException {
        while (!killed) {

            if(botPlayer != null) botPlayer.process();
            String word = read(side);
            switch (word.toCharArray()[0]) {
                case 'b' -> send(side, "b" + game.getBoardSize());

                case 'n' -> send(side, "n" + ConvertUtils.toSide(side));  // whichMoveNow

                case 'w' -> send(side, "w" + ConvertUtils.toSide(game.getWinner()));

                case 'a' -> {
                    Vec2 pos =  ConvertUtils.parseVec(word.substring(1));
                    List<Vec2> list = game.getMovesToAllCloseNeighbours(pos);
                    send(side, "a" + ConvertUtils.toList(list));
                }
                case 's' -> {
                    Vec2 pos =  ConvertUtils.parseVec(word.substring(1));
                    send(side, "s" + ConvertUtils.toState(game.getCellState(pos)));
                }
                case 'p' -> {
                    Vec2 pos =  ConvertUtils.parseVec(word.substring(1));
                    send(side, "p" + ConvertUtils.toGeometryMoves(game.getPossibleMoves(pos)));
                }
                case 'm' -> {
                    String[] in = word.substring(1).split(":");
                    Vec2 start = ConvertUtils.parseVec(in[0]);
                    Vec2 end = ConvertUtils.parseVec(in[1]);
                    send(side, "m" + ConvertUtils.toBoolean(game.move(start, end)));
                }
            }
        }
    }

    private void send(Side side, String msg){
        if(debug) System.out.println("s rsp:" + msg);

        PrintWriter writer = switch (side) {
            case FOX_TURN -> foxOut;
            case GOOSE_TURN -> gooseOut;
        };

        writer.println(msg);
    }

    private String read(Side side) throws IOException {
        BufferedReader reader = switch (side) {
            case FOX_TURN -> foxIn;
            case GOOSE_TURN -> gooseIn;
        };
        String line = reader.readLine();

        resetTime();

        if(debug) System.out.println("s req:" + line);

        return line;
    }

    public void kill() throws IOException {
        System.out.println("killed");
        killed = true;
        gooseIn.close();
        gooseOut.close();

        foxIn.close();
        foxOut.close();
    }
}
