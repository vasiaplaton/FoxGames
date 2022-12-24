package players;

import game.GameAdapter;
import game.Side;
import geometry.Vec2;

public class HumanPlayer extends Player {
    private Vec2 startCellPos;
    private Vec2 endCellPos;
    public HumanPlayer(GameAdapter game, Side side) {
        super(game, side);
    }

    @Override
    public boolean process() {
        if(game.whichMoveNow() != side) return false;
        if(startCellPos == null || endCellPos == null) return false;
        boolean resMove = game.move(startCellPos, endCellPos);
        startCellPos = null;
        endCellPos = null;
        return resMove;
    }

    public void setNextMove(Vec2 startCellPos, Vec2 endCellPos) {
        this.startCellPos = startCellPos;
        this.endCellPos = endCellPos;
    }
}
