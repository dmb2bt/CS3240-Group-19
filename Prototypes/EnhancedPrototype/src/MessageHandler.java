

import java.util.ArrayList;

public class MessageHandler {
	static final int MESSAGE_LENGTH = 10;

	public MessageHandler() {
	}

	public ArrayList<String> decodeMessage(String message) {
		ArrayList<String> commandData = new ArrayList<String>(); 
		if (message.length() != 11) {
			return commandData;
		} else {
			if(verifyChecksum(message)){
				String command = message.substring(0, 2);
				String params = message.substring(2,10);
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
				case "EC":
					commandData = new ArrayList<String>();
					commandData.add("exit");
					return commandData;
				default:
					return new ArrayList<String>();
				}
			}			
		}
		return new ArrayList<String>();
	}

	public String createACK(){
		System.out.println("ACK created");
		String ack = "AK00000000";
		ack += getChecksum(ack);
		System.out.println("Message Length: " + ack.length());
		return ack;
	}

	public String encodeMessage(ArrayList<String> messageData) {
		if(messageData.size() > 1){
			String encoded = "SD";
			String value = messageData.get(1);
			switch (messageData.get(0)) {
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
			if(isNumeric(value)){
				encoded += getPadding(encoded.length(), value.length()) + value;
			}
			encoded += getChecksum(encoded);
			return encoded;
		} else return "";
	}

	private boolean isNumeric(String number) {
		try {
			int i = Integer.parseInt(number);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	private boolean verifyChecksum(String message) {
		if(message.length() == 11) {
			byte[] string = message.getBytes();
			if(getChecksum(message.substring(0, 10)).equals(message.substring(10))){
				return true;
			}
		}
		return false;
	}

	private String getPadding(int messageHeadingLength, int messageTailLength){
		String returnString = "";
		for(int i = 0; i < MESSAGE_LENGTH - messageHeadingLength - messageTailLength; i++){
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
		checksum[0] = (byte) sum;
		ret = new String(checksum);
		System.out.println("CheckSum: " + ret);
		return ret;
	}

	private ArrayList<String> decodeMoveStraight(String parameters) {
		ArrayList<String> commandData = new ArrayList<String>();
		commandData.add("straight");
		String direction = parameters.substring(0,1);
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
		if(isNumeric(distance)){
			commandData.add(distance);
		}else{
			return new ArrayList<String>();
		}
		return commandData;
	}
	private ArrayList<String> decodeTurn(String parameters) {
		ArrayList<String> commandData = new ArrayList<String>();
		commandData.add("turn");
		String direction = parameters.substring(0,1);
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
		if(isNumeric(radius)){
			commandData.add(radius);
		}else{
			return new ArrayList<String>();
		}
		return commandData;
	}
	private ArrayList<String> decodeStop(String parameters) {
		ArrayList<String> commandData = new ArrayList<String>();
		commandData.add("stop");
		String params = parameters.substring(0);
		if( !(isNumeric(params) && Integer.parseInt(params) == 0) ){
			return new ArrayList<String>();
		}
		return commandData;
	}
	private ArrayList<String> decodeMoveArc(String parameters) {
		ArrayList<String> commandData = new ArrayList<String>();
		commandData.add("arc");		
		String direction = parameters.substring(0,1);
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

		String turn = parameters.substring(1,2);
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

		String distance = parameters.substring(4,7);
		if(isNumeric(distance)){
			commandData.add(distance);
		}else{
			return new ArrayList<String>();
		}

		String radius = parameters.substring(7);
		if(isNumeric(radius)){
			commandData.add(radius);
		}else{
			return new ArrayList<String>();
		}
		return commandData;
	}
	private ArrayList<String> decodeReadSensor(String parameters)
	{
		ArrayList<String> commandData = new ArrayList<String>();
		commandData.add("readsensor");		
		String direction = parameters.substring(0,1);
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
	private ArrayList<String> decodeSetSpeed(String parameters)
	{
		ArrayList<String> commandData = new ArrayList<String>();
		commandData.add("setspeed");
		switch (parameters.substring(0,1)) {
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
		switch (parameters.substring(1,2)) {
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
		if(isNumeric(speed)){
			commandData.add(speed);
		}else{
			return new ArrayList<String>();
		}
		return commandData;	
	}
}