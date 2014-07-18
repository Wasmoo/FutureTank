
package wasmoo.futuretank.field;

/**
 * Represents a Movable Wall
 */
public class Hole extends FieldObject {

    public Hole(FieldPoint position) {
        super(Type.TYPE_HOLE, position, Integer.MAX_VALUE);
    }

    public Hole clone() {
        return new Hole(position);
    }
    
}
