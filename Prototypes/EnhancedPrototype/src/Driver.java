

import java.util.ArrayList;

import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;

public class Driver {
	private DifferentialPilot pilot;
	final int DEFAULT_RADIUS = 90;
	final int FORWARD_ANGLE = 1;
	final int BACKWARD_ANGLE = -1;
	final int COMMAND_TYPE_INDEX = 0;
	final int PARAMETER1_INDEX = 1;
	final int PARAMETER2_INDEX = 2;
	final int PARAMETER3_INDEX = 3;
	final int PARAMETER4_INDEX = 4;

	public Driver() {
		pilot = new DifferentialPilot(2.25f, 5.5f, Motor.B, Motor.C);
	}

	public ArrayList<String> implementCommand(ArrayList<String> command) {
		if (command.get(COMMAND_TYPE_INDEX).equalsIgnoreCase("straight")) {
			boolean forward;
			if (command.get(PARAMETER1_INDEX).equalsIgnoreCase("forward")) {
				forward = true;
			} else {
				forward = false;
			}
			int distance = Integer.parseInt(command.get(PARAMETER2_INDEX));
			moveStraight(forward, distance);
			return new ArrayList<String>();
		} else if (command.get(COMMAND_TYPE_INDEX).equalsIgnoreCase("turn")) {
			boolean right;
			if (command.get(PARAMETER1_INDEX).equalsIgnoreCase("right")) {
				right = true;
			} else {
				right = false;
			}
			int radius = Integer.parseInt(command.get(PARAMETER2_INDEX));
			turn(right, radius);
			return new ArrayList<String>();
		} else if (command.get(COMMAND_TYPE_INDEX).equalsIgnoreCase("stop")) {
			stop();
			return new ArrayList<String>();
		} else if (command.get(COMMAND_TYPE_INDEX).equalsIgnoreCase("arc")) {
			boolean forward;
			if (command.get(PARAMETER1_INDEX).equalsIgnoreCase("forward")) {
				forward = true;
			} else {
				forward = false;
			}
			boolean right;
			if(command.get(PARAMETER2_INDEX).equalsIgnoreCase("right")){
				right = true;
			} else {
				right = false;
			}
			int distance = Integer.parseInt(command.get(PARAMETER3_INDEX));
			int radius = Integer.parseInt(command.get(PARAMETER4_INDEX));
			moveArc(forward, right, distance, radius);
			return new ArrayList<String>();
		} else {
			noOp();
			return new ArrayList<String>();
		}
	}

	private boolean moveStraight(boolean forward, int distance) {
		if (forward) {
			if (distance == 0) {
				pilot.forward();
				return true;
			} else {
				pilot.travel(distance);
				return true;
			}
		} else {
			if (distance == 0) {
				pilot.backward();
				return true;
			} else {
				pilot.travel(-distance);
				return true;
			}
		}
	}

	private boolean moveArc(boolean forward, boolean right, int distance,
			int radius) {

		if (forward) {
			if (right) {
				if (distance == 0 && radius == 0) {
					pilot.arcForward(-DEFAULT_RADIUS);
					return true;
				} else {
					pilot.arc(-radius, FORWARD_ANGLE);
					return true;
				}
			} else {
				if (distance == 0 && radius == 0) {
					pilot.arcForward(DEFAULT_RADIUS);
					return true;
				} else {
					pilot.arc(radius, FORWARD_ANGLE);
					return true;
				}
			}
		} else {
			if (right) {
				if (distance == 0 && radius == 0) {
					pilot.arcBackward(-DEFAULT_RADIUS);
					return true;
				} else {
					pilot.arc(-radius, BACKWARD_ANGLE);
					return true;
				}
			}else{
				if(distance == 0 && radius == 0){
					pilot.arcBackward(DEFAULT_RADIUS);
					return true;
				} else {
					pilot.arc(radius, BACKWARD_ANGLE);
					return true;
				}
			}
		}
	}

	private boolean turn(boolean right, int radius) {
		if (right) {
			if (radius == 0) {
				pilot.rotateRight();
				return true;
			} else {
				pilot.rotate(radius);
				return true;
			}
		} else {
			if (radius == 0) {
				pilot.rotateLeft();
				return true;
			} else {
				pilot.rotate(-radius);
				return true;
			}
		}
	}

	private boolean stop() {
		pilot.stop();
		return true;
	}

	private boolean noOp() {
		return true;
	}

	private String[] read(int sensorNumber) {
		return new String[1];
	}

}
