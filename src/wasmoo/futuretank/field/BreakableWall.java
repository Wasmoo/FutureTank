/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wasmoo.futuretank.field;

/**
 * Represents a Breakable Wall. Has health
 */
public class BreakableWall extends FieldObject {
    public BreakableWall(FieldPoint position, int healthLeft) {
        super(Type.TYPE_BREAKABLE_WALL, position, healthLeft);
    }

    public BreakableWall clone() {
        return new BreakableWall(new FieldPoint(position), health);
    }
    
}
