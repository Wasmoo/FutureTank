
package wasmoo.futuretank.field;

import wasmoo.futuretank.Control;

/**
 * Represents a Movable Wall
 */
public class MovableWall extends FieldObject {

    public MovableWall(FieldPoint position) {
        super(Type.TYPE_MOVABLE_WALL, position, Control.MOVABLE_WALL_HEALTH);
    }

    public MovableWall clone() {
        return new MovableWall(position);
    }
    
}
