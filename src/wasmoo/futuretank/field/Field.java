package wasmoo.futuretank.field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import wasmoo.futuretank.Control;
import wasmoo.futuretank.Direction;

public class Field {
    public static final int FIELD_WIDTH = Control.FIELD_WIDTH;
    public static final int FIELD_HEIGHT = Control.FIELD_HEIGHT;
    private final HashMap<FieldPoint, FieldObject> pointMap = new HashMap();
    private final HashMap<FieldObject, FieldPoint> objectMap = new HashMap();
    
    public Field() {};
    /**
     * Creates a deep copy of the field
     * @param f The field to copy
     */
    public Field(Field f) {
        for (Map.Entry<FieldPoint, FieldObject> e : f.pointMap.entrySet()) {
            FieldPoint p = new FieldPoint(e.getKey());
            FieldObject obj = e.getValue().clone();
            pointMap.put(p, obj);
            objectMap.put(obj, p);
        }
    }
    
    /**
     * Puts an object at a given point
     * @param point The point to put the object
     * @param obj The object to put
     * @return The object that used to be at the point
     */
    public FieldObject put(FieldPoint point, FieldObject obj) {
        obj.position.setLocation(point);
        objectMap.put(obj, point);
        return pointMap.put(point, obj);
    }
    
    public FieldObject remove(FieldPoint point) {
        FieldObject oldObject = pointMap.remove(point);
        if (oldObject != null) {
            objectMap.remove(oldObject);
        }
        return oldObject;
    }
    
    public FieldPoint remove(FieldObject object) {
        FieldPoint oldPoint = objectMap.remove(object);
        if (oldPoint != null) {
            pointMap.remove(oldPoint);
        }
        return oldPoint;
    }
    
    /**
     * Returns the object at the given point
     * @param p The point
     * @return  The object
     */
    public FieldObject get(FieldPoint p) {
        return pointMap.get(p);
    }
    
    /**
     * Returns all objects in the direction of the given point
     * @param origin The point
     * @param dir The direction
     * @return  The object
     */
    public ArrayList<FieldObject> get(FieldPoint origin, Direction dir) {
        ArrayList<FieldObject> ret = new ArrayList();
        FieldPoint p = dir.getFieldPoint(origin);
        while (isOnField(p)) {
            FieldObject obj = get(p);
            if (obj != null) ret.add(obj);
            p = dir.getFieldPoint(p);
        }
        return ret;
    }
    
    /**
     * Returns the location of the given object
     * @param obj
     * @return 
     */
    public FieldPoint get(FieldObject obj) {
        return objectMap.get(obj);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Field) {
            Field f = (Field)obj;
            return f.pointMap.equals(pointMap)
                    && f.objectMap.equals(objectMap);
        }
        return super.equals(obj); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.pointMap);
        hash = 79 * hash + Objects.hashCode(this.objectMap);
        return hash;
    }
    
    public boolean isOnField(FieldPoint p) {
        return p.x >= 0 && p.x < FIELD_WIDTH && p.y >= 0 && p.y < FIELD_HEIGHT;
    }
    
    /**
     * Returns all objects in the field
     * @return 
     */
    public Set<FieldObject> getAllObjects() {
        return objectMap.keySet();
    }

    /**
     * Returns all points at which there is an object
     * @return 
     */
    public Set<FieldPoint> getAllPoints() {
        return pointMap.keySet();
    }
}
