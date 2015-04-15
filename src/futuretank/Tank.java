
package futuretank;

import java.awt.Point;

public class Tank {

    public Tank() {
        this(++id_count);
    }
    
    private Tank(int id) {
        this.health = Control.TANK_HEALTH;
        this.id = id;
        this.position = new Point();
    }
    
    public void init() {
        
    }
    
    public void getActions(Field field, Action[] actions) {
    }
    
    public final void reset() {
        init();
        this.health = Control.TANK_HEALTH;
        this.charged = false;
    }

    @Override
    public final Tank clone() {
        Tank tank = new Tank(id);
        tank.charged = charged;
        tank.health = health;
        tank.direction = direction;
        tank.position.setLocation(position);
        return tank;
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof Tank) {
            return ((Tank)obj).id == this.id;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.id;
        return hash;
    }
    
    private static int id_count = 0;
    private final int id;
    public final Point position;
    public boolean charged;
    public int health;
    public int direction;
}
