package players;

import game.*;
import game.Side;
import geometry.Vec2;

public class OnlinePlayerHandler implements PlayersHandler{
    final Side playerSide;
    private final GameAdapter game;
    final HumanPlayer player;
    public OnlinePlayerHandler(GameAdapter game, Side playerSide) {
        this.playerSide = playerSide;
        this.game = game;
        player = new HumanPlayer(game, playerSide);

    }
    @Override
    public boolean playerProcess() {
        player.process();
        return false;
    }

    @Override
    public void setNextMove(Side side, Vec2 startPos, Vec2 endPos) {
        if(side != playerSide) throw new IllegalArgumentException("cant set to move another person");
        player.setNextMove(startPos, endPos);
    }
}
