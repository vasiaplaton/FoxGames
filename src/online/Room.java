package online;

import game.Game;
import game.Side;
import geometry.Vec2;
import players.local.BotPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Room implements Runnable {
    private BufferedReader foxIn = null;
    private PrintWriter foxOut = null;

    private BufferedReader gooseIn = null;
    private PrintWriter gooseOut = null;

    private Side onlineSide;

    private final Game game;

    private BotPlayer player;

    private final boolean debug = false;

    private boolean killed = false;

    long last_smt;

    public Room(Side side) {
        game = new Game();
        onlineSide = side;
        last_smt = System.currentTimeMillis();

        switch (onlineSide) {
            case FOX_TURN -> player = new BotPlayer(game, Side.GOOSE_TURN);
            case GOOSE_TURN -> player = new BotPlayer(game, Side.FOX_TURN);
        }

    }

    void resetTime(){
        last_smt = System.currentTimeMillis();
    }

    void setFox(BufferedReader in, PrintWriter out){
        foxIn = in;
        foxOut = out;
        resetTime();
    }

    void setGoose(BufferedReader in, PrintWriter out){
        gooseIn = in;
        gooseOut = out;
        resetTime();
    }

    public boolean roomAlive(long timeout){
        return System.currentTimeMillis() - last_smt < timeout;
    }

    public void process() throws IOException {

        while (!killed) {
            player.process();
            String word = read(onlineSide);
            switch (word.toCharArray()[0]) {
                case 'b' -> {
                    // getBoardSize
                    send(onlineSide, "b" + game.getBoardSize());
                }
                case 'n' -> {
                    // whichMoveNow
                    send(onlineSide, "n" + ConvertUtils.toSide(game.whichMoveNow()));
                }

                case 'w' -> {
                    // getWinner
                    send(onlineSide, "w" + ConvertUtils.toSide(game.getWinner()));
                }
                case 'a' -> {
                    Vec2 pos =  ConvertUtils.parseVec(word.substring(1));
                    List<Vec2> list = game.getMovesToAllCloseNeighbours(pos);
                    send(onlineSide, "a" + ConvertUtils.toList(list));
                }
                case 's' -> {
                    Vec2 pos =  ConvertUtils.parseVec(word.substring(1));
                    // getCellState
                    send(onlineSide, "s" + ConvertUtils.toState(game.getCellState(pos)));
                }
                case 'p' -> {
                    Vec2 pos =  ConvertUtils.parseVec(word.substring(1));
                    send(onlineSide, "a" + ConvertUtils.toGeometryMoves(game.getPossibleMoves(pos)));
                }
                case 'm' -> {
                    String[] in = word.substring(1).split(":");
                    Vec2 start = ConvertUtils.parseVec(in[0]);
                    Vec2 end = ConvertUtils.parseVec(in[1]);
                    send(onlineSide, "m" + ConvertUtils.toBoolean(game.move(start, end)));
                }
            }
            player.process();
        }
    }

    private void send(Side side, String msg){
        if(debug) System.out.println("rsp:" + msg);

        switch (side) {
            case FOX_TURN -> foxOut.println(msg);
            case GOOSE_TURN -> gooseOut.println(msg);
        }
    }

    private String read(Side side) throws IOException {
        String line =  switch (side) {
            case FOX_TURN -> foxIn.readLine();
            case GOOSE_TURN -> gooseIn.readLine();
        };

        resetTime();

        if(debug) System.out.println("req:" + line);

        return line;
    }

    @Override
    public void run() {
        try {
            process();
        } catch (IOException e) {
            System.out.println("can't communicate with client");
        }
    }


    public static Side parseSide(char c){
        return switch (c) {
            case 'F' -> Side.FOX_TURN;
            case 'G' -> Side.GOOSE_TURN;
            case 'N' -> null;
            default -> throw new IllegalStateException("Unexpected value: " + c);
        };
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
