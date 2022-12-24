package game;

import geometry.GeometryMove;
import geometry.Vec2;

import java.util.*;

public class Game implements GameAdapter{
    private final int crossSize = 3;
    private final int boardSize = crossSize + (crossSize - 1) * 2;

    private final Cell[][] board = new Cell[boardSize][boardSize];

    private Side whichMoveNow = Side.FOX_TURN;

    private final Fox fox;

    public Game() {
        initBoard();

        fox = new Fox(board[3][3]);
        initGeese();
    }

    public int getBoardSize() {
        return boardSize;
    }

    private void initBoard() {
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                Vec2 pos = new Vec2(x, y);
                if (isCellOnboard(pos)) {
                    board[y][x] = new Cell(pos);
                } else {
                    board[y][x] = null;
                }
            }
        }
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                Vec2 pos = new Vec2(x, y);
                if (board[y][x] == null) continue;
                for (Vec2 move : Cell.getMovesToAllCloseNeighbours()) {
                    Cell state1 = getCell(new Vec2(pos.x + move.x, pos.y));
                    Cell state2 = getCell(new Vec2(pos.x, pos.y + move.y));
                    // if move is diagonal, for exam§e
                    // *   t
                    //   /
                    // s   *
                    // and we have one not cell on board then we can't go like this
                    if (state1 == null || state2 == null) continue;
                    board[y][x].setCloseNeighbour(move, getCell(pos.add(move)));
                }
            }
        }
    }

    Cell getCell(Vec2 pos) {
        if (pos.x < 0 || pos.x >= boardSize || pos.y < 0 || pos.y >= boardSize) return null;
        return board[pos.y][pos.x];
    }

    private boolean isCellOnboard(Vec2 pos) {
        if (pos.x < 0 || pos.x >= boardSize || pos.y < 0 || pos.y >= boardSize) {
            return false;
        }
        return pos.x >= crossSize - 1 && pos.x <= boardSize - crossSize ||
                pos.y >= crossSize - 1 && pos.y <= boardSize - crossSize;
    }

    private void initGeese() {
        for (int y = 0; y < boardSize - crossSize; y++) {
            Cell cell = getCell(new Vec2(0, y));
            if (cell != null) {
                new Goose(cell);
            }

            cell = getCell(new Vec2(boardSize - 1, y));
            if (cell != null) {
                new Goose(cell);
            }
        }
        for (int y = boardSize - crossSize; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                Cell cell = getCell(new Vec2(x, y));
                if (cell != null) {
                    new Goose(cell);
                }
            }
        }
    }


    boolean move(Cell startCell, Cell endCell) {
        if (getWinner() != null) return false;
        if(startCell.getCellState() == GameCellStates.FREE) return false;
        if (startCell.getCellState() == GameCellStates.FOX && whichMoveNow != Side.FOX_TURN) return false;
        if (startCell.getCellState() == GameCellStates.GOOSE && whichMoveNow != Side.GOOSE_TURN) return false;
        boolean result = startCell.getFigure().move(endCell);
        if (result) {
            if (whichMoveNow == Side.GOOSE_TURN) whichMoveNow = Side.FOX_TURN;
            else if (whichMoveNow == Side.FOX_TURN) whichMoveNow = Side.GOOSE_TURN;
        }
        return result;
    }

    public Side whichMoveNow() {
        return whichMoveNow;
    }

    public Side getWinner() {
        // if fox cant move
        if (fox.getPossibleMoves().size() == 0) return Side.GOOSE_TURN;
        if (countGeese() < 3) return Side.FOX_TURN;
        if (fox.cell.getPos().y == boardSize - 1) return Side.FOX_TURN;
        return null;
    }

    private int countGeese() {
        int count = 0;
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                if (board[y][x] != null && board[y][x].getCellState() == GameCellStates.GOOSE) count++;
            }
        }
        return count;
    }

    public List<Vec2> getMovesToAllCloseNeighbours(Vec2 cellPos){
        Cell cell = getCell(cellPos);

        if(cell == null) return new ArrayList<>();

        List<Vec2> list = new ArrayList<>();

        for (Vec2 move: Cell.getMovesToAllCloseNeighbours()) {
            if(cell.getCloseNeighbour(move) == null) continue;
            list.add(move);
        }

        return list;
    }

    public GameCellStates getCellState(Vec2 pos) {
        Cell cell = getCell(pos);
        if(cell == null) return null;

        return cell.getCellState();
    }

    public boolean move(Vec2 startPos, Vec2 endPos){
        Cell start = getCell(startPos);
        Cell end = getCell(endPos);
        if(start == null || end == null) return false;
        return move(start, end);
    }

    @Override
    public List<GeometryMove> getPossibleMoves(Vec2 pos) {
        Cell cell = getCell(pos);
        if(cell == null) return new ArrayList<>();

        Figure figure = cell.getFigure();
        if(figure == null) return new ArrayList<>();

        List<Move> possibleMoves = cell.getFigure().getPossibleMoves();

        return new ArrayList<>(possibleMoves.stream().map((move -> new GeometryMove(move.startCell.getPos(), move.endCell.getPos()
                , move.getCloseMoves()))).toList());
    }
}
