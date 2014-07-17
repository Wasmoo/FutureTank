
package wasmoo.futuretank;

import wasmoo.futuretank.field.FieldPoint;
import wasmoo.futuretank.field.FieldTank;

public class TankState {
    public FieldTank tank;
    public boolean charged;

    public TankState(FieldTank tank, boolean charged) {
        this.tank = tank;
        this.charged = charged;
    }

    public int getHealth() {
        return tank.health;
    }
    
    public Direction getDirection() {
        return tank.direction;
    }
    
    public FieldPoint getPosition() {
        return tank.position;
    }
    
    

    
}
