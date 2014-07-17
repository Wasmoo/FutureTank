
package wasmoo.futuretank.field;

/**
 * Represents an object in the Field.
 */
public abstract class FieldObject implements Cloneable {
    public static enum Type {
        TYPE_TANK, TYPE_BREAKABLE_WALL, TYPE_MOVABLE_WALL
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
}
