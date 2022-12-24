package geometry;

@SuppressWarnings("ClassCanBeRecord")
public class Vec2 {
    public final int x;
    public final int y;

    public Vec2(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Vec2 add(Vec2 toAdd){
        return new Vec2(x + toAdd.x, y + toAdd.y);
    }

    public double getLen(){
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    @Override
    public boolean equals(Object object) {
        if(object instanceof Vec2 test) {
            return x == test.x && y == test.y;
        }
        return false;
    }

    @Override
    public String toString(){
        return "x=" + x + ", y=" + y;
    }
}
