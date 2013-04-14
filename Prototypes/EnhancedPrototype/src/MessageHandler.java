

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
				if (command.equalsIgnoreCase("ms")) {
					commandData = decodeMoveStraight(params);
					return commandData;
				} else if (command.equalsIgnoreCase("ma")) {
					commandData = decodeMoveArc(params);
					return commandData;
				} else if (command.equalsIgnoreCase("tn")) {
					commandData = decodeTurn(params);
					return commandData;
				} else if (command.equalsIgnoreCase("st")) {
					commandData = decodeStop(params);
					return commandData;
				} else if (command.equalsIgnoreCase("rs")) {
					commandData = decodeReadSensor(params);
					return commandData;
				} else if (command.equalsIgnoreCase("ss")) {
					commandData = decodeSetSpeed(params);
					return commandData;
				} else if (command.equalsIgnoreCase("ra")) {

				} else if (command.equalsIgnoreCase("ec")) {
					commandData = new ArrayList<String>();
					commandData.add("exit");
					return commandData;
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
		if(direction.equalsIgnoreCase("f")){
			commandData.add("forward");
		} else if(direction.equalsIgnoreCase("b")){
			commandData.add("backward");
		} else {
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
		if(direction.equalsIgnoreCase("r")){
			commandData.add("right");
		} else if(direction.equalsIgnoreCase("l")){
			commandData.add("left");
		} else {
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
		if(direction.equalsIgnoreCase("f")){
			commandData.add("forward");
		} else if(direction.equalsIgnoreCase("b")){
			commandData.add("backward");
		} else {
			return new ArrayList<String>();
		}

		String turn = parameters.substring(1,2);
		if(turn.equalsIgnoreCase("r")){
			commandData.add("right");
		} else if(turn.equalsIgnoreCase("l")){
			commandData.add("left");
		} else {
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