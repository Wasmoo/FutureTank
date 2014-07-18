
package wasmoo.futuretank.tank;

import wasmoo.futuretank.Action;
import wasmoo.futuretank.TankState;
import wasmoo.futuretank.field.Field;

public abstract class Tank {
    abstract public Action getAction(Field field, TankState state);

    @Override
    public String toString() {
        String ret = super.toString();
        return ret.substring(ret.lastIndexOf(".")+1);
    }
    
    
}
