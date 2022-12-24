package players.local;

import game.GameAdapter;
import game.Side;
import geometry.Vec2;
import players.*;

public class LocalPlayersHandler implements PlayersHandler {
    private Player foxPlayer;
    private Player goosePlayer;

    private final GameAdapter game;

    public LocalPlayersHandler(GameAdapter game, LocalGameType type) {
        this.game = game;
        initPlayers(type);
    }

    private void setFoxPlayer(Player foxPlayer) {
        if (foxPlayer.side != Side.FOX_TURN) throw new IllegalArgumentException();
        this.foxPlayer = foxPlayer;
    }

    private void setGoosePlayer(Player goosePlayer) {
        if (goosePlayer.side != Side.GOOSE_TURN) throw new IllegalArgumentException();
        this.goosePlayer = goosePlayer;
    }

    public boolean playerProcess() {
        if (foxPlayer != null && game.whichMoveNow() == Side.FOX_TURN) return foxPlayer.process();
        else if (goosePlayer != null && game.whichMoveNow() == Side.GOOSE_TURN) return goosePlayer.process();
        return false;
    }

    public Player getPlayerBySide(Side side) {
        return switch (side) {
            case FOX_TURN -> foxPlayer;
            case GOOSE_TURN -> goosePlayer;
        };
    }

    public void setNextMove(Side side, Vec2 startPos, Vec2 endPos){
        if(getPlayerBySide(side) instanceof HumanPlayer player) {
            player.setNextMove(startPos, endPos);
        } else {
            throw new IllegalArgumentException("cant set next move for not human player");
        }
    }
    
    public void initPlayers(LocalGameType type){
        switch (type) {
            case FOX_BOT -> {
               setFoxPlayer(new BotPlayer(game, Side.FOX_TURN));
               setGoosePlayer(new HumanPlayer(game, Side.GOOSE_TURN));
            }
            case GOOSE_BOT -> {
               setGoosePlayer(new BotPlayer(game, Side.GOOSE_TURN));
               setFoxPlayer(new HumanPlayer(game, Side.FOX_TURN));
            }
            case TWO_BOTS -> {
               setGoosePlayer(new BotPlayer(game, Side.GOOSE_TURN));
               setFoxPlayer(new BotPlayer(game, Side.FOX_TURN));
            }
            case LOCAL_GAME -> {
               setFoxPlayer(new HumanPlayer(game, Side.FOX_TURN));
               setGoosePlayer(new HumanPlayer(game, Side.GOOSE_TURN));
            }
        }
    }
    
}
