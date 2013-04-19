import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import lejos.nxt.Sound;
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
	final int SAFEDISTANCE = 50;
	final int SOUNDTHRESHOLD = 50;
	private boolean DRIVING = false;
	private boolean hasStopped = false;

	private TouchSensor touchSensor;
	private UltrasonicSensor ultrasonicSensor;
	private LightSensor lightSensor;
	private SoundSensor soundSensor;

	public Driver() {
		pilot = new DifferentialPilot(2.25f, 5.5f, Motor.B, Motor.C);
		pilot.setRotateSpeed(90);

		touchSensor = new TouchSensor(SensorPort.S1);
		ultrasonicSensor = new UltrasonicSensor(SensorPort.S2);
		lightSensor = new LightSensor(SensorPort.S3);
		soundSensor = new SoundSensor(SensorPort.S4);

	}
	public ArrayList<String> safetySense()
	{
		if (hasStopped) {
			if (ultrasonicSensor.getDistance() > SAFEDISTANCE)
				hasStopped = false;
		} else if (!hasStopped && DRIVING
				&& (ultrasonicSensor.getDistance() < SAFEDISTANCE)) {
			Sound.twoBeeps();
			hasStopped = true;
			stop();
			return readSensor("ultrasonic");
		}
		if (DRIVING && (touchSensor.isPressed())) {
			Sound.beepSequence();
			stop();
			return readSensor("touch");
		}
		if (DRIVING && (soundSensor.readValue() > SOUNDTHRESHOLD)) {
			Sound.playNote(Sound.FLUTE, 130, 500);
			stop();
			return readSensor("sound");
		} else if (!DRIVING
				&& (soundSensor.readValue() > SOUNDTHRESHOLD)){
			moveStraight(false, 0);
			return readSensor("sound");
		}
		return new ArrayList<String>();
	}
	public ArrayList<String> implementCommand(ArrayList<String> command) {
		boolean forward, right, travel;
		int radius, distance, speed;
		String sensor, motor;
		switch (command.get(COMMAND_TYPE_INDEX)) {
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
		case "setspeed":
			motor = command.get(PARAMETER1_INDEX);
			if (command.get(PARAMETER2_INDEX).equalsIgnoreCase("travel"))
				travel = true;
			else
				travel = false;
			speed = Integer.parseInt(command.get(PARAMETER2_INDEX));
			setSpeed(motor, travel, speed);
			return new ArrayList<String>();
		default:
			noOp();
			return new ArrayList<String>();
		}

	}

	private boolean setSpeed(String motor, boolean travel, int speed) {
		switch (motor) {
		case "motora":
			return true;
		case "motorb":
			return true;
		case "motorc":
			return true;
		case "drivemotors":
			if (travel)
				pilot.setTravelSpeed(speed);
			else
				pilot.setRotateSpeed(speed);
			System.out.println("speed set to " + speed);
			return true;
		default:
			return false;
		}
	}

	private boolean moveStraight(boolean forward, int distance) {
		DRIVING = true;
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
		DRIVING = true;
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
		DRIVING = true;
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
		DRIVING = false;
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
			if (touchSensor.isPressed())
				returnList.add("1");
			else
				returnList.add("0");
			break;
		case "ultrasonic":
			returnList.add(Integer.toString(ultrasonicSensor.getDistance()));
			break;
		case "sound":
			returnList.add(Integer.toString(soundSensor.readValue()));
			break;
		case "light":
			returnList.add(Integer.toString(lightSensor
					.getNormalizedLightValue()));
			break;
		default:
			return new ArrayList<String>();
		}
		return returnList;
	}

}
