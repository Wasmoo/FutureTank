
package wasmoo.futuretank.field;

import java.util.Comparator;

/**
 * Represents an object in the Field.
 */
public abstract class FieldObject implements Cloneable {
    public static enum Type {
        TYPE_TANK, TYPE_BREAKABLE_WALL, TYPE_MOVABLE_WALL, TYPE_HOLE
    }
    public final Type type;
    public int health;
    public FieldPoint position;

    public FieldObject(Type type, FieldPoint position, int health) {
        this.type = type;
        this.health = health;
        this.position = position;
    }
    
    abstract public FieldObject clone();
    
    /**
     * Returns a comparator that will sort by distance from a point.
     * @param originObject the object's point to compare to
     * @return A Comparator to use for sorting
     */
    public static Comparator<FieldObject> getDistanceComparator(final FieldObject originObject) {
        return getDistanceComparator(originObject.position);
    }
    
    /**
     * Returns a comparator that will sort by distance from a point.
     * @param origin the point to compare to
     * @return A Comparator to use for sorting
     */
    public static Comparator<FieldObject> getDistanceComparator(final FieldPoint origin) {
        if (origin == null) throw new IllegalArgumentException("Argument cannot be null");
        return new Comparator<FieldObject>() {
            @Override
            public int compare(FieldObject o1, FieldObject o2) {
                return (int)Math.signum(-o2.position.distance(origin) + o1.position.distance(origin));
            }
        };
    }
    
}
