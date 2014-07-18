package wasmoo.futuretank;

import wasmoo.futuretank.field.FieldPoint;

public enum Direction {
    EAST(1, 0), 
    NORTH(0, 1), 
    WEST(-1, 0), 
    SOUTH(0, -1);
    
    private final int deltaX;
    private final int deltaY;

    private Direction(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }
    
    /**
     * Returns true if the destination point is DIRECTLY [direction] of origin.
     * @param origin The origin point
     * @param dest The destination point
     * @return True if dest is [direction] of origin
     */
    public boolean isDirectionOf(FieldPoint origin, FieldPoint dest) {
        int dx = (int)Math.signum(dest.x - origin.x);
        int dy = (int)Math.signum(dest.y - origin.y);
        return deltaX == dx && deltaY == dy;
    }
    
    /**
     * Creates a new FieldPoint moving in this direction by 1.
     * @param p
     * @return 
     */
    public FieldPoint getFieldPoint(FieldPoint p) {
        return new FieldPoint(p.x + deltaX, p.y + deltaY);
    }
    
    /**
     * Creates a new FieldPoint moving in this direction by the distance.
     * @param p
     * @param distance
     * @return 
     */
    public FieldPoint getFieldPoint(FieldPoint p, int distance) {
        return new FieldPoint(p.x + deltaX*distance, p.y + deltaY*distance);
    }
    
    /**
     * Returns the Direction of the given action, relative to this direction.
     * @param a
     * @return 
     */
    public Direction getDirection(Action a) {
        switch (a) {
            case BACKWARD: return values()[(ordinal()+2) % 4];
            case LEFT: return values()[(ordinal()+1) % 4];
            case RIGHT: return values()[(ordinal()+3) % 4];
            default: return this;
        }
    }
}
