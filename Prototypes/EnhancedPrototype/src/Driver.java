import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.SoundSensor;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
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

	private TouchSensor touchSensor;
	private UltrasonicSensor ultrasonicSensor;
	private LightSensor lightSensor;
	private SoundSensor soundSensor;

	public Driver() {
		pilot = new DifferentialPilot(2.25f, 5.5f, Motor.B, Motor.C);

		touchSensor = new TouchSensor(SensorPort.S1);
		ultrasonicSensor = new UltrasonicSensor(SensorPort.S2);
		lightSensor = new LightSensor(SensorPort.S3);
		soundSensor = new SoundSensor(SensorPort.S4);
	}

	public ArrayList<String> implementCommand(ArrayList<String> command) {
		boolean forward, right;
		int radius, distance;
		String sensor;
		switch(command.get(COMMAND_TYPE_INDEX)){
		case "straight":
			if (command.get(PARAMETER1_INDEX).equalsIgnoreCase("forward")) {
				forward = true;
			} else {
				forward = false;
			}
			distance = Integer.parseInt(command.get(PARAMETER2_INDEX));
			moveStraight(forward, distance);
			return new ArrayList<String>();
		case "turn":
			if (command.get(PARAMETER1_INDEX).equalsIgnoreCase("right")) {
				right = true;
			} else {
				right = false;
			}
			radius = Integer.parseInt(command.get(PARAMETER2_INDEX));
			turn(right, radius);
			return new ArrayList<String>();
		case "stop":
			stop();
			return new ArrayList<String>();
		case "arc":
			if (command.get(PARAMETER1_INDEX).equalsIgnoreCase("forward")) {
				forward = true;
			} else {
				forward = false;
			}
			if (command.get(PARAMETER2_INDEX).equalsIgnoreCase("right")) {
				right = true;
			} else {
				right = false;
			}
			distance = Integer.parseInt(command.get(PARAMETER3_INDEX));
			radius = Integer.parseInt(command.get(PARAMETER4_INDEX));
			moveArc(forward, right, distance, radius);
			return new ArrayList<String>();
		case "readsensor":
			sensor = command.get(PARAMETER1_INDEX);
			return readSensor(sensor);

		default:
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
			} else {
				if (distance == 0 && radius == 0) {
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

	private ArrayList<String> readSensor(String sensorType) {
		ArrayList<String> returnList = new ArrayList<String>();
		returnList.add(sensorType);
		switch (sensorType) {
		case "touch":
			if(touchSensor.isPressed())
				returnList.add("pressed");
			else
				returnList.add("notpressed");
			break;
		case "ultrasonic":
			returnList.add(Integer.toString(ultrasonicSensor.getDistance()));
			break;
		case "sound":
			returnList.add(Integer.toString(soundSensor.readValue()));
			break;
		case "light":
			returnList.add(Integer.toString(lightSensor.getNormalizedLightValue()));
			break;
		default:
			return new ArrayList<String>();
		}
		return returnList;
	}

}
