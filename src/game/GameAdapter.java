package game;

import geometry.GeometryMove;
import geometry.Vec2;

import java.util.List;

public interface GameAdapter {
    int getBoardSize();

    Side whichMoveNow();

    Side getWinner();

    List<Vec2> getMovesToAllCloseNeighbours(Vec2 cellPos);

    GameCellStates getCellState(Vec2 pos);

    boolean move(Vec2 startPos, Vec2 endPos);

    List<GeometryMove> getPossibleMoves(Vec2 pos);

}
