package players.local;

import game.*;
import geometry.GeometryMove;
import geometry.Vec2;
import players.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BotPlayer extends Player {
    public BotPlayer(GameAdapter game, Side side) {
        super(game, side);
    }

    public boolean process() {
        GameCellStates desireState =  switch (side){
            case FOX_TURN -> GameCellStates.FOX;
            case GOOSE_TURN -> GameCellStates.GOOSE;
        };

        List<Vec2> sideFiguresPoses = new ArrayList<>();
        for (int y = 0 ; y < game.getBoardSize(); y++) {
            for (int x = 0; x < game.getBoardSize(); x++) {
                Vec2 pos = new Vec2(x, y);
                if(game.getCellState(pos) == desireState) sideFiguresPoses.add(pos);
            }
        }
        Collections.shuffle(sideFiguresPoses);

        for (Vec2 figurePos : sideFiguresPoses) {

            List<GeometryMove> moves = game.getPossibleMoves(figurePos);
            if (moves.size() == 0) continue;

            Collections.shuffle(moves);
            for (GeometryMove move : moves) {
                boolean resMove = game.move(move.start(), move.end());
                if (resMove) {
                    return true;
                }
            }
        }
        return false;
    }
}
