
package wasmoo.futuretank;

import java.util.ArrayList;
import wasmoo.futuretank.field.BreakableWall;
import wasmoo.futuretank.field.Field;
import wasmoo.futuretank.field.FieldObject;
import wasmoo.futuretank.field.FieldPoint;
import wasmoo.futuretank.field.MovableWall;

public enum Action {
    LEFT,
    RIGHT,
    FORWARD,
    BACKWARD,
    SHOOT,
    CHARGE;
    
    public void canDoAction(Field field, TankState state) {
        
    }
    
    /**
     * Adjusts the Field and TankState to reflect this action being
     * performed.
     * @param field
     * @param state 
     */
    public void doAction(Field field, TankState state) {
        Direction dir = state.tank.direction.getDirection(this);
        switch (this) {
            case RIGHT:
            case LEFT:
                if (state.charged) {
                    shove(field, dir, state.tank.position);
                    state.charged = false;
                } else {
                    state.tank.direction = dir;
                }
                break;
            case FORWARD:
            case BACKWARD:
                if (state.charged) {
                    shove(field, dir, state.tank.position);
                    state.charged = false;
                } else {
                    move(field, dir, state.tank.position, 1);
                }
                break;
            case CHARGE:
                state.charged = true;
                break;
            case SHOOT:
                ArrayList<FieldObject> objs = field.get(state.tank.position, dir);
                if (!objs.isEmpty()){
                    FieldObject obj = objs.get(0);
                    if (!(obj instanceof MovableWall)) {
                        obj.health -= state.charged ? 2 : 1;
                        if (obj.health <= 0) field.remove(obj);
                    }
                }
                state.charged = false;
                break;
        }
    }
    
    private boolean move(Field field, Direction dir, FieldPoint origin, int dist) {
        if (dist < 0) return false;
        FieldPoint dest = dir.getFieldPoint(origin);
        FieldObject obj = field.get(dest);
        if (obj instanceof BreakableWall) {
            return false;
        } else if (obj == null || move(field, dir, dest, dist-1)) {
            obj = field.remove(origin);
            if (field.isOnField(dest)) {
                field.put(dest, obj);
            }
            return true;
        } else {
            return false;
        }
    }
    
    private void shove(Field field, Direction dir, FieldPoint origin) {
        FieldPoint dest = dir.getFieldPoint(origin);
        FieldObject obj = field.get(dest);
        if (obj == null) {
            obj = field.remove(origin);
            field.put(dest, obj);
            dir.getFieldPoint(dest);
        }
        move(field, dir, origin, 2);
    }
}
