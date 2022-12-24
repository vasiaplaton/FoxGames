package console;

import game.Game;
import game.GameAdapter;
import game.GameCellStates;
import game.Side;
import geometry.GeometryMove;
import geometry.Vec2;
import players.local.*;
import players.*;
import players.local.LocalPlayersHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BoardConsole {

    private GameAdapter game;
    private PlayersHandler playersHandler;
    private final Scanner scanner;

    private Vec2 chosenCellPos;

    private String resultLastAction = "";
    public BoardConsole(){
        scanner = new Scanner(System.in);
        initNewGame();
    }

    public void initNewGame(){
        cleanScreen();
        System.out.println("choose game type");
        System.out.println("t/T - two bots, g/G - goose bot, f/F - fox bot, o/O - online game, l/L/* - local");
        String input = scanner.nextLine();

        boolean isOnline = false;
        LocalGameType type = LocalGameType.LOCAL_GAME;

        switch (input.toCharArray()[0]) {
            case 'g', 'G' -> type = LocalGameType.GOOSE_BOT;
            case 'f', 'F' -> type = LocalGameType.FOX_BOT;
            case 't', 'T' -> type = LocalGameType.TWO_BOTS;
            case 'o', 'O' -> {
                isOnline = true;
                type = null;
            }
        }

        if(!isOnline) {
            game = new Game();
            playersHandler = new LocalPlayersHandler(game, type);
            return;
        }

    }

    public void cleanScreen(){
        System.out.print("\033\143");
    }

    public void draw(){
        StringBuilder screen = new StringBuilder();

        screen.append(" ");
        for (int x = 0; x < game.getBoardSize(); x++) {
            if(chosenCellPos != null && chosenCellPos.x == x) screen.append("|");
            else screen.append(x % 10);
        }
        screen.append("\n");
        List<Vec2> endCellPoses = new ArrayList<>();
        if(chosenCellPos != null) {
            for (GeometryMove move: game.getPossibleMoves(chosenCellPos)) {
                endCellPoses.add(move.end());
            }
        }
        for (int y = 0; y < game.getBoardSize(); y++) {
            if(chosenCellPos != null && chosenCellPos.y== y) screen.append("-");
            else screen.append(y % 10);
            for (int x = 0; x < game.getBoardSize(); x++) {
                Vec2 pos = new Vec2(x, y);
                TerminalColors color = TerminalColors.STD;
                if(pos.equals(chosenCellPos))  {
                    color = TerminalColors.RED;
                }
                if(endCellPoses.contains(pos)) {
                    color = TerminalColors.GREEN;
                }
                screen.append(getCellString(game.getCellState(pos), color));
            }
            screen.append("\n");
        }

        cleanScreen();
        System.out.println();
        System.out.print(screen);

        if(game.getWinner() != null) {
            System.out.println(game.getWinner() + " win, e!");
            System.out.println("press any key restart");
            scanner.nextLine();
            initNewGame();
        }

        System.out.println(resultLastAction);

        if(playersHandler.playerProcess()){
            return;
        }

        String prompt = "Now is "  + game.whichMoveNow() + " move\n";
        if(chosenCellPos == null) {
            prompt += "No cell chosen, choose cell";
        }
        else {
            prompt += "Choose cell to move";
        }
        prompt += " x, y:";
        System.out.print(prompt);

        Vec2 inputCellPos = inputCell(scanner.nextLine());
        if(inputCellPos == null) {
            System.out.println("chosen cell is null");
            return;
        }

        System.out.println("input cell:" + inputCellPos + " input");

        if(chosenCellPos == null) {
            if((game.getCellState(inputCellPos) == GameCellStates.FOX && game.whichMoveNow() == Side.FOX_TURN)
                    || (game.getCellState(inputCellPos)  == GameCellStates.GOOSE && game.whichMoveNow() == Side.GOOSE_TURN)){
                chosenCellPos = inputCellPos;
                resultLastAction = "Cell " + chosenCellPos + ", " + game.getCellState(inputCellPos) + " is chosen";
            }
            else {
                resultLastAction = "input cell is:" + game.getCellState(inputCellPos) + ", but now is " +
                       game.whichMoveNow() + " move";

            }
        }
        else {
            playersHandler.setNextMove(game.whichMoveNow(), chosenCellPos, inputCellPos);
            resultLastAction = "try to move";
            //}
           // else resultLastAction = "error move";
            chosenCellPos = null;

        }

    }

    public Vec2 inputCell(String input){
        String[] inputs = input.split(",");
        if (inputs.length != 2) {
            System.out.println("error number format");
            return null;
        }
        try {
            int x = Integer.parseInt(inputs[0].strip());
            int y = Integer.parseInt(inputs[1].strip());
            return new Vec2(x, y);
        } catch (java.lang.NumberFormatException e) {
            System.out.println("error number format");
        }

        return null;
    }

    public String getCellString(GameCellStates cellState, TerminalColors color){
        String ANSI_RESET = "\u001B[0m";
        String ANSI_RED = "\u001B[31m";
        String ANSI_GREEN = "\u001B[32m";

        String nullCell = "*";
        String freeCell = "○";
        String foxCell = "A";
        String gooseCell = "W";

        String res = switch (color) {
            case RED -> ANSI_RED;
            case GREEN -> ANSI_GREEN;
            case STD -> "";
        };

        if(cellState == null) {
            res += nullCell;
        }
        else res += switch (cellState) {
            case GOOSE -> gooseCell;
            case FOX -> foxCell;
            case FREE ->  freeCell;
        };
        res += ANSI_RESET;
        return res;
    }

    private enum TerminalColors{
        RED,
        GREEN,
        STD
    }
}
