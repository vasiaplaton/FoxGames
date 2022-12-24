package online;

import game.GameAdapter;
import game.GameCellStates;
import game.Side;
import geometry.GeometryMove;
import geometry.Vec2;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class OnlineGame implements GameAdapter {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    /*
    }

    public String sendMessage(String msg) {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    public void stopConnection() {
        in.close();
        out.close();
        clientSocket.close();
    }
     */
    public OnlineGame(String ip, int port) throws IOException {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            throw new IOException("cant connect to server");
        }
    }

    public boolean connectToExistingRoom(int roomId){
        String resp = sendMessage("c" + roomId);
        return parseBoolean(resp.toCharArray()[1]);
    }

    public Integer createNewRoom(Side side){
        char c;
        switch (side) {
            case FOX_TURN -> c = 'F';
            case GOOSE_TURN -> c = 'G';
            default -> throw new IllegalArgumentException();
        }
        String resp = sendMessage("h" + c);
        return Integer.parseInt(resp.substring(1));
    }

    private String sendMessage(String msg) {
        try {
            out.println(msg);
            return in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * sends b\n,
     * wait to get b + X + \n, where X is number
     * @return board size
     */
    @Override
    public int getBoardSize() {
        String resp = sendMessage("b");
        if(resp.toCharArray()[0] != 'b') {
            throw new RuntimeException();
        }
        return Integer.parseInt(resp.substring(1));
    }

    /**
     * sends w\n,
     * wait to get w + X + \n, where X is F/G
     * @return which move now
     */
    @Override
    public Side whichMoveNow() {
        String resp = sendMessage("n");
        if(resp.toCharArray()[0] != 'n') {
            throw new RuntimeException();
        }
        return parseSide(resp.toCharArray()[1]);
    }

    /**
     * sends w\n
     * wait to get w + X + \n, where X is F/G/N
     * @return who is winner or null if game is going on
     */
    @Override
    public Side getWinner() {
        String resp = sendMessage("w");
        if(resp.toCharArray()[0] != 'w') {
            throw new RuntimeException();
        }
        return parseSide(resp.toCharArray()[1]);
    }

    /**
     * sends nX,Y\n
     * wait to get n + list + \n, where list
     * @return all possible moves from given cell to his close neighbourhoods
     */
    @Override
    public List<Vec2> getMovesToAllCloseNeighbours(Vec2 cellPos) {
        String resp = sendMessage("n" + cellPos.x + "," + cellPos.y ); // n0,0
        // return list
        return null;
    }

    /**
     * sends cX,Y\n
     * wait to get c + X + \n, where X is F/G/O/N
     * @return state of cell that on given pos
     */
    @Override
    public GameCellStates getCellState(Vec2 pos) {
        String resp = sendMessage("s" + pos.x + "," + pos.y);
        if(resp.toCharArray()[0] != 's') {
            throw new RuntimeException();
        }
        return switch (resp.toCharArray()[1]) {
            case 'F' -> GameCellStates.FOX;
            case 'G' -> GameCellStates.GOOSE;
            case 'O' -> GameCellStates.FREE;
            case 'N' -> null;
            default -> throw new IllegalStateException("Unexpected value: " + resp.toCharArray()[1]);
        };
    }

    /**
     * sends mX,Y,A,B\n
     * wait to get m + X + \n, where X is T/F
     * @return result move
     */
    @Override
    public boolean move(Vec2 startPos, Vec2 endPos) {
        String resp = sendMessage("m" + startPos.x + "," + startPos.y + ":" + endPos.x + "," + endPos.y); // m0,0:2,1
        if(resp.toCharArray()[0] != 'm') {
            throw new RuntimeException();
        }
        return parseBoolean(resp.toCharArray()[1]);

    }

    private Boolean parseBoolean(char c){
        return switch (c) {
            case 'T' -> true;
            case 'F' -> false;
            default -> throw new IllegalStateException("Unexpected value: " + c);
        };
    }

    private Side parseSide(char c){
        return switch (c) {
            case 'F' -> Side.FOX_TURN;
            case 'G' -> Side.GOOSE_TURN;
            case 'N' -> null;
            default -> throw new IllegalStateException("Unexpected value: " + c);
        };
    }

    /**
     * sends pX,Y\n
     * wait to get p + list + \n, where list
     * @return all possible figure moves figure on given cell
     */
    @Override
    public List<GeometryMove> getPossibleMoves(Vec2 pos) {
        String resp = sendMessage("p" + pos.x + "," + pos.y ); // m0,0:2,1
        // return list
        return null;
    }
}
