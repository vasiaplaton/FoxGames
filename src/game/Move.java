package game;

import geometry.Vec2;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
class Move {
    public final Cell startCell;
    public final Cell endCell;
    private final List<Vec2> closeMoves;

    public Move(Cell startCell, Cell endCell, List<Vec2> closeMoves) {
        this.startCell = startCell;
        this.endCell = endCell;
        this.closeMoves = closeMoves;
        for (Vec2 move : closeMoves) {
            if (Math.abs(move.x) > 1 || Math.abs(move.y) > 1) throw new IllegalArgumentException();
        }
    }
    public List<Vec2> getCloseMoves(){
        return new ArrayList<>(closeMoves);
    }

    public List<Cell> getAllCellsOnMove(){
        List<Cell> poses = new ArrayList<>(closeMoves.size());
        Cell cell = startCell;
        for (Vec2 move: closeMoves) {
            poses.add(cell);
            cell = cell.getCloseNeighbour(move);
        }
        return poses;
    }
}
