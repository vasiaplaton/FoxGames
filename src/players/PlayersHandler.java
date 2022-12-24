package players;

import game.Side;
import geometry.Vec2;

public interface PlayersHandler {
    boolean playerProcess();
    void setNextMove(Side side, Vec2 startPos, Vec2 endPos);
}
