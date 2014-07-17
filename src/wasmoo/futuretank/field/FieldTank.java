/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wasmoo.futuretank.field;

import wasmoo.futuretank.Direction;

/**
 * Represents a tank in the field. Has direction and health
 */
public class FieldTank extends FieldObject {
    public Direction direction;

    public FieldTank(FieldPoint position, int health, Direction direction) {
        super(Type.TYPE_TANK, position, health);
        this.direction = direction;
    }

    public FieldTank clone() {
        return new FieldTank(new FieldPoint(position), health, direction);
    }
    
}
