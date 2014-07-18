
package wasmoo.futuretank;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import wasmoo.futuretank.field.BreakableWall;
import wasmoo.futuretank.field.Field;
import wasmoo.futuretank.field.FieldObject;
import wasmoo.futuretank.field.FieldPoint;
import wasmoo.futuretank.field.FieldTank;
import wasmoo.futuretank.field.Hole;
import wasmoo.futuretank.field.MovableWall;
import wasmoo.futuretank.tank.RandomTank;
import wasmoo.futuretank.tank.Tank;

public final class Control {
    private final Random rnd = new Random();
    
    public static final boolean useUI = true;
    public static final int FIELD_WIDTH = 30;
    public static final int FIELD_HEIGHT = 10;
    public static final int TANK_HEALTH = 5;
    public static final int BREAKABLE_WALL_HEALTH = 1;
    public static final int MOVABLE_WALL_HEALTH = 5;
    public static final int ROUND_COUNT = 100;
    public static final long SLEEP = 100;
    
    private final ArrayList<Tank> tanks;
    private final HashMap<Tank, Integer> score = new HashMap();
    private final ArrayList<Tank> highlightTanks = new ArrayList();

    public Control() {
        tanks = new ArrayList<>();
        tanks.add(new RandomTank());
        tanks.add(new RandomTank());
        tanks.add(new RandomTank());
        tanks.add(new RandomTank());
        
        highlightTanks.add(tanks.get(0));
        
        for (Tank t : tanks) {
            score.put(t, 0);
        }
    }
    
    private void play() {
        for (int i = 1; i <= ROUND_COUNT; i++) {
            playRound();
            if (i % 10 == 0) printScore("Score after "+i+" rounds");
        }
    }
    
