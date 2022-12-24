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
    private final PrintWriter out;
    private final BufferedReader in;

    private final boolean debug = false;
    public OnlineGame(String ip, int port) throws IOException {
        try {
            Socket clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            throw new IOException("cant connect to server");
        }
    }

    public void assertFirstTo(String in, char c){
        if(in.toCharArray()[0] != c) {
            throw new RuntimeException();
        }
    }

    /**
     * sends c + roomId + "\n"
     * @param roomId room to connect
     * @return connected or not to given server
     */
    public boolean connectToExistingRoom(int roomId){
        String resp = sendMessage("c" + roomId);
        assertFirstTo(resp, 'c');
        return ConvertUtils.parseBoolean(resp.toCharArray()[1]);
    }

    /**
     * sends h + G/G + "\n"
     * force server to create new room
     * wait for h + roomId
     * @param side which player start on
     * @return room id
     */
    public Integer createNewRoom(Side side){
        char c = ConvertUtils.toSide(side);
        String resp = sendMessage("h" + c);
        assertFirstTo(resp, 'h');
        return Integer.parseInt(resp.substring(1));
    }

    private String sendMessage(String msg) {
        if(debug) System.out.println("c req:" + msg);
        try {
            out.println(msg);
            String input = in.readLine();
            if(debug) System.out.println("c rsp:" + input);
            return input;
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
        assertFirstTo(resp, 'b');
        return Integer.parseInt(resp.substring(1));
    }

    /**
     * sends n\n,
     * wait to get n + X + \n, where X is F/G
     * @return which move now
     */
    @Override
    public Side whichMoveNow() {
        String resp = sendMessage("n");
        assertFirstTo(resp, 'n');
        return ConvertUtils.parseSide(resp.toCharArray()[1]);
    }

    /**
     * sends w\n
     * wait to get w + X + \n, where X is F/G/N
     * @return who is winner or null if game is going on
     */
    @Override
    public Side getWinner() {
        String resp = sendMessage("w");
        assertFirstTo(resp, 'w');
        return ConvertUtils.parseSide(resp.toCharArray()[1]);
    }

    // TODO list
    /**
     * sends aX,Y\n
     * wait to get a + list + \n, where list
     * @return all possible moves from given cell to his close neighbourhoods
     */
    @Override
    public List<Vec2> getMovesToAllCloseNeighbours(Vec2 cellPos) {
        String resp = sendMessage("a" + cellPos.x + "," + cellPos.y ); // n0,0
        assertFirstTo(resp, 'a');
        return ConvertUtils.parseList(resp.substring(1));
    }

    /**
     * sends sX,Y\n
     * wait to get s + X + \n, where X is F/G/O/N
     * @return state of cell that on given pos
     */
    @Override
    public GameCellStates getCellState(Vec2 pos) {
        String resp = sendMessage("s" + ConvertUtils.toVec(pos));
        assertFirstTo(resp, 's');
        return ConvertUtils.parseState(resp.toCharArray()[1]);
    }

    /**
     * sends mX,Y:A,B\n
     * wait to get m + X + \n, where X is T/F
     * @return result move
     */
    @Override
    public boolean move(Vec2 startPos, Vec2 endPos) {
        String resp = sendMessage("m" + ConvertUtils.toVec(startPos) + ":" + ConvertUtils.toVec(endPos)); // m0,0:2,1
        assertFirstTo(resp, 'm');
        return ConvertUtils.parseBoolean(resp.toCharArray()[1]);

    }

    // TODO list
    /**
     * sends pX,Y\n
     * wait to get p + list + \n, where list
     * @return all possible figure moves figure on given cell
     */
    @Override
    public List<GeometryMove> getPossibleMoves(Vec2 pos) {
        String resp = sendMessage("p" + ConvertUtils.toVec(pos));
        assertFirstTo(resp, 'p');
        return ConvertUtils.parseGeometryMoves(resp.substring(1));
    }
}
