package graphic;

import game.Game;
import game.GameAdapter;
import game.GameCellStates;
import game.Side;
import geometry.GeometryMove;
import geometry.Vec2;
import online.*;
import players.*;
import players.local.LocalPlayersHandler;

import javax.swing.*;
import java.awt.*;
import java.io.IOError;
import java.io.IOException;
import java.util.List;

public class BoardGraphics {
    private GameAdapter game;

    private PlayersHandler playersHandler;
    private final Image backImg = new ImageIcon("img/background.jpeg").getImage();
    private final Image duckImg = new ImageIcon("img/duck.png").getImage();
    private final Image foxImg = new ImageIcon("img/fox.png").getImage();

    private final Color unhighlightedGrid = new Color(255, 255, 255, 150);
    private final Color highlightedGrid = new Color(0, 255, 0, 150);


    private Vec2 chosenCellPos;

    private int cellSize;
    private int offset;

    private boolean gameFinished = false;

    public BoardGraphics(){
        initNewGame();
    }

    private void initNewGame(){
        chooseGameDialog dialog = new chooseGameDialog();
        dialog.pack();
        dialog.setVisible(true);

        if(!dialog.isOnline()) {
            game = new Game();
            playersHandler = new LocalPlayersHandler(game, dialog.whichTypeChosen());
            gameFinished = false;
            return;
        }

        inputServerDialog serverDialog = new inputServerDialog();
        serverDialog.pack();
        serverDialog.setVisible(true);


        if(serverDialog.getParams() == null) {
            initNewGame();
            return;
        }
        inputServerDialog.ServerParams params = serverDialog.getParams();
        try {
            OnlineGame game = new OnlineGame(params.host(), params.port());
            boolean res = false;
            if(params.toExist()) {
                res = game.connectToExistingRoom(params.roomId());
            }
            else {
                int room_id = game.createNewRoom(params.side());
                if(room_id != -1){
                    res = true;
                    JOptionPane.showMessageDialog(null, "room id:" + room_id);
                }
                else {
                    JOptionPane.showMessageDialog(null, "can't force sever to create new room");
                }
            }
            if(!res) initNewGame();
            this.game = game;
            playersHandler = new OnlinePlayerHandler(game, game.whichMoveNow());
            gameFinished = false;
        }
        catch (IOError | IOException e) {
            JOptionPane.showMessageDialog(null, new JLabel(e.getMessage()), "error", JOptionPane.ERROR_MESSAGE);
            initNewGame();
        }

    }
    
    private void drawCellGrid(GameAdapter game, Vec2 pos, Graphics2D canvas){
        canvas.setStroke(new BasicStroke(3));

        canvas.setColor(unhighlightedGrid);
        if(chosenCellPos != null && chosenCellPos.equals(pos)) {
            canvas.setColor(highlightedGrid);
        }

        int radius = (int) (cellSize * 0.27);

        canvas.drawOval(getCenterOfCell(pos).x-radius, getCenterOfCell(pos).y-radius,
                radius * 2 -1, radius * 2 -1);

        for (Vec2 move: game.getMovesToAllCloseNeighbours(pos)) {
            canvas.setColor(unhighlightedGrid);
            drawMoveOnGrid(canvas, getCenterOfCell(pos), move, radius);
        }

        if(chosenCellPos != null && chosenCellPos.equals(pos)) {
            for (GeometryMove move: game.getPossibleMoves(chosenCellPos)) {
                Vec2 posNow = move.start();

                List<Vec2> closeMoves = move.poses();
                for (Vec2 closeMove: closeMoves) {
                    canvas.setColor(highlightedGrid);
                    drawMoveOnGrid(canvas, getCenterOfCell(posNow), closeMove, radius);
                    posNow = posNow.add(closeMove);
                    drawMoveOnGrid(canvas, getCenterOfCell(posNow), new Vec2(-closeMove.x, -closeMove.y), radius);
                }

            }
        }
    }
    private void drawMoveOnGrid(Graphics2D canvas, Vec2 center, Vec2 move, int radius){
        canvas.drawLine((int) (center.x + move.x * radius / move.getLen()),
                (int) (center.y + move.y * radius / move.getLen()),
                (center.x + move.x * cellSize / 2),
                 (center.y + move.y * cellSize / 2));
    }

    public void click(Vec2 posOnScreen){
        if(gameFinished ) {
            initNewGame();
            return;
        }
        Vec2 chosenPos = new Vec2((posOnScreen.x - offset) / cellSize, (posOnScreen.y - offset) / cellSize);
        if(posOnScreen.x - offset < 0 ) chosenPos = chosenPos.add(new Vec2(-1, 0));
        if(posOnScreen.y - offset < 0 ) chosenPos = chosenPos.add(new Vec2(0, -1));

        Vec2 clickedPos = chosenPos;

        if(chosenCellPos != null) {
            playersHandler.setNextMove(game.whichMoveNow(), chosenCellPos, chosenPos);
            chosenCellPos = null;
        }
        else if((game.getCellState(clickedPos) == GameCellStates.FOX && game.whichMoveNow() == Side.FOX_TURN)
                || (game.getCellState(clickedPos) == GameCellStates.GOOSE && game.whichMoveNow() == Side.GOOSE_TURN)){
            chosenCellPos = clickedPos;
        } else {
            chosenCellPos = null;
        }
    }

    public void draw(Graphics2D canvas, int width, int height) {
        playersHandler.playerProcess();

        int size = Math.min(width, height);

        double scale = 0.8;
        cellSize = (int) (size / game.getBoardSize() * scale);
        offset = (int) (size * (1 - scale) / 2);

        canvas.drawImage(backImg, 0, 0, size, size, null);


        for (int y = 0; y < game.getBoardSize(); y++) {
            for (int x = 0; x < game.getBoardSize(); x++) {
                Vec2 pos = new Vec2(x, y);

                Image img = null;
                GameCellStates state = game.getCellState(pos);
                if(state == null) continue;
                switch (state) {
                    case GOOSE -> img = duckImg;
                    case FOX -> img = foxImg;
                }

                canvas.drawImage(img, getUpperLeftOfCell(pos).x, getUpperLeftOfCell(pos).y, cellSize, cellSize, null);

                drawCellGrid(game, pos, canvas);

            }
        }
        if(game.getWinner() != null) {
            gameFinished = true;
            canvas.setColor(Color.BLACK);
            canvas.setFont(new Font("Arial", Font.PLAIN, size / 6));
            if (game.getWinner() == Side.FOX_TURN) {
                canvas.drawString("FOX WIN", 10, size / 2);
            }
            if (game.getWinner() == Side.GOOSE_TURN) {
                canvas.drawString("GOOSE WIN", 10, size / 2);
            }
            canvas.setFont(new Font("Arial", Font.PLAIN, size / 12));
            canvas.drawString("click in any place to restart", 10, size * 2 / 3);
        }
    }
    
    private Vec2 getUpperLeftOfCell(Vec2 posOnBoard){
        return new Vec2(posOnBoard.x * cellSize + offset,
                (posOnBoard.y * cellSize + offset));
    }

    private Vec2 getCenterOfCell(Vec2 posOnBoard){
        return getUpperLeftOfCell(posOnBoard).add(new Vec2(cellSize/2, cellSize/2));
    }


}
