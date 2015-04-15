
package futuretank;

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
import futuretank.tank.RandomTank;
import java.awt.Point;
import java.util.HashSet;

public final class Control {

    private final ArrayList<Tank> all_tanks = new ArrayList();
    {
        all_tanks.add(new RandomTank());
        all_tanks.add(new RandomTank());
        all_tanks.add(new RandomTank());
        all_tanks.add(new RandomTank());
        
    }
    
    private final Random rnd = new Random();
    
    public static final boolean useUI = true;
    public static final int FIELD_WIDTH = 12;
    public static final int FIELD_HEIGHT = 12;
    public static final int TANK_HEALTH = 5;
    public static final int ROUND_COUNT = 100;
    public static final long SLEEP = 500;
    
    private final HashMap<Tank, Integer> score = new HashMap();
    private final ArrayList<Tank> highlightTanks = new ArrayList();

    
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
                c.playTournament();
            }
        });
        t.start();
        
    }
    
    public Control() {
        
        highlightTanks.add(all_tanks.get(0));
        
        for (Tank t : all_tanks) {
            score.put(t, 0);
        }
    }
    
    private void playTournament() {
        for (int i = 1; i <= ROUND_COUNT; i++) {
            playMatch(new ArrayList(all_tanks));
            if (i % 10 == 0) printScore("Score after "+i+" matches");
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
    
    private boolean playMatch(ArrayList<Tank> tanks) {
        Field field = new Field();
        panel.clear();
        
        for (Tank t : tanks) {
            Point p = new Point();
            while (true) {
                p.setLocation(rnd.nextInt(FIELD_WIDTH), rnd.nextInt(FIELD_HEIGHT));
                if (field.getTank(p) == null) {
                    t.direction = rnd.nextInt(4);
                    t.reset();
                    field.put(t, p);
                    break;
                }
            }
        }
        
        final HashMap<Tank, Action[]> actions = new HashMap();
        final HashMap<Tank, Long> times = new HashMap();
        while (tanks.size() > 1) {
            int act_count = 4;
            
            for (Tank t : tanks) {
                Field fieldCopy = field.clone();
                Action acts[] = new Action[act_count];
                long start = System.nanoTime();
                t.getActions(fieldCopy, acts);
                times.put(t, System.nanoTime() - start);
                actions.put(t, acts);
            }
            
            Collections.sort(tanks, new Comparator<Tank>() {
                @Override
                public int compare(Tank o1, Tank o2) {
                    return (int)(times.get(o1) - times.get(o2));
                }
            });
            
            ArrayList<Tank> deadTanks;
            for (int i = 0; i < act_count; i++) {
                for (Tank t : tanks) {
                    Action a[] = actions.get(t);
                    field.doAction(t, a[i]);
                }
                if (i == act_count-1) {
                    deadTanks = field.doBombs();
                } else {
                    deadTanks = field.doHoles();
                }
                tanks.removeAll(deadTanks);
                
                if (useUI) {
                    panel.update(field, deadTanks, actions, i);
                    panel.repaint();
                    try {
                        Thread.sleep(SLEEP);
                    } catch (InterruptedException ex) {

                    }
                }
            }
        }
        
        for (Tank t : tanks) {
            score.put(t, score.get(t)+1);
        }
        return true;
    }
    
    private class FieldPanel extends JPanel {
        public Field lastField = null;
        public HashMap<Point, Tank> deadTanks = new HashMap();
        public HashSet<Point> deadPoints = new HashSet();
        public HashMap<Tank, Action> actions = new HashMap();
        int cellX;
        int cellY;
        
        public void clear() {
            lastField = null;
        }

        public void update(Field field, ArrayList<Tank> deadTanks, HashMap<Tank, Action[]> acts, int index) {
            deadPoints.clear();
            actions.clear();
            this.deadTanks.clear();
            if (lastField == null) {
                lastField = field;
            }
            
            for (Tank t : lastField.getTanks()) {
                actions.put(t, acts.get(t)[index]);
            }
            
            for (Point p : lastField.getPoints()) {
                if (field.getObject(p) == null) {
                    deadPoints.add(p);
                }
            }
            
            for (Tank t : deadTanks) {
                this.deadTanks.put(t.position, t);
            }
            lastField = field.clone();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            cellX = getWidth() / (FIELD_WIDTH+2);
            cellY = getHeight() / (FIELD_HEIGHT+2);
            if (cellX == 0 || cellY == 0) return;
            
            g2d.translate(cellX, cellY);
            g2d.setPaint(Color.WHITE);
            g2d.fillRect(0, 0, cellX * FIELD_WIDTH+1, cellY * FIELD_HEIGHT+1);

            Shape clip = g2d.getClip();
            
            if (lastField == null) return;
            
            for (Tank t : lastField.getTanks()) {
                if (highlightTanks.indexOf(t) != -1) {
                    paintTank(g2d, t, Color.MAGENTA.darker());
                } else {
                    paintTank(g2d, t, Color.BLUE.darker());
                }
            }
            
            for (Point p : lastField.getPoints()) {
                paintObject(g2d, lastField.getObject(p), p);
            }
            
            for (Tank t : deadTanks.values()) {
                paintTank(g2d, t, Color.RED);
            }
            
            for (Point p : deadPoints) {
                paintObject(g2d, null, p);
            }
            
            g2d.setClip(clip);
            g2d.translate(-cellX, -cellY);
        }
        
        private void paintObject(Graphics2D g2d, ObjectType type, Point p) {
            if (type == null) {
                g2d.setPaint(Color.RED);
                g2d.fillRect(p.x * cellX, (FIELD_HEIGHT - p.y-1) * cellY, cellX, cellY);
                return;
            }
            switch (type) {
                case HOLE:
                    g2d.setPaint(Color.BLACK);
                    g2d.fillRect(p.x * cellX, (FIELD_HEIGHT - p.y-1) * cellY, cellX, cellY);
                    break;
                case BOMB:
                    g2d.setPaint(Color.YELLOW.darker());
                    g2d.fillOval(p.x * cellX, (FIELD_HEIGHT - p.y-1) * cellY, cellX, cellY);
                    return;
                case WALL:
                    g2d.setPaint(Color.LIGHT_GRAY);
                    g2d.fillRect(p.x * cellX, (FIELD_HEIGHT - p.y-1) * cellY, cellX, cellY);
                    break;
            }
        }
        
        private void paintTank(Graphics2D g2d, Tank t, Color c) {
            int d = t.direction;
            Point p1 = new Point(t.position);
            Point p2 = Field.movePoint(new Point(p1), d);
            if (t.charged) {
                c = c.brighter().brighter();
            }
            g2d.setPaint(c);
            g2d.fillRect(p1.x * cellX, (FIELD_HEIGHT - p1.y-1) * cellY, cellX, cellY);
            g2d.setColor(c.darker());
            g2d.drawLine(p2.x * cellX+cellX/2, (FIELD_HEIGHT - p2.y-1) * cellY+cellY/2,
                         p1.x * cellX+cellX/2, (FIELD_HEIGHT - p1.y-1) * cellY+cellY/2);

            Action a = actions.get(t);
            if (a == Action.SHOOT) {
                while (Field.isOnField(p1)) {
                    Field.movePoint(p1, d);
                    if (deadPoints.contains(p1) || lastField.getTank(p1) != null || deadTanks.get(p1) != null) {
                        break;
                    }
                }
                g2d.setPaint(Color.RED);
                g2d.drawLine(p2.x * cellX+cellX/2, (FIELD_HEIGHT - p2.y-1) * cellY+cellY/2,
                             p1.x * cellX+cellX/2, (FIELD_HEIGHT - p1.y-1) * cellY+cellY/2);
            }
        }
        
    };
    private final FieldPanel panel = new FieldPanel();
    
}
