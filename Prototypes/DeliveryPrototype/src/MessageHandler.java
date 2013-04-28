import java.util.ArrayList;

public class MessageHandler {
	static final int MESSAGE_LENGTH = 11;
	static final int COMMAND_LENGTH = 10;
	static final int CHECKSUM_INDEX = 10;
	static final int START_INDEX = 0;
	static final int COMMAND_TYPE_END_INDEX = 2;
	static final int SENSOR_TYPE_INDEX = 0;
	static final int SENSOR_VALUE_INDEX = 1;
	static final int SENSOR_DATA_SIZE = 2;

	public MessageHandler() {

	}

	public String createACK() {
		System.out.println("ACK created");
		String ack = "AK00000000";
		ack += getChecksum(ack);
		return ack;
	}

	public String encodeMessage(ArrayList<String> messageData) {
		if (messageData.size() == SENSOR_DATA_SIZE) {
			String encoded = "SD";
			String value = messageData.get(SENSOR_VALUE_INDEX);
			switch (messageData.get(SENSOR_TYPE_INDEX)) {
			case "touch":
				encoded += "T";
				break;
			case "sound":
				encoded += "M";
				break;
			case "ultrasonic":
				encoded += "U";
				break;
			case "light":
				encoded += "L";
				break;
			default:
				encoded = "";
				break;
			}
			if (isNumeric(value)) {
				encoded += getPadding(encoded.length(), value.length()) + value;
			}
			encoded += getChecksum(encoded);
			return encoded;
		} else
			return "";
	}

