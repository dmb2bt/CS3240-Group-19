import java.util.Arrays;
import java.util.Scanner;

public class Test {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		String input = "";
		
		do {
			System.out.println("Enter a command...");
			input = in.nextLine();
			if (input.equalsIgnoreCase("help") || input.equals("?")) {
				System.out.println(getCommandHelp());
			} else {
				String message = createCommand(input);
				if (message.equals("")) {
                    System.out.println("Maformed Input, try again.");
                } else if (message.length() < 10){
                	System.out.println("Message length is too short to be correct!");
                } else {
    				System.out.println(message);                	
                }
			}
		} while (!input.equalsIgnoreCase("exit"));
		System.out.println("Exiting command creation test...");
	}

	public static String createCommand(String input) {
		String message = "";
		String[] cmdWords = input.split(" ");
		String command = getCommand(cmdWords);
		String[] args = getCommandArguments(cmdWords);

		if (cmdWords.length < 1) {
			return message;
		} else {
			if (command.equalsIgnoreCase("move")) {
				message = createMoveCommand(args);
			} else if (command.equalsIgnoreCase("arc")) {
				message = createArcCommand(args);
			} else if (command.equalsIgnoreCase("turn")) {
				message = createTurnCommand(args);
			} else if (command.equalsIgnoreCase("stop")) {
				message = createStopCommand();
			} else if (command.equalsIgnoreCase("setspeed")) {
				message = createSetSpeedMessage(args);
			} else if (command.equalsIgnoreCase("read")) {
				message = createReadSensorMessage(args);
			} else if (command.equalsIgnoreCase("none")) {
				message = createNoOpMessage();
			}
		}
		return message;
	}

	public static String[] getCommandArguments(String[] words) {
		String[] args = new String[words.length - 1];
		for (int i = 1; i < words.length; i++) {
			args[i - 1] = words[i];
		}
		System.out.println("args: " + Arrays.toString(args));
		System.out.println("args length: " + args.length);
		return args;
	}

	public static String getCommand(String[] words) {
		return words[0];
	}

	public static boolean isNumeric(String number) {
		try {
			int i = Integer.parseInt(number);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static String createMoveCommand(String[] args) {
		String command = "MS";
		if (args.length < 1) {
			return "";
		} else {
			if (args[0].equalsIgnoreCase("forward")) {
				command += "F";
			} else if (args[0].equalsIgnoreCase("backward")) {
				command += "B";
			}
			if (args.length == 2) {
				if (isNumeric(args[1])) {
					for(int i = 0; i < 7 - args[1].length(); i++){
						command += "0";	
					}
					command += args[1];
				} else {
					return "";
				}
			} else if (args.length > 2){
				return "";
			} else {
				command += "0000000";
			}
		}
		return command;
	}

	public static String createArcCommand(String[] args) {
		String command = "MA";
		if (args.length < 1) {
			return "";
		} else {
			if (args[0].equalsIgnoreCase("forward")) {
				command += "F";
			} else if (args[0].equalsIgnoreCase("backward")) {
				command += "B";
			} else {
				return "";
			}
			if (args.length >= 2) {
				if (args[1].equalsIgnoreCase("left")) {
					command += "L";
				} else if (args[1].equalsIgnoreCase("right")) {
					command += "R";
				} else {
					return "";
				}
			}
			command += "090";
			command += "000";
		}
		return command;
	}

	public static String createTurnCommand(String[] args) {
		String command = "TN";
		if (args.length < 1) {
			return "";
		} else {
			if (args[0].equalsIgnoreCase("right")) {
				command += "R";
			} else if (args[0].equalsIgnoreCase("left")) {
				command += "L";
			} else {
				return "";
			}
			if (args.length > 1) {
				if (isNumeric(args[1])) {
					for(int i = 0; i < 7 - args[1].length(); i++){
						command += "0";	
					}
					command += args[1];
				} else {
					return "";
				}
			} else {
				command += "0000000";
			}
		}
		return command;
	}

	public static String createStopCommand() {
		return "ST00000000";
	}

	public static String createSetSpeedMessage(String[] args) {
		String command = "SS";
		if (args.length != 2){
			return "";
		} else {
			if(args[0].equalsIgnoreCase("a") || 
					args[0].equalsIgnoreCase("b") || 
					args[0].equalsIgnoreCase("c") || 
					args[0].equalsIgnoreCase("d")){
				command += args[0].toUpperCase();
			} else {
				return "";
			}
			if (isNumeric(args[1])) {
				for(int i = 0; i < 7 - args[1].length(); i++){
					command += "0";	
				}
				command += args[1];
			} else {
				return "";
			}
		}
		return command;
	}

	public static String createReadSensorMessage(String[] args) {
		String command = "";
		if(args.length != 1){
			return "";
		}
		if(args[0].equalsIgnoreCase("all")){
			command = "RA00000000";
		}else {
			if(args[0].equalsIgnoreCase("u") || 
					args[0].equalsIgnoreCase("t") || 
					args[0].equalsIgnoreCase("m") || 
					args[0].equalsIgnoreCase("l")){
				command = "RS" + args[0].toUpperCase() + "0000000";
			}
		}
		return command;
	}

	public static String createNoOpMessage() {
		return "0000000000";
	}

	public static String getCommandHelp() {
		return "";
	}
}
