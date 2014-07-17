/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wasmoo.futuretank.field;

/**
 * Represents a Movable Wall
 */
public class MovableWall extends FieldObject {

    public MovableWall(FieldPoint position) {
        super(Type.TYPE_MOVABLE_WALL, position, Integer.MAX_VALUE);
    }

    public MovableWall clone() {
        return new MovableWall(position);
    }
    
}
