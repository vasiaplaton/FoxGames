package game;

import java.util.List;

abstract class Figure {
    protected Cell cell;

    public Figure(Cell cell) {
        this.cell = cell;
        cell.setFigure(this);
    }

    public abstract List<Move> getPossibleMoves();

    protected Move getMove(Cell endCell) {
        for (Move move : getPossibleMoves()) {
            if(move.startCell.getPos().equals(cell.getPos()) &&
                    move.endCell.getPos().equals(endCell.getPos())){
                return move;
            }
        }
        return null;
    }


    boolean move(Cell endCell) {
        Move move = getMove(endCell);
        if (move == null) return false;
        killEnemy(move);
        cell.setFigure(null);
        cell = move.endCell;
        cell.setFigure(this);
        return true;
    }

    protected void killEnemy(Move move){}

}
