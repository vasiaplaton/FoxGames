package players;

import game.GameAdapter;
import game.Side;

public abstract class Player {
    protected final GameAdapter game;
    public final Side side;
    public Player(GameAdapter game, Side side) {
        this.game = game;
        this.side = side;
    }
    public abstract boolean process();
}
