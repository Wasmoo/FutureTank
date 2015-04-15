package futuretank.tank;

import futuretank.Tank;
import futuretank.Action;
import futuretank.Field;

public class RandomTank extends Tank {

    @Override
    public void getActions(Field field, Action actions[]) {
        for (int i = 0; i < actions.length; i++) {
            do {
                Action a = Action.values()[(int) (Math.random() * Action.values().length)];
                if (field.doAction(this, a)) {
                    actions[i] = a;
                    break;
                }
            } while (true);
        }
    }

    @Override
    public String toString() {
        return "Random Tank";
    }
}
