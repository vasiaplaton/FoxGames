package geometry;

import java.util.List;

public record GeometryMove(Vec2 start, Vec2 end, List<Vec2> poses) {
}
