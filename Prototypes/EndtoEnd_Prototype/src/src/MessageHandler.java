package src;

import java.util.ArrayList;

public class MessageHandler {

	public MessageHandler() {
	}

	public ArrayList<String> decodeMessage(String message) {
		ArrayList<String> commandData = new ArrayList<String>(); 
		if (message.length() != 11) {
			return commandData;
		} else {
			String command = message.substring(0, 2);
			String params = message.substring(2,10);
			if (command.equalsIgnoreCase("ms")) {
				decodeMoveStraight(params, commandData);
			} else if (command.equalsIgnoreCase("ma")) {
				decodeMoveArc(params, commandData);
			} else if (command.equalsIgnoreCase("tn")) {
				decodeTurn(params, commandData);
			} else if (command.equalsIgnoreCase("st")) {
				decodeStop(params, commandData);
			} else if (command.equalsIgnoreCase("rs")) {

			} else if (command.equalsIgnoreCase("ss")) {

			} else if (command.equalsIgnoreCase("ra")) {

			} else if (command.equalsIgnoreCase("ec")) {

			}
		}

		return new ArrayList<String>();
	}

	public String encodeMessage(String[] message) {

		return "";
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
		return true;
	}

	private String getChecksum(String message) {
		return "";
	}

	private void decodeMoveStraight(String parameters,
			ArrayList<String> commandData) {
		commandData.add("straight");
		String direction = parameters.substring(0,1);
		if(direction.equalsIgnoreCase("f")){
			commandData.add("forward");
		} else if(direction.equalsIgnoreCase("b")){
			commandData.add("backward");
		} else {
			commandData = new ArrayList<String>();
			return;
		}
		String distance = parameters.substring(1);
		if(isNumeric(distance)){
			commandData.add(distance);
		}else{
			commandData = new ArrayList<String>();
			return;
		}
	}
	private void decodeTurn(String parameters,
			ArrayList<String> commandData) {
		commandData.add("turn");
		String direction = parameters.substring(0,1);
		if(direction.equalsIgnoreCase("r")){
			commandData.add("right");
		} else if(direction.equalsIgnoreCase("l")){
			commandData.add("left");
		} else {
			commandData = new ArrayList<String>();
			return;
		}
		String radius = parameters.substring(1);
		if(isNumeric(radius)){
			commandData.add(radius);
		}else{
			commandData = new ArrayList<String>();
			return;
		}
	}
	private void decodeStop(String parameters,
			ArrayList<String> commandData) {
		commandData.add("stop");
		String params = parameters.substring(0);
		if( !(isNumeric(params) && Integer.parseInt(params) == 0) ){
			commandData = new ArrayList<String>();
			return;
		}
	}
	private void decodeMoveArc(String parameters,
			ArrayList<String> commandData) {
		commandData.add("arc");
		String direction = parameters.substring(0,1);
		
		if(direction.equalsIgnoreCase("f")){
			commandData.add("forward");
		} else if(direction.equalsIgnoreCase("b")){
			commandData.add("backward");
		} else {
			commandData = new ArrayList<String>();
			return;
		}
		
		String turn = parameters.substring(1,2);
		if(turn.equalsIgnoreCase("r")){
			commandData.add("right");
		} else if(turn.equalsIgnoreCase("l")){
			commandData.add("left");
		} else {
			commandData = new ArrayList<String>();
			return;
		}
		
		String distance = parameters.substring(4,7);
		if(isNumeric(distance)){
			commandData.add(distance);
		}else{
			commandData = new ArrayList<String>();
			return;
		}
		
		String radius = parameters.substring(7);
		if(isNumeric(radius)){
			commandData.add(radius);
		}else{
			commandData = new ArrayList<String>();
			return;
		}
	}
}