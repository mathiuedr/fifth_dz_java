import java.util.*;

public class Elevator implements Runnable {
    private static int counter=0;
    private final int id;
    private int direction;
    private int currentFloor = 0;
    private NavigableSet<Integer> StoppingPoints;
    public Map<Integer, NavigableSet<Integer>> DirectedStoppingPoints = new LinkedHashMap<Integer, NavigableSet<Integer>>();

    /**
     * Initialize elevator
     */
    public Elevator() {
        this.id = counter;
        counter++;
        Administrator.updateElevatorLists(this);
    }

    public int getId() {
        return id;
    }

    public int getDirection() {
        return direction;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }


    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    /**
     * Main method of elevator. Works in cycle and maintains the moving of elevator
     */
    public void move() {
        synchronized (Administrator.getInstance()) {
            Iterator<Integer> iter = DirectedStoppingPoints.keySet().iterator();

            if (iter.hasNext() && StoppingPoints == null) {
                direction = iter.next();
                StoppingPoints = DirectedStoppingPoints.get(direction);
                iter.remove();
                return;
            }
            if (StoppingPoints == null) return;

            if (iter.hasNext() && StoppingPoints.isEmpty()) {
                direction = iter.next();
                StoppingPoints = DirectedStoppingPoints.get(direction);
                iter.remove();
                return;
            }

            Integer currentFloor = null;
            Integer nextFloor = null;

            if (!StoppingPoints.isEmpty()) {

                if (direction == 1) {
                    currentFloor = StoppingPoints.pollFirst();
                    nextFloor = StoppingPoints.higher(currentFloor);

                } else if (direction == -1) {
                    currentFloor = StoppingPoints.pollLast();
                    nextFloor = StoppingPoints.lower(currentFloor);
                } else {
                    return;
                }

                setCurrentFloor(currentFloor);

                if (nextFloor != null) {
                    generateInBetweenFloors(currentFloor, nextFloor);
                } else {
                    setDirection(0);
                    Administrator.updateElevatorLists(this);
                }

                System.out.println(this);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }


        }

    }

    /**
     * Generates floors that are between initial and target floors
     */
    private void generateInBetweenFloors(int initial, int target) {
        if (Math.abs(target - initial) < 2) return;
        int direction = target - initial >= 0 ? 1 : -1;
        while (initial != target) {
            initial += direction;
            if (!StoppingPoints.contains(initial)) {
                StoppingPoints.add(initial);
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            move();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    @Override
    public String toString() {
        String[] state= new String[]{"Идет вниз","Стоит","Идет вверх"};

        return "Лифт " +
                id +
                " " + state[direction +1] +
                ", текущий этаж=" + currentFloor;
    }
}
