//CS3240g8b
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

public class Debugger {
	private static DebuggerShell shell;
	private boolean isConnected;
	private Thread readThread;

	private static NXTComm connection;
	private static boolean USBTest = false;
	private static NXTInfo[] info;
	private static long start;
	private static long latency;
	final static int DATA_START = 2;
	final static int NXT_PIN = 1234;
	final static int COMMAND_PARAMETER = 0;
	final static int PARAMETER1_INDEX = 1;
	final static int PARAMETER2_INDEX = 2;
	final static int ONLY_COMMAND_LENGTH = 1;
	final static int COMMAND_AND_PARAM_LENGTH = 2;
	static Boolean readFlag = true;
	static Object lock = new Object();
	private OutputStream os;
	private InputStream is;
	private DataOutputStream oHandle;
	private DataInputStream iHandle;
	private static byte[] buffer = new byte[256];

	public Debugger() throws NXTCommException {
		shell = new DebuggerShell(this);
		this.isConnected = false;
	}

	public boolean isConnected() {
		return this.isConnected;
	}

	public void readFromRobot() {
		readThread = new Thread() {
			public void run() {
				try {
					while (true) {
						int count = iHandle.read(buffer);
						if (count > 0) {
							String input = (new String(buffer))
									.substring(0, 11);
							shell.printRobotMessage("Message from robot: "
									+ input);

							shell.set(input.charAt(DATA_START) + "",
									Integer.parseInt(input.substring(3, 10)));
						}
					}
				} catch (Exception e) {
					System.out.println("read error");
				}
			}
		};
		readThread.start();
	}

	public void stopReading() {
		if (readThread == null)
			return;
		else if (readThread.isAlive())
			readThread.stop();
	}

	public void establishConnection() {
		Thread t = new Thread() {
			public void run() {
				start = System.currentTimeMillis();
				try {
					if (USBTest) {
						connection = NXTCommFactory
								.createNXTComm(NXTCommFactory.USB);
						info = connection.search(null, 0);
					} else {
						connection = NXTCommFactory
								.createNXTComm(NXTCommFactory.BLUETOOTH);
						info = connection.search("NXT", NXT_PIN);
					}
					if (info.length == 0) {
						shell.printMessage("Unable to find device");
						return;
					}

					connection.open(info[0]);
					os = connection.getOutputStream();
					is = connection.getInputStream();

					oHandle = new DataOutputStream(os);
					iHandle = new DataInputStream(is);
					latency = System.currentTimeMillis() - start;
					shell.printRobotMessage("Connection is established after "
							+ latency + "ms.");
					isConnected = true;

					readFromRobot();
					sendMessage("DMSM000001");

				} catch (Exception e) {
					shell.printMessage("Connection failed to establish.");
				}
			}
		};
		shell.printMessage("Establishing Connection...");
		t.start();
	}

	public void endConnection() {
		shell.printMessage("Ending Connection...");
		sendMessage("DMSM000000");
		sendMessage("EC00000000");
		stopReading();
		isConnected = false;
		shell.printRobotMessage("Disconnected from robot");
	}

	public void sendMessage(String message) {
		if (!isConnected) {
			shell.printMessage("Not connected to NXT!");
			return;
		}
		try {
			message += getCheckSum(message);
			shell.printMessage("Sending message: \"" + message + "\"");
			oHandle.write(message.getBytes());
			oHandle.flush();
		} catch (Exception e) {

		}
	}