	// utility method to determine whether a String is a number
	private boolean isNumeric(String number) {
		try {
			int i = Integer.parseInt(number);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	// utility method to determine whether the parameters is empty space by
	// communications protocol
	private boolean isEmptyZeros(String parameters) {
		if (Integer.parseInt(parameters) == 0) {
			return true;
		} else {
			return false;
		}
	}

	private boolean verifyChecksum(String message) {
		if (message.length() == MESSAGE_LENGTH) {
			byte[] string = message.getBytes();
			System.out.println("Checksum: " + message.substring(CHECKSUM_INDEX));
			if (getChecksum(message.substring(START_INDEX, COMMAND_LENGTH))
					.equals(message.substring(CHECKSUM_INDEX))) {
				return true;
			}
		}
		return false;
	}

	private String getPadding(int messageHeadingLength, int messageTailLength) {
		String returnString = "";
		for (int i = 0; i < MESSAGE_LENGTH - messageHeadingLength
				- messageTailLength; i++) {
			returnString += "0";
		}
		return returnString;
	}

	private String getChecksum(String message) {
		int sum = 0;
		String ret;
		byte[] buffer = message.getBytes();
		for (int i = 0; i < buffer.length; i++) {
			sum += (int) buffer[i];
		}
		sum = sum % 256;
		byte[] checksum = new byte[1];
		checksum[START_INDEX] = (byte) sum;
		ret = new String(checksum);
		System.out.println("Calc Checksum: " + ret);
		return ret;
	}

	public ArrayList<String> decodeMessage(String message) {
		ArrayList<String> commandData = new ArrayList<String>();
		if (message.length() != MESSAGE_LENGTH) {
			return commandData;
		} else {
			if (verifyChecksum(message)) {
				String command = message.substring(START_INDEX,
						COMMAND_TYPE_END_INDEX);
				String params = message.substring(COMMAND_TYPE_END_INDEX,
						COMMAND_LENGTH);
				switch (command) {
				case "MS":
					commandData = decodeMoveStraight(params);
					return commandData;
				case "MA":
					commandData = decodeMoveArc(params);
					return commandData;
				case "TN":
					commandData = decodeTurn(params);
					return commandData;
				case "ST":
					commandData = decodeStop(params);
					return commandData;
				case "RS":
					commandData = decodeReadSensor(params);
					return commandData;
				case "SS":
					commandData = decodeSetSpeed(params);
					return commandData;
				case "RA":
					break;
				case "SW":
					commandData = decodeSwing(params);
				case "EC":
					commandData.add("exit");
					return commandData;
				case "DM":
					commandData = decodeDebugMessage(params);
					return commandData;
				default:
					return new ArrayList<String>();
				}
			} else {
				System.out.println("Invalid Checksum");
			}
		}
		return new ArrayList<String>();
	}

	private ArrayList<String> decodeMoveStraight(String parameters) {
		ArrayList<String> commandData = new ArrayList<String>();
		commandData.add("straight");
		String direction = parameters.substring(0, 1);
		switch (direction) {
		case "F":
			commandData.add("forward");
			break;
		case "B":
			commandData.add("backward");
			break;
		default:
			return new ArrayList<String>();
		}

		String distance = parameters.substring(1);
		if (isNumeric(distance)) {
			commandData.add(distance);
		} else {
			return new ArrayList<String>();
		}
		return commandData;
	}

	private ArrayList<String> decodeTurn(String parameters) {
		ArrayList<String> commandData = new ArrayList<String>();
		commandData.add("turn");
		String direction = parameters.substring(0, 1);
		switch (direction) {
		case "R":
			commandData.add("right");
			break;
		case "L":
			commandData.add("left");
			break;
		default:
			return new ArrayList<String>();
		}
		String radius = parameters.substring(1);
		if (isNumeric(radius)) {
			commandData.add(radius);
		} else {
			return new ArrayList<String>();
		}
		return commandData;
	}

	private ArrayList<String> decodeStop(String parameters) {
		ArrayList<String> commandData = new ArrayList<String>();
		commandData.add("stop");
		if (!(isNumeric(parameters) && isEmptyZeros(parameters))) {
			return new ArrayList<String>();
		}
		return commandData;
	}

	private ArrayList<String> decodeMoveArc(String parameters) {
		ArrayList<String> commandData = new ArrayList<String>();
		commandData.add("arc");
		String direction = parameters.substring(0, 1);
		switch (direction) {
		case "F":
			commandData.add("forward");
			break;
		case "B":
			commandData.add("backward");
			break;
		default:
			return new ArrayList<String>();
		}

		String turn = parameters.substring(1, 2);
		switch (turn) {
		case "R":
			commandData.add("right");
			break;
		case "L":
			commandData.add("left");
			break;
		default:
			return new ArrayList<String>();
		}

		String distance = parameters.substring(4, 7);
		if (isNumeric(distance)) {
			commandData.add(distance);
		} else {
			return new ArrayList<String>();
		}

		String radius = parameters.substring(7);
		if (isNumeric(radius)) {
			commandData.add(radius);
		} else {
			return new ArrayList<String>();
		}
		return commandData;
	}

	private ArrayList<String> decodeReadSensor(String parameters) {
		ArrayList<String> commandData = new ArrayList<String>();
		commandData.add("readsensor");
		String direction = parameters.substring(0, 1);
		switch (direction) {
		case "U":
			commandData.add("ultrasonic");
			break;
		case "T":
			commandData.add("touch");
			break;
		case "M":
			commandData.add("sound");
			break;
		case "L":
			commandData.add("light");
			break;
		default:
			return new ArrayList<String>();
		}

		return commandData;
	}

	private ArrayList<String> decodeSetSpeed(String parameters) {
		ArrayList<String> commandData = new ArrayList<String>();
		commandData.add("setspeed");
		switch (parameters.substring(0, 1)) {
		case "A":
			commandData.add("motora");
			break;
		case "B":
			commandData.add("motorb");
			break;
		case "C":
			commandData.add("motorc");
			break;
		case "D":
			commandData.add("drivemotors");
			break;
		default:
			return new ArrayList<String>();
		}
		switch (parameters.substring(1, 2)) {
		case "T":
			commandData.add("travel");
			break;
		case "R":
			commandData.add("rotate");
			break;
		default:
			return new ArrayList<String>();

		}
		String speed = parameters.substring(2);
		if (isNumeric(speed)) {
			commandData.add(speed);
		} else {
			return new ArrayList<String>();
		}
		return commandData;
	}

	private ArrayList<String> decodeSwing(String parameters) {
		ArrayList<String> commandData = new ArrayList<String>();
		commandData.add("swing");
		if (!(isNumeric(parameters) && isEmptyZeros(parameters))) {
			return new ArrayList<String>();
		}
		return commandData;
	}

	private ArrayList<String> decodeDebugMessage(String parameters) {
		String debugCommand = parameters.substring(START_INDEX,
				COMMAND_TYPE_END_INDEX);
		ArrayList<String> commandData = new ArrayList<String>();
		switch (debugCommand) {
		case "SM":
			commandData = decodeDebugMode(parameters
					.substring(COMMAND_TYPE_END_INDEX));
			break;
		case "SB":
			commandData = decodeSetBreakpointMessage(parameters
					.substring(COMMAND_TYPE_END_INDEX));
			break;
		default:
			return new ArrayList<String>();
		}
		return commandData;
	}

	private ArrayList<String> decodeDebugMode(String parameters) {
		ArrayList<String> commandData = new ArrayList<String>();
		commandData.add("mode");
		int modeValue = Integer.parseInt(parameters);
		switch (modeValue) {
		case 0:
			commandData.add("false");
			break;
		case 1:
			commandData.add("true");
			break;
		default:
			return new ArrayList<String>();
		}
		return commandData;
	}

	private ArrayList<String> decodeSetBreakpointMessage(String parameters) {
		ArrayList<String> commandData = new ArrayList<String>();
		commandData.add("breakpoint");
		String breakpointMethod = parameters.substring(START_INDEX,
				COMMAND_TYPE_END_INDEX);
		switch (breakpointMethod) {
		case "MV":
			commandData.add("move");
			switch (parameters.substring(COMMAND_TYPE_END_INDEX, 3)) {
			case "0":
				break;
			case "F":
				commandData.add("forward");
				break;
			case "B":
				commandData.add("backward");
				break;
			default:
				return new ArrayList<String>();
			}
			break;
		case "MA":
			commandData.add("arc");
			switch (parameters.substring(COMMAND_TYPE_END_INDEX, 3)) {
			case "F":
				commandData.add("forward");
				break;
			case "B":
				commandData.add("backward");
				break;
			case "0":
				break;
			default:
				return new ArrayList<String>();
			}
			switch (parameters.substring(3, 4)) {
			case "R":
				commandData.add("right");
				break;
			case "L":
				commandData.add("left");
				break;
			case "0":
				break;
			default:
				return new ArrayList<String>();
			}
			break;
		case "SS":
			commandData.add("speed");
			break;
		case "RS":
			commandData.add("sensor");
			switch (parameters.substring(COMMAND_TYPE_END_INDEX, 3)) {
			case "T":
				commandData.add("touch");
				break;
			case "U":
				commandData.add("ultrasonic");
				break;
			case "M":
				commandData.add("microphone");
				break;
			case "L":
				commandData.add("light");
				break;
			case "0":
				break;
			default:
				return new ArrayList<String>();
			}
			break;
		case "TN":
			commandData.add("turn");
			switch (parameters.substring(COMMAND_TYPE_END_INDEX, 3)) {
			case "R":
				commandData.add("right");
				break;
			case "L":
				commandData.add("left");
				break;
			case "0":
				break;
			default:
				return new ArrayList<String>();
			}
			break;
		case "SW":
			commandData.add("swing");
			break;
		default:
			return new ArrayList<String>();
		}
		switch (parameters.substring(parameters.length() - 1)) {
		case "1":
			commandData.add("true");
			break;
		case "0":
			commandData.add("false");
			break;
		default:
			return new ArrayList<String>();
		}
		return commandData;
	}
}