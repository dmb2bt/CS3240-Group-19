package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.USB;

public class Activator extends Object {
	private boolean debugMode = false;
	private static boolean usbTest = false;
	private static NXTConnection connection;
	private static byte[] buffer = new byte[256];
	private static DataInputStream readPipe;
	private static DataOutputStream writePipe;
	private static Driver driver;
	private static MessageHandler messageHandler;

	public static void main(String[] args) {
		boolean connected = false;
		do {
			connected = createConnection();
		} while (connected == false);

		readPipe = connection.openDataInputStream();
		writePipe = connection.openDataOutputStream();

		driver = new Driver();
		messageHandler = new MessageHandler();
		Thread NXTReceiver = new Thread() {
			public void run() {
				try {
					int count = readPipe.read(buffer);
					if (count > 0) {
						String input = (new String(buffer)).trim();
						ArrayList<String> commandData = messageHandler
								.decodeMessage(input);
						if (!commandData.get(0).equals("end")) {
							driver.implementCommand(commandData);
						}
					}
					Thread.sleep(10);
				} catch (Exception e) {

				}
			}
		};
		NXTReceiver.start();
		while (true) {

		}
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
			writePipe.write(message.getBytes());
			writePipe.flush();
		} catch (IOException e) {
			System.out.println("Write error: " + e);
		}
	}

}
