import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

public class TestController {
    private static NXTComm connection;
    private static boolean USBTest = false;
    private static NXTInfo[] info;
    private static long start;
	private static long latency;
    static Boolean readFlag = true;
    static Object lock = new Object();
    private OutputStream os;
    private InputStream is;
    private DataOutputStream oHandle;
    private DataInputStream iHandle;

    public static void main(String[] args) throws NXTCommException {
        start = System.currentTimeMillis();

        if (USBTest) {
            connection = NXTCommFactory.createNXTComm(NXTCommFactory.USB);
            info = connection.search(null, 0);
        } else {
            connection = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
            info = connection.search("NXT", 1234);
        }
        if (info.length == 0) {
            System.out.println("Unable to find device");
            return;
        }

        connection.open(info[0]);
        OutputStream os = connection.getOutputStream();
        InputStream is = connection.getInputStream();

        final DataOutputStream oHandle = new DataOutputStream(os);
        final DataInputStream iHandle = new DataInputStream(is);
        latency = System.currentTimeMillis() - start;
        System.out.printf("Connection is established [%dms]\n", latency);

        String input = "Initiate.";

        Scanner scanner = new Scanner(System.in);

        Thread PCreceiver = new Thread() {
            public void run() {
                while (readFlag) {
                    try {
                        start = System.currentTimeMillis();
                        byte[] buffer = new byte[256];
                        int count = iHandle.read(buffer); // might wnt to check ack later
                        if (count > 0) {
                            String ret = (new String(buffer)).trim();
                            long l = System.currentTimeMillis() - start;
                            System.out.printf("NXJ: %s [%dms]\n", ret, l);
                        }
                        Thread.sleep(10);
                    } catch (IOException e) {
                        System.out.println("Fail to read from iHandle bc "
                                + e.toString());
                        return;
                    } catch (InterruptedException e) {

                    }

                }
            }
        };
        PCreceiver.start();

        System.out.println("\n PC: Enter Commands (help or ? for commands list)");

        do {
            try {
                input = scanner.nextLine();
                if (input.equalsIgnoreCase("help") || input.equals("?")) {
                    System.out.println(getCommandHelp());
                } else {
                    start = System.currentTimeMillis();
                    String message = createCommand(input);
                    if (message.equals("")) {
                        System.out.println("Maformed Input, try again.");
                    } else {
                        oHandle.write(message.getBytes());
                        oHandle.flush();
                        latency = System.currentTimeMillis() - start;

                        System.out.println("\nPC: " + input + " [" + latency + "ms]");
                    }
                }
            } catch (IOException e) {
                System.out.println("Fail to write to oHandle bc "
                        + e.toString());
                return;
            }
        } while (!input.equalsIgnoreCase("exit"));

        try {
            connection.close();
            readFlag = false; // stop reading threads
            // stop all threads as well
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Ending session...");
    }

    public static String createCommand(String cmd) {
        // TO DO: Create commands based on the input...
        String message = "";
        String[] cmdWords = cmd.split(" ");
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
			if (args[0].equalsIgnoreCase("forward") || args[0].equalsIgnoreCase("forwards")) {
				command += "F";
			} else if (args[0].equalsIgnoreCase("backward") || args[0].equalsIgnoreCase("backwards")) {
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
			if (args[0].equalsIgnoreCase("forward") || args[0].equalsIgnoreCase("forwards")) {
				command += "F";
			} else if (args[0].equalsIgnoreCase("backward") || args[0].equalsIgnoreCase("backwards")) {
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
