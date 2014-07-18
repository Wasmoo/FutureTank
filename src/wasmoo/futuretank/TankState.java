
package wasmoo.futuretank;

import wasmoo.futuretank.field.Field;
import wasmoo.futuretank.field.FieldPoint;
import wasmoo.futuretank.field.FieldTank;

public class TankState {
    public FieldTank tank;
    public boolean charged;

    public TankState(TankState original, Field field) {
        //Find the FieldTank in the field
        this.tank = (FieldTank)field.get(original.tank.position);
        this.charged = original.charged;
    }

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
