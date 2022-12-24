package game;

import geometry.Vec2;

import java.util.ArrayList;
import java.util.List;

class Goose extends Figure {
    public Goose(Cell cell) {
        super(cell);
    }

    @Override
    public List<Move> getPossibleMoves() {
        List<Move> possibleMoves = new ArrayList<>();
        for (Vec2 deltaPos : Cell.getMovesToAllCloseNeighbours()) {
            // goose only go to up of board
            if (deltaPos.y > 0) continue;

            Cell testCell = cell.getCloseNeighbour(deltaPos);

            if (testCell != null && testCell.getCellState() == GameCellStates.FREE) {
                List<Vec2> moves = new ArrayList<>();
                moves.add(deltaPos);
                possibleMoves.add(new Move(cell, testCell, moves));
            }
        }
        return possibleMoves;
    }
}
