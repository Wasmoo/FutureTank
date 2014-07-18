
package wasmoo.futuretank;

import java.util.ArrayList;
import wasmoo.futuretank.field.BreakableWall;
import wasmoo.futuretank.field.Field;
import wasmoo.futuretank.field.FieldObject;
import wasmoo.futuretank.field.FieldPoint;
import wasmoo.futuretank.field.Hole;
import wasmoo.futuretank.field.MovableWall;

public enum Action {
    NOTHING,
    LEFT,
    RIGHT,
    FORWARD,
    BACKWARD,
    SHOOT,
    CHARGE;
    
    
    /**
     * Returns true if the action would have an effect on the field.
     * Note that charging and discharging doesn't count as having an effect
     * on the field.
     * @param field The field to take the action upon
     * @param state The TankState performing the action
     * @return 
     */
    public boolean canDoAction(Field field, TankState state) {
        Direction dir = state.tank.direction.getDirection(this);
        switch (this) {
            case RIGHT:
            case LEFT:
                if (state.charged) {
                    return canShove(field, dir, state.tank.position);
                } else {
                    //Turning always has an effect
                    return true;
                }
            case FORWARD:
            case BACKWARD:
                if (state.charged) {
                    canShove(field, dir, state.tank.position);
                } else {
                    canMove(field, dir, state.tank.position);
                }
                break;
            case NOTHING:
                //Doing nothing can discharge
                return false;
            case CHARGE:
                //Charging when charged technically does nothing
                return false;
            case SHOOT:
                ArrayList<FieldObject> objs = field.get(state.tank.position, dir);
                return (!objs.isEmpty() && !(objs.get(0) instanceof MovableWall));
        }
        return false;
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
                    move(field, dir, state.tank.position);
                }
                break;
            case NOTHING:
                state.charged = false;
                break;
            case CHARGE:
                state.charged = true;
                break;
            case SHOOT:
                ArrayList<FieldObject> objs = field.get(state.tank.position, dir);
                for (FieldObject obj : objs) {
                    if (obj instanceof Hole) continue;
                    obj.health -= state.charged ? 2 : 1;
                    break;
                }
                state.charged = false;
                break;
        }
    }
    
    private boolean canMove(Field field, Direction dir, FieldPoint origin) {
        return move(field, dir, origin, 1, true);
    }
    private boolean move(Field field, Direction dir, FieldPoint origin) {
        return move(field, dir, origin, 1, false);
    }
    private boolean canShove(Field field, Direction dir, FieldPoint origin) {
        return shove(field, dir, origin, true);
    }
    private boolean shove(Field field, Direction dir, FieldPoint origin) {
        return shove(field, dir, origin, false);
    }
    
    private boolean move(Field field, Direction dir, FieldPoint origin, int dist, boolean checkOnly) {
        if (dist < 0) return false;
        FieldPoint dest = dir.getFieldPoint(origin);
        FieldObject obj = field.get(dest);
        if (obj instanceof BreakableWall) {
            return false;
        } else if (obj == null || obj instanceof Hole || move(field, dir, dest, dist-1, checkOnly)) {
            if (!checkOnly) {
                obj = field.remove(origin);
                field.put(dest, obj);
            }
            return true;
        } else {
            return false;
        }
    }
    
    private boolean shove(Field field, Direction dir, FieldPoint origin, boolean checkOnly) {
        FieldPoint dest = dir.getFieldPoint(origin);
        FieldObject obj = field.get(dest);
        boolean ret = false;
        if (obj == null || obj instanceof Hole) {
            ret = true;
            if (!checkOnly) {
                obj = field.remove(origin);
                field.put(dest, obj);
                dir.getFieldPoint(dest);
            }
        }
        ret = move(field, dir, origin, 2, checkOnly) || ret;
        return ret;
    }
}
