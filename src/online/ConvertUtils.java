package online;

import game.GameCellStates;
import game.Side;
import geometry.GeometryMove;
import geometry.Vec2;

import java.util.ArrayList;
import java.util.List;

public class ConvertUtils {
    static Side parseSide(char c){
        return switch (c) {
            case 'F' -> Side.FOX_TURN;
            case 'G' -> Side.GOOSE_TURN;
            case 'N' -> null;
            default -> throw new IllegalStateException("Unexpected value: " + c);
        };
    }

    static char toSide(Side side){
        if(side == null) return 'N';
        return switch (side) {
            case FOX_TURN -> 'F';
            case GOOSE_TURN -> 'G';
        };
    }

    static GameCellStates parseState(char c){
        return switch (c) {
            case 'F' -> GameCellStates.FOX;
            case 'G' -> GameCellStates.GOOSE;
            case 'O' -> GameCellStates.FREE;
            case 'N' -> null;
            default -> throw new IllegalStateException("Unexpected value: " + c);
        };
    }

    static char toState(GameCellStates s){
        if(s == null) return 'N';
        return switch (s) {
            case FOX -> 'F';
            case GOOSE -> 'G';
            case FREE -> 'O';
        };
    }

    static Boolean parseBoolean(char c){
        return switch (c) {
            case 'T' -> true;
            case 'F' -> false;
            default -> throw new IllegalStateException("Unexpected value: " + c);
        };
    }

    static char toBoolean(boolean b){
        return b ? 'T' :  'F';
    }

    static String toVec(Vec2 p){
        return p.x + "," + p.y;
    }

    static Vec2 parseVec(String string){
        String[] str = string.split(",");
        if(str.length != 2) throw new IllegalArgumentException();
        return new Vec2(Integer.parseInt(str[0]), Integer.parseInt(str[1]));
    }

    static String toList(List<Vec2> list) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            s.append(list.get(i).x).append(",").append(list.get(i).y);
            if(i != list.size() - 1) s.append(",");
        }
        return s.toString();
    }

    static List<Vec2> parseList(String string){
        List<Vec2> list = new ArrayList<>();
        String[] nums = string.split(",");
        for (int i = 0; i < nums.length / 2; i++) {
            list.add(new Vec2(Integer.parseInt(nums[i*2]), Integer.parseInt(nums[i*2+1])));
        }
        return list;
    }

    static String toGeometryMove(GeometryMove move){
        return toVec(move.start()) + "/" + toList(move.poses()) + "/" + toVec(move.end());
    }

    static GeometryMove parseGeometryMove(String string){
        String[] str = string.split("/");
        if(str.length != 3) throw new IllegalArgumentException();
        return new GeometryMove(parseVec(str[0]), parseVec(str[2]), parseList(str[1]));
    }

    static String toGeometryMoves(List<GeometryMove> moves){
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < moves.size(); i++) {
            s.append(toGeometryMove(moves.get(i)));
            if(i != moves.size() - 1) s.append("]");
        }
        return s.toString();
    }

    static List<GeometryMove> parseGeometryMoves(String input){
        List<GeometryMove> list = new ArrayList<>();
        String[] str = input.split("]");
        for (String s : str) {
            list.add(parseGeometryMove(s));
        }
        return list;
    }


}
