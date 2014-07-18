
package wasmoo.futuretank.field;

import java.awt.Point;
import java.util.Comparator;
import wasmoo.futuretank.Direction;

public class FieldPoint extends Point {
    /**
     * Returns a comparator that will sort by distance from a point.
     * @param origin the point to compare to
     * @return A Comparator to use for sorting
     */
    public static Comparator<FieldPoint> getDistanceComparator(final FieldPoint origin) {
        return new Comparator<FieldPoint>() {
            @Override
            public int compare(FieldPoint o1, FieldPoint o2) {
                return (int)Math.signum(-o2.distance(origin) + o1.distance(origin));
            }
        };
    }

    public FieldPoint(int x, int y) {
        super(x, y);
    }
    
    public FieldPoint(FieldPoint p) {
        super(p);
    }
    
    /**
     * Creates a new FieldPoint moving in the given direction by 1.
     */
    public FieldPoint getFieldPoint(Direction dir) {
        return dir.getFieldPoint(this);
    }
    
    /**
     * Returns true if this point is DIRECTLY [direction] of the origin.
     * @param dir The direction to check
     * @param origin The origin point
     * @return True if this point is [direction] of origin
     */
    public boolean isDirectionOf(Direction dir, FieldPoint origin) {
        return dir.isDirectionOf(origin, this);
    }
}
