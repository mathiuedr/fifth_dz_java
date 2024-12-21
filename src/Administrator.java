import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

public final class Administrator implements Runnable {
    private static final Map<Integer, Elevator> MovingUp = new HashMap<>();
    private static final Map<Integer, Elevator> MovingDown = new HashMap<>();
    private static final Administrator instance = new Administrator();

    /**
     * Initializing constructor
     */
    private Administrator() {
        initializeElevators();
    }

    /**
     * Get an instance of singleton administrator
     */
    public static Administrator getInstance() {
        return instance;
    }

    /**
     * Select elevator to do the request
     * @param peopleFloor floor where elevator was called
     * @param targetFloor floor to go to
     */
    public synchronized void selectElevator(int peopleFloor, int targetFloor) {
        findElevator(getElevatorDirection(peopleFloor, targetFloor), peopleFloor, targetFloor);
    }

    /**
     * initialize elevators threads
     */
    private static void initializeElevators() {
        int elevatorsCount = 5;
        for (int i = 0; i < elevatorsCount; i++) {
            Elevator elevator = new Elevator();
            Thread t = new Thread(elevator);
            t.start();
        }
    }

    /**
     * Get the direction of moving of request
     * @return integer 1 for moving up or -1 for moving down
     */
    private static int getElevatorDirection(int initial_floor, int go_to_floor) {
        return go_to_floor > initial_floor ? 1 : -1;
    }

    /**
     * Fill the key map with distances and elevator ids
     */
    private static void FillKeyMapByDirection(int direction, TreeMap<Integer, Integer> keymap, int requestedFloor) {
        Map<Integer, Elevator> selectedMap = direction == 1 ? MovingUp : MovingDown;
        for (Elevator elv : selectedMap.values()) {
            int distance = (requestedFloor - elv.getCurrentFloor()) * direction;
            if (!(distance < 0 && elv.getDirection() == direction)) {
                keymap.put(Math.abs(distance), elv.getId());
            }
        }
    }

    /**
     * Find the closest to request elevator and pass the task to it
     */
    private static void findElevator(int elevatorDirection, int peopleFloor, int targetFloor) {
        Elevator elevator = null;


        TreeMap<Integer, Integer> sortedKeyMap = new TreeMap<Integer, Integer>();

        if (elevatorDirection == 1) {

            FillKeyMapByDirection(1, sortedKeyMap, peopleFloor);
            Integer selectedElevatorId = sortedKeyMap.firstEntry().getValue();
            elevator = MovingUp.get(selectedElevatorId);


        } else if (elevatorDirection == -1) {
            FillKeyMapByDirection(-1, sortedKeyMap, peopleFloor);
            Integer selectedElevatorId = sortedKeyMap.firstEntry().getValue();
            elevator = MovingDown.get(selectedElevatorId);
        }

        int ToPeopleDirection = getElevatorDirection(elevator.getCurrentFloor(), peopleFloor);
        int FromPeopleToTargetDirection = getElevatorDirection(peopleFloor, targetFloor);

        NavigableSet<Integer> floorsToPeople = elevator.DirectedStoppingPoints.get(ToPeopleDirection);
        if (floorsToPeople == null) {
            floorsToPeople = new ConcurrentSkipListSet<Integer>();
        }

        floorsToPeople.add(elevator.getCurrentFloor());
        floorsToPeople.add(peopleFloor);
        elevator.DirectedStoppingPoints.put(ToPeopleDirection, floorsToPeople);

        NavigableSet<Integer> floorsToTarget = elevator.DirectedStoppingPoints.get(FromPeopleToTargetDirection);
        if (floorsToTarget == null) {
            floorsToTarget = new ConcurrentSkipListSet<Integer>();
        }

        floorsToTarget.add(peopleFloor);
        floorsToTarget.add(targetFloor);
        elevator.DirectedStoppingPoints.put(FromPeopleToTargetDirection, floorsToTarget);
    }

    /**
     * Update maps with state of all elevators (if elevator isn`t moving it`s going to be in all 2 maps)
     * @param elevator elevator to be updated
     */
    public static synchronized void updateElevatorLists(Elevator elevator) {
        if (elevator.getDirection() == 1) {
            MovingUp.put(elevator.getId(), elevator);
            MovingDown.remove(elevator.getId());
        } else if (elevator.getDirection() == -1) {
            MovingDown.put(elevator.getId(), elevator);
            MovingUp.remove(elevator.getId());
        } else {
            MovingUp.put(elevator.getId(), elevator);
            MovingDown.put(elevator.getId(), elevator);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }


}
