import java.util.ArrayList;

import lejos.nxt.Button;
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
	private final int DEFAULT_RADIUS = 90;
	private final int FORWARD_ANGLE = 1;
	private final int BACKWARD_ANGLE = -1;
	private final int COMMAND_TYPE_INDEX = 0;
	private final int PARAMETER1_INDEX = 1;
	private final int PARAMETER2_INDEX = 2;
	private final int PARAMETER3_INDEX = 3;
	private final int PARAMETER4_INDEX = 4;
	private final int SAFEDISTANCE = 50;
	private final int SOUNDTHRESHOLD = 85;
	private final int INITIAL_ROTATE_SPEED = 90;
	private final int SWING_FORWARD_ANGLE = 90;
	private final int SWING_BACKWARD_ANGLE = -90;
	private final double WHEEL_DIAMETER = 2.25f;
	private final double TRACK_WIDTH = 5.5f;
	private boolean DRIVING = false;
	private boolean hasStopped = false;
	private boolean moveBreakpoint = false;
	private boolean moveForwardBreakpoint = false;
	private boolean moveBackwardBreakpoint = false;
	private boolean arcBreakpoint = false;
	private boolean arcForwardBreakpoint = false;
	private boolean arcBackwardBreakpoint = false;
	private boolean arcForwardRightBreakpoint = false;
	private boolean arcForwardLeftBreakpoint = false;
	private boolean arcBackwardRightBreakpoint = false;
	private boolean arcBackwardLeftBreakpoint = false;
	private boolean setspeedBreakpoint = false;
	private boolean readSensorBreakpoint = false;
	private boolean readSensorTouchBreakpoint = false;
	private boolean readSensorUltrasonicBreakpoint = false;
	private boolean readSensorLightBreakpoint = false;
	private boolean readSensorMicrophoneBreakpoint = false;
	private boolean turnBreakpoint = false;
	private boolean turnRightBreakpoint = false;
	private boolean turnLeftBreakpoint = false;
	private boolean swingBreakpoint = false;

	private TouchSensor touchSensor;
	private UltrasonicSensor ultrasonicSensor;
	private LightSensor lightSensor;
	private SoundSensor soundSensor;

	public Driver() {
		pilot = new DifferentialPilot(WHEEL_DIAMETER, TRACK_WIDTH, Motor.B,
				Motor.C);
		pilot.setRotateSpeed(INITIAL_ROTATE_SPEED);
		Motor.A.setSpeed(INITIAL_ROTATE_SPEED);

		touchSensor = new TouchSensor(SensorPort.S1);
		ultrasonicSensor = new UltrasonicSensor(SensorPort.S2);
		lightSensor = new LightSensor(SensorPort.S3);
		soundSensor = new SoundSensor(SensorPort.S4);

	}

	public ArrayList<String> safetySense() {
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
		} else if (!DRIVING && (soundSensor.readValue() > SOUNDTHRESHOLD)) {
			moveStraight(false, 0);
			return readSensor("sound");
		}
		return new ArrayList<String>();
	}

	public ArrayList<String> implementCommand(ArrayList<String> command) {
		boolean forward, right, setTravelSpeed;
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
				setTravelSpeed = true;
			else
				setTravelSpeed = false;
			speed = Integer.parseInt(command.get(PARAMETER2_INDEX));
			setSpeed(motor, setTravelSpeed, speed);
			return new ArrayList<String>();
		case "swing":
			swing();
			return new ArrayList<String>();
		case "breakpoint":
			implementBreakpoint(command);
			return new ArrayList<String>();
		case "variable":
			return implementVariable(command);
		default:
			noOp();
			return new ArrayList<String>();
		}

	}

	private boolean setSpeed(String motor, boolean setTravelSpeed, int speed) {
		if (setspeedBreakpoint) {
			System.out.println("Hit Breakpoint");
			Button.ENTER.waitForPressAndRelease();
		}
		switch (motor) {
		case "motora":
			Motor.A.setSpeed(speed);
			return true;
		case "motorb":
			Motor.B.setSpeed(speed);
			return true;
		case "motorc":
			Motor.C.setSpeed(speed);
			return true;
		case "drivemotors":
			if (setTravelSpeed)
				pilot.setTravelSpeed(speed);
			else
				pilot.setRotateSpeed(speed);
			System.out.println("speed set to " + speed);
			return true;
		default:
			return false;
		}
	}

	private boolean swing() {
		if (swingBreakpoint) {
			System.out.println("Hit Breakpoint");
			Button.ENTER.waitForPressAndRelease();
		}
		Motor.A.rotateTo(SWING_FORWARD_ANGLE);
		Motor.A.rotateTo(SWING_BACKWARD_ANGLE);
		return true;
	}

	private boolean moveStraight(boolean forward, int distance) {
		DRIVING = true;
		if (moveBreakpoint) {
			System.out.println("Hit Breakpoint");
			Button.ENTER.waitForPressAndRelease();
		}
		if (forward) {
			if (moveForwardBreakpoint) {
				System.out.println("Hit Breakpoint");
				Button.ENTER.waitForPressAndRelease();
			}
			if (distance == 0) {
				pilot.forward();
				return true;
			} else {
				pilot.travel(distance);
				return true;
			}
		} else {
			if (moveBackwardBreakpoint) {
				System.out.println("Hit Breakpoint");
				Button.ENTER.waitForPressAndRelease();
			}
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
		if (arcBreakpoint) {
			System.out.println("Hit Breakpoint");
			Button.ENTER.waitForPressAndRelease();
		}
		DRIVING = true;
		if (forward) {
			if (arcForwardBreakpoint) {
				System.out.println("Hit Breakpoint");
				Button.ENTER.waitForPressAndRelease();
			}
			if (right) {
				if (arcForwardRightBreakpoint) {
					System.out.println("Hit Breakpoint");
					Button.ENTER.waitForPressAndRelease();
				}
				if (distance == 0 && radius == 0) {
					pilot.arcForward(-DEFAULT_RADIUS);
					return true;
				} else {
					pilot.arc(-radius, FORWARD_ANGLE);
					return true;
				}
			} else {
				if (arcForwardLeftBreakpoint) {
					System.out.println("Hit Breakpoint");
					Button.ENTER.waitForPressAndRelease();
				}
				if (distance == 0 && radius == 0) {
					pilot.arcForward(DEFAULT_RADIUS);
					return true;
				} else {
					pilot.arc(radius, FORWARD_ANGLE);
					return true;
				}
			}
		} else {
			if (arcBackwardBreakpoint) {
				System.out.println("Hit Breakpoint");
				Button.ENTER.waitForPressAndRelease();
			}
			if (right) {
				if (arcBackwardRightBreakpoint) {
					System.out.println("Hit Breakpoint");
					Button.ENTER.waitForPressAndRelease();
				}
				if (distance == 0 && radius == 0) {
					pilot.arcBackward(-DEFAULT_RADIUS);
					return true;
				} else {
					pilot.arc(-radius, BACKWARD_ANGLE);
					return true;
				}
			} else {
				if (arcBackwardLeftBreakpoint) {
					System.out.println("Hit Breakpoint");
					Button.ENTER.waitForPressAndRelease();
				}
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
		if (turnBreakpoint) {
			System.out.println("Hit Breakpoint");
			Button.ENTER.waitForPressAndRelease();
		}
		DRIVING = true;
		if (right) {
			if (turnRightBreakpoint) {
				System.out.println("Hit Breakpoint");
				Button.ENTER.waitForPressAndRelease();
			}
			if (radius == 0) {
				pilot.rotateRight();
				return true;
			} else {
				pilot.rotate(radius);
				return true;
			}
		} else {
			if (turnLeftBreakpoint) {
				System.out.println("Hit Breakpoint");
				Button.ENTER.waitForPressAndRelease();
			}
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
		DRIVING = false;
		return true;
	}

	private boolean noOp() {
		return true;
	}

	private ArrayList<String> readSensor(String sensorType) {
		if (readSensorBreakpoint) {
			System.out.println("Hit Breakpoint");
			Button.ENTER.waitForPressAndRelease();
		}
		ArrayList<String> returnList = new ArrayList<String>();
		returnList.add(sensorType);
		switch (sensorType) {
		case "touch":
			if (readSensorTouchBreakpoint) {
				System.out.println("Hit Breakpoint");
				Button.ENTER.waitForPressAndRelease();
			}
			if (touchSensor.isPressed())
				returnList.add("1");
			else
				returnList.add("0");
			break;
		case "ultrasonic":
			if (readSensorUltrasonicBreakpoint) {
				System.out.println("Hit Breakpoint");
				Button.ENTER.waitForPressAndRelease();
			}
			returnList.add(Integer.toString(ultrasonicSensor.getDistance()));
			break;
		case "sound":
			if (readSensorMicrophoneBreakpoint) {
				System.out.println("Hit Breakpoint");
				Button.ENTER.waitForPressAndRelease();
			}
			returnList.add(Integer.toString(soundSensor.readValue()));
			break;
		case "light":
			if (readSensorLightBreakpoint) {
				System.out.println("Hit Breakpoint");
				Button.ENTER.waitForPressAndRelease();
			}
			returnList.add(Integer.toString(lightSensor
					.getNormalizedLightValue()));
			break;
		default:
			return new ArrayList<String>();
		}
		return returnList;
	}

	private void implementBreakpoint(ArrayList<String> command) {
		boolean breakpointValue;
		int lastIndex = command.size() - 1;
		switch (command.get(lastIndex)) {
		case "true":
			breakpointValue = true;
			break;
		default:
			breakpointValue = false;
			break;
		}
		if (command.size() > 2) {
			switch (command.get(PARAMETER1_INDEX)) {
			case "move":
				if (command.size() > 3) {
					switch (command.get(PARAMETER2_INDEX)) {
					case "forward":
						moveForwardBreakpoint = breakpointValue;
						break;
					case "backward":
						moveBackwardBreakpoint = breakpointValue;
						break;
					default:
						break;
					}
					break;
				}
				moveBreakpoint = breakpointValue;
				break;
			case "arc":
				if (command.size() > 3) {
					switch (command.get(PARAMETER2_INDEX)) {
					case "forward":
						if (command.size() > 4) {
							switch (command.get(PARAMETER3_INDEX)) {
							case "right":
								arcForwardRightBreakpoint = breakpointValue;
								break;
							case "left":
								arcForwardLeftBreakpoint = breakpointValue;
								break;
							default:
								break;
							}
							break;
						}
						arcForwardBreakpoint = breakpointValue;
						break;
					case "backward":
						if (command.size() > 4) {
							switch (command.get(PARAMETER3_INDEX)) {
							case "right":
								arcBackwardRightBreakpoint = breakpointValue;
								break;
							case "left":
								arcBackwardLeftBreakpoint = breakpointValue;
								break;
							default:
								break;
							}
							break;
						}
						arcBackwardBreakpoint = breakpointValue;
						break;
					default:
						break;
					}
				}
			case "turn":
				if (command.size() > 3) {
					switch (command.get(PARAMETER2_INDEX)) {
					case "right":
						turnRightBreakpoint = breakpointValue;
						break;
					case "left":
						turnLeftBreakpoint = breakpointValue;
						break;
					default:
						break;
					}
					break;
				}
				turnBreakpoint = breakpointValue;
				break;
			case "speed":
				setspeedBreakpoint = breakpointValue;
				break;
			case "sensor":
				if (command.size() > 3) {
					switch (command.get(PARAMETER2_INDEX)) {
					case "touch":
						readSensorTouchBreakpoint = breakpointValue;
						break;
					case "ultrasonic":
						readSensorUltrasonicBreakpoint = breakpointValue;
						break;
					case "microphone":
						readSensorMicrophoneBreakpoint = breakpointValue;
						break;
					case "light":
						readSensorLightBreakpoint = breakpointValue;
						break;
					default:
						break;
					}
					break;
				}
				readSensorBreakpoint = breakpointValue;
				break;
			case "swing":
				swingBreakpoint = breakpointValue;
				break;
			default:
				break;
			}
		}
	}

	private ArrayList<String> implementVariable(ArrayList<String> command) {
		ArrayList<String> messageData = new ArrayList<String>();
		String speed;
		switch (command.get(PARAMETER1_INDEX)) {
		case "drive":
			messageData.add("drive");
			switch(command.get(PARAMETER2_INDEX)){
			case "rotate":
				messageData.add("rotate");
				speed = Integer.toString((int) pilot.getRotateSpeed());
				messageData.add(speed);
				break;
			case "travel":
				messageData.add("travel");
				speed = Integer.toString((int) pilot.getTravelSpeed());
				messageData.add(speed);
				break;
			default:
				return new ArrayList<String>();
			}
			break;
		case "swing":
			messageData.add("swing");
			messageData.add("rotate");
			messageData.add("" + Motor.C.getSpeed());
			break;
		default:
			return new ArrayList<String>();
		}
		return messageData;
	}
}
