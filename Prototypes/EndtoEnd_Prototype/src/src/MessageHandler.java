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
				commandData.add("straight");
				String direction = params.substring(0,1);
				if(direction.equalsIgnoreCase("f")){
					commandData.add("forward");
				} else if(direction.equalsIgnoreCase("b")){
					commandData.add("backward");
				} else {
					return new ArrayList<String>();
				}
				
			} else if (command.equalsIgnoreCase("ma")) {

			} else if (command.equalsIgnoreCase("tn")) {

			} else if (command.equalsIgnoreCase("st")) {

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

	}
}
