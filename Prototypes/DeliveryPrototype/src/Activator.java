import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.USB;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class Activator extends Object {

	private static boolean debugMode = false;
	private static boolean usbTest = false;

	private static NXTConnection connection;
	private static byte[] buffer = new byte[256];
	private static DataInputStream readPipe;
	private static DataOutputStream writePipe;
	private static Driver driver;
	private static MessageHandler messageHandler;
	private static ArrayList<ArrayList<String>> storedCommands;

	public static void main(String[] args) {
		boolean connected = false;
		do {
			connected = createConnection();
		} while (connected == false);

		driver = new Driver();
		messageHandler = new MessageHandler();
		readPipe = connection.openDataInputStream();
		writePipe = connection.openDataOutputStream();
		storedCommands = new ArrayList<ArrayList<String>>();
		String input = "";

		(new Thread() {
			public void run() {
				while (true) {
					ArrayList<String> sensorData = driver.safetySense();
					if (sensorData.size() > 1) {
						sendMessage(messageHandler.encodeMessage(sensorData));
					}
				}
			}
		}).start();

		do {
			try {
				int count = readPipe.read(buffer);
				if (count > 0) {
					input = (new String(buffer)).substring(0, 11);
					if (!messageHandler.isAck(input)) {
						ArrayList<String> commandData = messageHandler
								.decodeMessage(input);

						if (commandData.size() < 1) {
							System.out.println("Invalid Message");
						} else {
							sendMessage(messageHandler.createACK());
						}
						if (commandData.get(0).equals("exit"))
							System.exit(0);
						if (commandData.get(0).equalsIgnoreCase("mode"))
							debugMode = Boolean
									.parseBoolean(commandData.get(1));
						ArrayList<String> sensorData = driver
								.implementCommand(commandData);
						if (sensorData.size() > 1) {
							sendMessage(messageHandler
									.encodeMessage(sensorData));
						}
					}
				}
				Thread.sleep(10);
			} catch (Exception e) {

			}
		} while (!input.equals("exit"));
	}

	public static boolean createConnection() {
		System.out.println("Waiting on Connection...");
		if (usbTest) {
			connection = USB.waitForConnection();
		} else {
			connection = Bluetooth.waitForConnection();
		}
		if (connection != null) {
			System.out.println("Connected!");
			return true;
		}
		System.out.println("Failed to Connect");
		return false;
	}

	public static void sendMessage(String message) {
		try {
			System.out.println("Send Message");
			System.out.println(message);
			writePipe.write(message.getBytes());
			writePipe.flush();
		} catch (Exception e) {
			System.out.println("Write error: " + e.getMessage());
		}
	}

}