    public void printScore(String header) {
        ArrayList<Map.Entry<Tank, Integer>> values = new ArrayList(score.entrySet());
        Collections.sort(values, new Comparator<Map.Entry<Tank, Integer>>() {
            @Override
            public int compare(Map.Entry<Tank, Integer> o1, Map.Entry<Tank, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });
        
        System.out.println();
        System.out.println(header);
        for (Map.Entry<Tank, Integer> v : values) {
            if (highlightTanks.contains(v.getKey())) {
                System.out.println(v.getValue()+"  " + v.getKey().toString()+"*");
            } else {
                System.out.println(v.getValue()+"  " + v.getKey().toString());
            }
        }
    }
    
    private FieldPoint getRandomEmpty(Field f, int xFilter, int yFilter) {
        FieldPoint p = new FieldPoint(0, 0);
        for (int i = 0; i < 100; i++) {
            p.setLocation(rnd.nextInt(FIELD_WIDTH), rnd.nextInt(FIELD_HEIGHT));
            if (f.get(p) == null && p.x % 2 != xFilter && p.y % 2 != yFilter) return p;
        }
        return null;
    }
    private boolean playRound() {
        ArrayList<Hole> holes = new ArrayList();
        ArrayList<Tank> turnTanks = new ArrayList(tanks);
        Collections.shuffle(turnTanks);
        Field field = new Field();
        HashMap<Tank, TankState> tankMap = new HashMap();
        ArrayList<FieldObject> highlightObjects = new ArrayList();
        int movableWallCount = 100;//rnd.nextInt(FIELD_WIDTH * FIELD_HEIGHT / 10);
        int breakableWallCount = 50;
        int holeCount = 50;
        
        for (Tank t : turnTanks) {
            FieldPoint p = getRandomEmpty(field, -1, -1);
            if (p == null) return false;
            FieldTank fTank = new FieldTank(p, TANK_HEALTH, Direction.values()[rnd.nextInt(Direction.values().length)]);
            TankState state = new TankState(fTank, false);
            tankMap.put(t, state);
            field.put(p, fTank);
        }
        
        for (Tank t : highlightTanks) {
            highlightObjects.add(tankMap.get(t).tank);
        }
        
        int offsetX = rnd.nextInt(2);
        int offsetY = rnd.nextInt(2);
        for (int i = 0; i < movableWallCount; i++) {
            FieldPoint p = getRandomEmpty(field, offsetX, offsetY);
            if (p == null) continue;
            field.put(p, new MovableWall(p));
        }
        
        for (int i = 0; i < breakableWallCount; i++) {
            FieldPoint p = getRandomEmpty(field, -1, -1);
            if (p == null) continue;
            field.put(p, new BreakableWall(p, BREAKABLE_WALL_HEALTH));
        }
        
        for (int i = 0; i < holeCount; i++) {
            FieldPoint p = getRandomEmpty(field, -1, -1);
            if (p == null) continue;
            Hole h = new Hole(p);
            field.put(p, h);
            holes.add(h);
        }
        
        ArrayList<Tank> lastRoundTanks = new ArrayList();
        while (turnTanks.size() > 1) {
            lastRoundTanks.clear();
            lastRoundTanks.addAll(turnTanks);
            HashMap<FieldTank, Action> actions = new HashMap();
            for (Tank t : turnTanks) {
                Field fieldCopy = new Field(field);
                TankState stateCopy = new TankState(tankMap.get(t), fieldCopy);
                actions.put(tankMap.get(t).tank, t.getAction(fieldCopy, stateCopy));
            }
            for (int i = 0; i < turnTanks.size(); i++) {
                Tank t = turnTanks.get(i);
                TankState s = tankMap.get(t);
                Action a = actions.get(s.tank);
                a.doAction(field, s);
            }
            
            ArrayList<FieldObject> deadObjects = new ArrayList();
            for (FieldObject f : field.getAllObjects()) {
                if (f.health <= 0 || !field.isOnField(f.position)) {
                    deadObjects.add(f);
                }
            }
            for (Hole h : holes) {
                FieldObject obj = field.get(h.position);
                if (obj != null && !(obj instanceof Hole)) {
                    deadObjects.add(obj);
                }
            }
            for (FieldObject f : deadObjects) {
                field.remove(f);
                if (f instanceof FieldTank) {
                    for (Tank t : turnTanks) {
                        TankState s = tankMap.get(t);
                        if (s.tank == f) {
                            turnTanks.remove(t);
                            break;
                        }
                    }
                }
            }
            for (Hole h : holes) {
                field.put(h.position, h);
            }
            
            if (useUI) {
                panel.field = field;
                panel.deadObjects = deadObjects;
                panel.actions = actions;
                highlightObjects.removeAll(deadObjects);
                panel.highlight = highlightObjects;
                panel.repaint();
                try {
                    Thread.sleep(SLEEP);
                } catch (InterruptedException ex) {
                    
                }
            }
            
        }
        
        if (turnTanks.isEmpty()) {
            for (Tank t : lastRoundTanks) {
                score.put(t, score.get(t)+1);
            }
        } else {
            for (Tank t : turnTanks) {
                score.put(t, score.get(t)+1);
            }
        }
        return true;
    }
    
    private class FieldPanel extends JPanel {
        public Field field = null;
        public ArrayList<FieldObject> highlight = null;
        public ArrayList<FieldObject> deadObjects = null;
        public HashMap<FieldTank, Action> actions = null;

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            int cellX = getWidth() / (FIELD_WIDTH+2);
            int cellY = getHeight() / (FIELD_HEIGHT+2);
            if (cellX == 0 || cellY == 0) return;
            
            g2d.translate(cellX, cellY);
            g2d.setPaint(Color.WHITE);
            g2d.fillRect(0, 0, cellX * FIELD_WIDTH+1, cellY * FIELD_HEIGHT+1);

            Shape clip = g2d.getClip();

            if (null == field || field.getAllObjects().isEmpty()) return;
            ArrayList<FieldTank> tanks = new ArrayList();
            for (FieldObject obj : field.getAllObjects()) {
                if (obj instanceof FieldTank) tanks.add((FieldTank)obj);
                paintObject((Graphics2D)g, obj, cellX, cellY, null);
            }
            
            for (FieldObject obj : tanks) {
                paintObject((Graphics2D)g, obj, cellX, cellY, null);
            }
            for (FieldObject obj : deadObjects) {
                paintObject((Graphics2D)g, obj, cellX, cellY, Color.RED);
            }
            for (FieldObject obj : highlight) {
                paintObject((Graphics2D)g, obj, cellX, cellY, Color.MAGENTA);
            }
            
            g2d.setClip(clip);
            g2d.translate(-cellX, -cellY);
        }
        
        private void paintObject(Graphics2D g2d, FieldObject obj, int cellX, int cellY, Color c) {
            int x = obj.position.x;
            int y = obj.position.y;
            if (c == null) {
                switch (obj.type) {
                    case TYPE_HOLE:
                        c = Color.BLACK;
                        break;
                    case TYPE_TANK:
                        c = Color.BLUE;
                        break;
                    case TYPE_MOVABLE_WALL:
                        c = Color.LIGHT_GRAY;
                        break;
                    case TYPE_BREAKABLE_WALL:
                        c = Color.YELLOW.darker();
                        break;
                }
            }
            g2d.setPaint(c);
            g2d.fillRect(x * cellX, (FIELD_HEIGHT - y-1) * cellY, cellX, cellY);
            //g2d.setColor(c.ORANGE);
            //g2d.drawRect(x * cellX, (FIELD_HEIGHT - y) * cellY, cellX, cellY);
            if (obj instanceof FieldTank) {
                Direction d = ((FieldTank)(obj)).direction;
                FieldPoint p = d.getFieldPoint(obj.position);
                g2d.setColor(c.darker());
                g2d.drawLine(x * cellX+cellX/2, (FIELD_HEIGHT - y-1) * cellY+cellY/2,
                             p.x * cellX+cellX/2, (FIELD_HEIGHT - p.y-1) * cellY+cellY/2);
                
                Action a = actions.get((FieldTank)obj);
                if (a == Action.SHOOT) {
                    p = null;
                    ArrayList<FieldObject> objs = field.get(obj.position, d);
                    objs.addAll(deadObjects);
                    Collections.sort(objs, FieldObject.getDistanceComparator(obj.position));
                    for (FieldObject fObject : objs) {
                        if (!d.isDirectionOf(obj.position, fObject.position) 
                                || fObject instanceof Hole) continue;
                        p = fObject.position;
                        break;
                    }
                    if (p == null) {
                        p = d.getFieldPoint(obj.position, Math.max(FIELD_HEIGHT, FIELD_WIDTH));
                    }
                    g2d.setPaint(Color.red);
                    g2d.drawLine(x * cellX+cellX/2, (FIELD_HEIGHT - y-1) * cellY+cellY/2,
                                 p.x * cellX+cellX/2, (FIELD_HEIGHT - p.y-1) * cellY+cellY/2);
                }
            }
        }
        
    };
    private final FieldPanel panel = new FieldPanel();
    
    public static final void main(String[] args) {
        final Control c = new Control();
        if (useUI) {
            final JFrame frame = new JFrame();
            frame.add(c.panel);
            frame.setSize(FIELD_WIDTH*20, FIELD_HEIGHT*20);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(useUI);
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                c.play();
            }
        });
        t.start();
        
    }
}
