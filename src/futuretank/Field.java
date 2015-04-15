package futuretank;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Field {
    public static final int WIDTH = Control.FIELD_WIDTH;
    public static final int HEIGHT = Control.FIELD_HEIGHT;
    private final HashMap<Point, ObjectType> map = new HashMap();
    private final ArrayList<Tank> tanks = new ArrayList();
    
    public Field() {
    };
    
    /**
     * Produces a deep copy of the Field, anonymizing any Tanks.
     * @return 
     */
    @Override
    public Field clone() {
        Field f = new Field();
        for (Point p : map.keySet()) {
            f.map.put(new Point(p), map.get(p));
        }
        for (Tank t : tanks) {
            f.tanks.add(t.clone());
        }
        return f;
    }
    
    /**
     * Returns true if the given point is on the field (and not off the edge)
     * @param p The point to test
     * @return true if on the field, false otherwise
     */
    public static boolean isOnField(Point p) {
        return p.x >= 0 && p.y >= 0 && p.x < WIDTH && p.y < HEIGHT;
    }
    
    /**
     * Relocates the point in the direction.
     * @param p The point to move
     * @param direction The direction to move in
     * @return p, the same point that was given
     */
    public static Point movePoint(Point p, int direction) {
        switch (direction) {
            case 0:
                p.y++;
                break;
            case 1:
                p.x++;
                break;
            case 2:
                p.y--;
                break;
            case 3:
                p.x--;
                break;
        }
        return p;
    }
    
    /**
     * Returns the ObjectType at the point, or null
     * @param point
     * @return 
     */
    public ObjectType getObject(Point point) {
        return map.get(point);
    }
    
    /**
     * Returns the Tank at the point, or null
     * @param point
     * @return 
     */
    public Tank getTank(Point point) {
        for (Tank t : tanks) {
            if (t.position.equals(point)) {
                return t;
            }
        }
        return null;
    }
    
    /**
     * Adjusts the Field to reflect this action being performed.
     * @param actor The tank performing the action
     * @param action The action being performed 
     * @return true if the field changed
     */
    public boolean doAction(Tank actor, Action action) {
        //No action taken for non-existent tanks
        if (tanks.indexOf(actor) == -1 || action == Action.NOTHING || action == null) {
            return false;
        }
        Tank t = tanks.get(tanks.indexOf(actor));
        boolean canMove = getObject(t.position) != ObjectType.HOLE;
        switch (action) {
            case RIGHT:
                if (t.charged) {
                    return canMove && move(t, (t.direction + 1) % 4);
                } else {
                    t.direction = (t.direction + 1) % 4;
                    return true;
                }
            case LEFT:
                if (t.charged) {
                    return canMove && move(t, (t.direction + 3) % 4);
                } else {
                    t.direction = (t.direction + 3) % 4;
                    return true;
                }
            case FORWARD:
                return canMove && move(t, t.direction);
            case BACKWARD:
                return canMove && move(t, (t.direction+2)%4);
            case CHARGE:
                boolean wasCharged = t.charged;
                t.charged = true;
                return !wasCharged;
            case SHOOT:
                return shoot(t);
            case BOMB:
                if (getObject(t.position) == null) {
                    put(ObjectType.BOMB, new Point(t.position));
                    return true;
                } else {
                    return false;
                }
            case WALL:
                Point newWall = movePoint(new Point(t.position), t.direction);
                if (getObject(newWall) == null && isOnField(newWall)) {
                    put(ObjectType.WALL, newWall);
                    return true;
                } else {
                    return false;
                }
        }
        return false;
    }
    
    private boolean move(Tank t, int direction) {
        boolean ret = t.charged;
        Point d1 = movePoint(new Point(t.position), direction);
        if (getObject(d1) == ObjectType.WALL) {
            t.charged = false;
            return ret;
        }
        
        Tank destTank = getTank(d1);
        if (destTank == null) {
            movePoint(t.position, direction);
            ret = true;
        } else {
            Point d2 = movePoint(new Point(d1), direction);
            if (getObject(d2) != ObjectType.WALL && getTank(d2) == null) {
                movePoint(t.position, direction);
                movePoint(destTank.position, direction);
                ret = true;
            } else {
                t.charged = false;
                return ret;
            }
        }
        
        if (t.charged) {
            t.charged = false;
            move(t, direction);
        }
        return ret;
    }
    
    private boolean shoot(Tank t) {
        boolean shot = false;
        Point p = movePoint(new Point(t.position), t.direction);
        while (isOnField(p)) {
            if (getObject(p) == ObjectType.WALL) {
                map.remove(p);
                shot = true;
                break;
            }
            if (getTank(p) != null) {
                getTank(p).health--;
                shot = true;
                break;
            }
            movePoint(p, t.direction);
        }
        if (t.charged) {
            t.charged = false;
            return (shot && shoot(t)) || true;
        }
        return shot;
    }
    
    /**
     * Evaluate this Field for holes. Any tank in a hole, off the edge,
     * or without health is removed from the field
     * @return An ArrayList of tanks that were removed
     */
    public ArrayList<Tank> doHoles() {
        ArrayList<Tank> ret = new ArrayList();
        Iterator<Tank> it = tanks.iterator();
        while (it.hasNext()) {
            Tank t = it.next();
            if (t.health < 0 || map.get(t.position) == ObjectType.HOLE || !isOnField(t.position)) {
                it.remove();
                ret.add(t);
            }
        }
        return ret;
    }

    /**
     * Explodes bombs, which create holes. Subsequently calls doHoles();
     * @return An ArrayList of tanks that were removed
     */
    public ArrayList<Tank> doBombs() {
        for (Point p : map.keySet()) {
            if (map.get(p) == ObjectType.BOMB) {
                map.put(p, ObjectType.HOLE);
            }
        }
        return doHoles();
    }
    
    /**
     * Gets all the points with objects on the Field (not including Tanks)
     */
    public Set<Point> getPoints() {
        return map.keySet();
    }
    
    /**
     * Gets all the Tanks on the Field
     */
    public ArrayList<Tank> getTanks() {
        return tanks;
    }


    /**
     * Puts an Object at the given point
     */
    public void put(ObjectType obj, Point point) {
        map.put(point, obj);
    }
    
    /**
     * Puts a Tank at the given point
     */
    public void put(Tank tank, Point point) {
        tanks.add(tank);
        tank.position.setLocation(point);
    }
    
}
