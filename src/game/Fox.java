package game;

import geometry.Vec2;

import java.util.ArrayList;
import java.util.List;

class Fox extends Figure {
    public Fox(Cell cell) {
        super(cell);
    }

    @Override
    public List<Move> getPossibleMoves() {
        List<Move> possibleMoves = new ArrayList<>();
        for (Vec2 deltaPos : Cell.getMovesToAllCloseNeighbours()) {
            Cell testCell = cell.getCloseNeighbour(deltaPos);
            if(testCell == null ) continue;
            if(testCell.getCellState() == GameCellStates.FREE) {
                List<Vec2> moves = new ArrayList<>();
                moves.add(deltaPos);
                Move move = new Move(cell, testCell, moves);
                possibleMoves.add(move);
            }
            possibleMoves.addAll(getAllKillMoves(new Move(cell, cell, new ArrayList<>()), deltaPos));
        }
        return possibleMoves;
    }

    private List<Move> getAllKillMoves(Move moveForNow, Vec2 deltaPos){
        List<Move> possibleMoves = new ArrayList<>();

        Cell nextCell = moveForNow.endCell.getCloseNeighbour(deltaPos);
        if(nextCell == null || nextCell.getCellState() != GameCellStates.GOOSE) return possibleMoves;

        Cell nextNextCell = nextCell.getCloseNeighbour(deltaPos);
        if(nextNextCell == null || nextNextCell.getCellState() != GameCellStates.FREE) return possibleMoves;

        List<Vec2> moves = moveForNow.getCloseMoves();
        moves.add(deltaPos);
        moves.add(deltaPos);
        Move moveForNow1 = new Move(moveForNow.startCell, nextNextCell, moves);
        possibleMoves.add(moveForNow1);
        possibleMoves.addAll(getAllKillMoves(moveForNow1, deltaPos));

        return possibleMoves;
    }

    //
    @Override
    protected void killEnemy(Move move) {
        for (Cell cell: move.getAllCellsOnMove()) {
            if(cell.getCellState() == GameCellStates.GOOSE) {
                // kill goose
                cell.setFigure(null);
            }
        }
    }
}
