
package wasmoo.futuretank.tank;

import wasmoo.futuretank.Action;
import wasmoo.futuretank.TankState;
import wasmoo.futuretank.field.Field;

public class RandomTank extends Tank {

    @Override
    public Action getAction(Field field, TankState state) {
        return Action.values()[(int)(Math.random() * Action.values().length)];
    }
    
}
