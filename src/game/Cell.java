package game;

import geometry.Vec2;

import java.util.ArrayList;
import java.util.List;

class Cell {
    private Figure figure = null;
    private final Vec2 pos;

    private final Cell[] neighbours = new Cell[9];

    public Cell(Vec2 pos) {
        this.pos = pos;
    }

    public Vec2 getPos(){
        return pos;
    }

    void setFigure(Figure figure){
        this.figure = figure;
    }

    public Figure getFigure(){
        return figure;
    }

    private int getNumOfNeighbour(Vec2 move){
        // 0 1 2  y = -1
        // 3 4 5  y = 0
        // 6 7 8  y = 0
        if(Math.abs(move.x) > 1 || Math.abs(move.y) > 1) {
            throw new IllegalArgumentException("cant get num of neighbours");
        }

        return (move.y + 1) * 3 + move.x + 1;
    }

    public Cell getCloseNeighbour(Vec2 move){
        return neighbours[getNumOfNeighbour(move)];

    }

    void setCloseNeighbour(Vec2 move, Cell cell){
        if(move.x == 0 && move.y == 0) throw new IllegalArgumentException("cant set this cell");
        neighbours[getNumOfNeighbour(move)] = cell;
    }

    public static List<Vec2> getMovesToAllCloseNeighbours(){
        List<Vec2> moves = new ArrayList<>();
        int[] deltas = new int[]{-1, 0, 1};

        for(int deltaX: deltas) {
            for (int deltaY : deltas) {
                if (deltaX == 0 && deltaY == 0) continue;
                moves.add(new Vec2(deltaX, deltaY));
            }
        }
        return moves;
    }

    // TODO: remove from here
    public GameCellStates getCellState(){
        if(figure == null) return GameCellStates.FREE;
        else if(figure instanceof Fox) return GameCellStates.FOX;
        else if(figure instanceof Goose) return GameCellStates.GOOSE;
        else {
            throw new IllegalStateException();
        }
    }

}
