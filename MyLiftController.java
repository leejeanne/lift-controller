package lift;

/**
 * leejeanne
 * 21 Nov 2014
 */
public class MyLiftController implements LiftController {

	private int startUp[] = new int[Main.NUMBER_FLOORS]; // keeps track of # of callLifts at each floor going UP
	private int startDown[] = new int[Main.NUMBER_FLOORS]; // keeps track of # of callLifts at each floor going DOWN
	private int end[] = new int[Main.NUMBER_FLOORS]; // keeps track of # of selectFloors for each floor UP
	private int currentFloor = -1; // lift location
	private Direction currentDirection = Direction.UNSET;
	
    /* Interface for People */
    public synchronized void callLift(int floor, Direction direction) throws InterruptedException {
    	
    	// if the person thread presses the up button, add value to array
    	// array will keep track of the number of callLift at each floor going in up direction
    	
    	if (direction == Direction.UP) {
    		startUp[floor]++;
    	} else {
			startDown[floor]++;
    	}
    	
    	while(currentFloor != floor || currentDirection != direction) {
    		wait();
    	}
    	
    	// once the thread has been notified by doorsOpen, decrement the array to signify
    	// the person threads have entered the lift
    	
    	if (direction == Direction.UP) {
    		startUp[floor]--;
    	} else {
			startDown[floor]--;
    	}
    	
    	notifyAll();
    	
    }

    public synchronized void selectFloor(int floor) throws InterruptedException{
    	
    	// person thread selects floor and array end keeps track of their end destination
    	end[floor]++;
    	
    	// thread must wait in the lift until the lift arrives at their end destination floor
    		
    	while (currentFloor != floor) {
    		 wait();
    	}
    		
		end[floor]--;
		notifyAll();
    }

    
    /* Interface for Lifts */
    public synchronized boolean liftAtFloor(int floor, Direction direction) {
    	currentFloor = floor;
    	currentDirection = direction;
    	
    	// if there is a person thread waiting to get off at the floor OR
    	// the elevator is moving in the right direction and the person is waiting at the floor
    	// return TRUE so the lift doors open
    	
    		
    	if (end[floor] != 0 || (direction == Direction.UP && startUp[floor] != 0) 
    			|| (direction == Direction.DOWN && startDown[floor] != 0)) {
    		return true;
    		} else {
    			return false;
    	}
    	
    }

    public synchronized void doorsOpen(int floor) throws InterruptedException {
    	notifyAll();
    	
			while (end[floor] != 0) { 
				wait();
			}
			if (currentDirection == Direction.UP) {
				while (startUp[floor] != 0) {
					wait();
				}
			} else if (currentDirection == Direction.DOWN) {
				while (startDown[floor] != 0) {
					wait();
				}
			}
    }
    

    public synchronized void doorsClosed(int floor) {
    }

}
