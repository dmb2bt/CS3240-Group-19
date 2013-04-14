

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
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

		driver = new Driver();
		messageHandler = new MessageHandler();
		readPipe = connection.openDataInputStream();
		writePipe = connection.openDataOutputStream();
		String input = "";
		do {
			try {
				int count = readPipe.read(buffer);
				if (count > 0) {
					input = (new String(buffer)).substring(0,11);
					System.out.println(input);
					ArrayList<String> commandData = messageHandler.decodeMessage(input);
					if(commandData.size() < 1){
						System.out.println("Invalid Message");
					} else {
						sendMessage(messageHandler.createACK());
					}
					if(commandData.get(0).equals("exit")) System.exit(0);
					ArrayList<String> sensorData = driver.implementCommand(commandData);
					if(sensorData.size() > 1){
						sendMessage(messageHandler.encodeMessage(sensorData));
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
		} catch (IOException e) {
			System.out.println("Write error: " + e);
		}
	}

}