	public static boolean isNumeric(String number) {
		try {
			int i = Integer.parseInt(number);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static String getCheckSum(String str) {
		int sum = 0;
		String ret;
		byte[] buffer = str.getBytes();
		for (int i = 0; i < buffer.length; i++) {
			sum += (int) buffer[i];
		}
		sum = sum % 256;
		byte[] checksum = new byte[1];
		checksum[0] = (byte) sum;
		ret = new String(checksum);
		return ret;
	}

	public static String getCommandHelp() {

		return "To form each the commands follow the directions below.  Arguments are sepearated by spaces. Case does not matter."
				+ " \n\nMOVE: Type: 'move [specify direction: forward or backward] [distance in cm]' \nFor example to move forward 120 cm, type: move forward 120"
				+ " \n\nARC: Type: 'arc [specify direction: forward or backward] [specify direction to arc in: left of right].\nFor example, to arc up and right, type: arc forward right"
				+ " \n\nTURN: Type: 'turn [specify direction: right or left] [specify number of degrees to turn]"
				+ " \n\nSTOP: Type: 'stop'"
				+ " \n\nSET SPEED: Type: 'setspeed [specify: a, b, c, or d, representing motor a, b, c, or drive] [t or r for type of speed] [number of new speed].\nFor example to set motor a to speed 30 type: setspeed a 30"
				+ " \n\nREAD: Type: 'read [specify: u, t, m, l, or all, for ultrasonic, touch, microphone, light, or all sensor information respectively].  \nFor example to read information from the light sensor type: read l.  \nTo read information from all sensors type: read all"
				+ " \n\nNONE: To create NoOp message type: none"
				+ " \n\nSWING: Type: 'swing'";
	}
	
	public void runCommand(String command) {
		if (command.equalsIgnoreCase("help") || command.equalsIgnoreCase("?")) {
			shell.printMessage(getCommandHelp());
		} else if (!isConnected) {
			shell.printMessage("Robot is not connected!");
		} else if (command.equalsIgnoreCase("exit")) {
			endConnection();
		} else {
			String message = createCommand(command);
			sendMessage(message);
		}

	}
	
	private static String addPaddingZeros(String command, String endOfCommand) {
		for (int i = command.length() - 1; i < 9 - endOfCommand.length(); i++) {
			command += "0";
		}
		return command + endOfCommand;
	}

	private static String addTrailingZeros(String message) {
		if (message.length() >= 10)
			return message;
		else {
			while (message.length() < 10) {
				message += "0";
			}
			return message;
		}
	}

	public static String createCommand(String cmd) {
		String message = "";
		String[] cmdWords = cmd.toLowerCase().split(" ");
		String command = getCommand(cmdWords);
		String[] args = getCommandArguments(cmdWords);

		if (cmdWords.length < ONLY_COMMAND_LENGTH) {
			return message;
		} else {
			if (command.equalsIgnoreCase("move")) {
				message = createMoveMessage(args);
			} else if (command.equalsIgnoreCase("arc")) {
				message = createArcMessage(args);
			} else if (command.equalsIgnoreCase("turn")) {
				message = createTurnMessage(args);
			} else if (command.equalsIgnoreCase("stop")) {
				message = createStopMessage();
			} else if (command.equalsIgnoreCase("setspeed")) {
				message = createSetSpeedMessage(args);
			} else if (command.equalsIgnoreCase("read")) {
				message = createReadSensorMessage(args);
			} else if (command.equalsIgnoreCase("none")) {
				message = createNoOpMessage();
			} else if (command.equalsIgnoreCase("swing")) {
				message = createSwingMessage();
			} else if (command.equalsIgnoreCase("setbreakpoint")) {
				message = createBreakpointMessage(args, true);
			} else if (command.equalsIgnoreCase("removebreakpoint")) {
				message = createBreakpointMessage(args, false);
			}
		}
		return message;
	}

	public static String[] getCommandArguments(String[] words) {
		String[] args = new String[words.length - 1];
		for (int i = 1; i < words.length; i++) {
			args[i - 1] = words[i];
		}
		return args;
	}

	public static String getCommand(String[] words) {
		return words[COMMAND_PARAMETER];
	}

	public static String createMoveMessage(String[] args) {
		String command = "MS";
		if (args.length < ONLY_COMMAND_LENGTH) {
			return "";
		} else {
			if (args[COMMAND_PARAMETER].equalsIgnoreCase("forward")
					|| args[COMMAND_PARAMETER].equalsIgnoreCase("forwards")) {
				command += "F";
			} else if (args[COMMAND_PARAMETER].equalsIgnoreCase("backward")
					|| args[COMMAND_PARAMETER].equalsIgnoreCase("backwards")) {
				command += "B";
			}
			if (args.length == COMMAND_AND_PARAM_LENGTH) {
				if (isNumeric(args[PARAMETER1_INDEX])) {
					for (int i = 0; i < 7 - args[PARAMETER1_INDEX].length(); i++) {
						command += "0";
					}
					command += args[PARAMETER1_INDEX];
				} else {
					return "";
				}
			} else if (args.length > COMMAND_AND_PARAM_LENGTH) {
				return "";
			} else {
				command = addTrailingZeros(command);
			}
		}
		return command;
	}

	public static String createArcMessage(String[] args) {
		String command = "MA";
		if (args.length < ONLY_COMMAND_LENGTH) {
			return "";
		} else {
			if (args[COMMAND_PARAMETER].equalsIgnoreCase("forward")
					|| args[COMMAND_PARAMETER].equalsIgnoreCase("forwards")) {
				command += "F";
			} else if (args[COMMAND_PARAMETER].equalsIgnoreCase("backward")
					|| args[COMMAND_PARAMETER].equalsIgnoreCase("backwards")) {
				command += "B";
			} else {
				return "";
			}
			if (args.length >= COMMAND_AND_PARAM_LENGTH) {
				if (args[PARAMETER1_INDEX].equalsIgnoreCase("left")) {
					command += "L";
				} else if (args[PARAMETER1_INDEX].equalsIgnoreCase("right")) {
					command += "R";
				} else {
					return "";
				}
			}
			command += "090";
			command = addTrailingZeros(command);
		}
		return command;
	}

	public static String createTurnMessage(String[] args) {
		String command = "TN";
		if (args.length < ONLY_COMMAND_LENGTH) {
			return "";
		} else {
			if (args[COMMAND_PARAMETER].equalsIgnoreCase("right")) {
				command += "R";
			} else if (args[COMMAND_PARAMETER].equalsIgnoreCase("left")) {
				command += "L";
			} else {
				return "";
			}
			if (args.length > ONLY_COMMAND_LENGTH) {
				if (isNumeric(args[PARAMETER1_INDEX])) {
					for (int i = 0; i < 7 - args[PARAMETER1_INDEX].length(); i++) {
						command += "0";
					}
					command += args[PARAMETER1_INDEX];
				} else {
					return "";
				}
			} else {
				command = addTrailingZeros(command);
			}
		}
		return command;
	}

	public static String createStopMessage() {
		return addTrailingZeros("ST");
	}
	
	public static String createSwingMessage() {
		return addTrailingZeros("SW");
	}

	public static String createSetSpeedMessage(String[] args) {
		String command = "SS";
		if (args.length != 3) {
			return "";
		} else {
			if (args[COMMAND_PARAMETER].equalsIgnoreCase("a")
					|| args[COMMAND_PARAMETER].equalsIgnoreCase("b")
					|| args[COMMAND_PARAMETER].equalsIgnoreCase("c")
					|| args[COMMAND_PARAMETER].equalsIgnoreCase("d")) {
				command += args[COMMAND_PARAMETER].toUpperCase();
			} else {
				return "";
			}
			if (args[PARAMETER1_INDEX].equalsIgnoreCase("t")
					|| args[PARAMETER1_INDEX].equalsIgnoreCase("r")) {
				command += args[PARAMETER1_INDEX].toUpperCase();
			} else {
				return "";
			}
			if (isNumeric(args[PARAMETER2_INDEX])) {
				for (int i = 0; i < 6 - args[PARAMETER2_INDEX].length(); i++) {
					command += "0";
				}
				command += args[PARAMETER2_INDEX];
			} else {
				return "";
			}
		}
		return command;
	}

	public static String createReadSensorMessage(String[] args) {
		String command = "";
		if (args.length != ONLY_COMMAND_LENGTH) {
			return "";
		}
		if (args[COMMAND_PARAMETER].equalsIgnoreCase("all")) {
			command = addTrailingZeros("RA");
		} else {
			if (args[COMMAND_PARAMETER].equalsIgnoreCase("u")
					|| args[COMMAND_PARAMETER].equalsIgnoreCase("t")
					|| args[COMMAND_PARAMETER].equalsIgnoreCase("m")
					|| args[COMMAND_PARAMETER].equalsIgnoreCase("l")) {
				command = addTrailingZeros("RS"
						+ args[COMMAND_PARAMETER].toUpperCase());
			}
		}
		return command;
	}

	public static String createNoOpMessage() {
		return "0000000000";
	}

	private static String createBreakpointMessage(String[] parameters,
			boolean value) {
		String message = "DMSB";
		if (parameters.length < 1) {
			return "";
		} else {
			switch (parameters[0]) {
			case "move":
				message += "MV";
				if (parameters.length == 2) {
					switch (parameters[1]) {
					case "forward":
						message += "F";
						break;
					case "backward":
						message += "B";
						break;
					}
				}
				break;
			case "arc":
				message += "MA";
				if (parameters.length >= 2) {
					switch (parameters[1]) {
					case "forward":
						message += "F";
						break;
					case "backward":
						message += "B";
						break;
					}
					if (parameters.length >= 3) {
						switch (parameters[2]) {
						case "left":
							message += "L";
							break;
						case "right":
							message += "R";
							break;
						}
					}
				}
				break;

			case "setspeed":
				message += "SS";
				break;
			case "read":
				message += "RS";
				if (parameters.length >= 2) {
					switch (parameters[1]) {
					case "touch":
						message += "T";
						break;
					case "ultrasonic":
						message += "U";
						break;
					case "microphone":
						message += "M";
						break;
					case "light":
						message += "L";
						break;
					}
				}
				break;
			case "turn":
				message += "TN";
				if (parameters.length >= 2) {
					switch (parameters[1]) {
					case "right":
						message += "R";
						break;
					case "left":
						message += "L";
						break;
					}
				}
				break;
			}
		}
		if (value) {
			message = addPaddingZeros(message, "1");
		} else {
			message = addPaddingZeros(message, "0");
		}
		message += getCheckSum(message);
		shell.printMessage("Message Length: " + message.length());
		return message;
	}
	
	public static void main(String[] args) throws Exception {
		Debugger d = new Debugger();
	}
}
