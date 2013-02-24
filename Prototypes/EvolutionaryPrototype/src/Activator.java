import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import lejos.nxt.SoundSensor;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.USB;
import lejos.robotics.Touch;
import lejos.robotics.navigation.DifferentialPilot;

public class Activator extends Object {

	private static boolean USBtest = false;

	// Initialize all sensors here
	private static Touch sTouch = new TouchSensor(SensorPort.S1);
	private static SoundSensor sSound = new SoundSensor(SensorPort.S2);
	private static LightSensor sLight = new LightSensor(SensorPort.S3);
	private static UltrasonicSensor sUltra = new UltrasonicSensor(SensorPort.S4);

	// DifferentialPilot is our motion controller, we will exclusively use the
	// object to navigate the robot around
	// the first two arguments are wheel diameters and track width (in cm)
	// respectively
	// last two arguments are left and right motors respectively
	private static DifferentialPilot pilot = new DifferentialPilot(2.25f, 5.5f,
			Motor.B, Motor.C);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Waiting...");

		// Establish the connection here, for testing purpose, we will use USB
		// connection
		NXTConnection connection = null;
		if (USBtest) {
			connection = USB.waitForConnection();
		} else {
			connection = Bluetooth.waitForConnection();
		}
		// An additional check before opening streams
		if (connection == null) {
			System.out.println("Failed");
		} else {
			System.out.println("Connected");
		}

		// Open two data input and output streams for read and write
		// respectively
		final DataOutputStream oHandle = connection.openDataOutputStream();
		final DataInputStream iHandle = connection.openDataInputStream();
		String input = "", output = "";

		// Register a listener to port S1 which is the Touch sensor at the back
		SensorPort.S1.addSensorPortListener(new SensorPortListener() { // Listener's
																		// style

					@Override
					public void stateChanged(SensorPort arg0, int arg1, int arg2) {
						try {
							if (sTouch.isPressed()) {
								String str = "ALERT: bump into something, stopping all actions";
								oHandle.write(str.getBytes());
								oHandle.flush();

							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						pilot.stop();
					}

				});

		// SensorPort.S4.addSensorPortListener(new SensorPortListener() { //
		// sensor 4 somehow doesnt get this event
		//
		// @Override
		// public void stateChanged(SensorPort arg0, int arg1, int arg2) {
		// if (sUltra.getDistance() < 30){
		// pilot.stop();
		// String str = "ALERT: obstacle ahead, stopping all actions";
		// try {
		// oHandle.write(str.getBytes());
		// oHandle.flush();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// pilot.stop();
		// }
		//
		// }
		//
		// });

		// setup threading for listener (Threading style)
		// (new Thread() {
		// public void run(){
		// while (true){
		// if (sTouch.isPressed()){
		// try {
		// oHandle.writeUTF("ALERT: bump into something, stopping all actions");
		// pilot.stop();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// }
		// }).start();

		do {
			try {
				byte[] buffer = new byte[256]; // allocate a buffer of max size
												// 256 bytes
				int count = iHandle.read(buffer); // pass the buffer to the
													// input handle to read
				if (count > 0) { // check if number of bytes read is more than
									// zero
					input = (new String(buffer)).trim(); // convert back to
															// string and trim
															// down the blank
															// space
					output = performAction(input); // perform arbitrary actions

					String str = output + " OK";
					oHandle.write(str.getBytes()); // ACK
					oHandle.flush(); // flush the output bytes
				}
				Thread.sleep(10);

			} catch (Exception e) {
				System.out.println(" write error " + e);
				System.exit(1);
			}
		} while (!input.equalsIgnoreCase("exit"));

		System.out.println("Ending session...");
		try {
			oHandle.close();
			iHandle.close();
			connection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * Perform different actions based on the command
	 */
	private static String performAction(String cmd) {
		System.out.println("PC: " + cmd);
		String output = cmd;
		if (cmd.equalsIgnoreCase("forward")) {
			pilot.forward();
			output = "Traveling at: " + pilot.getTravelSpeed();
		} else if (cmd.equalsIgnoreCase("backward")) {
			pilot.backward();
			output = "Traveling at: " + pilot.getTravelSpeed();
		} else if (cmd.equalsIgnoreCase("stop")) {
			pilot.stop();
			output = "Distance traveled: "
					+ pilot.getMovement().getDistanceTraveled();
		} else if (cmd.toLowerCase().startsWith("setspeed")) {
			int speed = decodeSetSpeed(cmd);
			if(speed <= 0){
				output = "Invalid value for speed";
			} else {
				pilot.setTravelSpeed(speed);
				output = "Speed Set to: " + speed + "cm/s.";
			}
		} else if (cmd.equalsIgnoreCase("status")) { // String.format does not
														// work
			output = "\nTouch sensor: "
					+ ((sTouch.isPressed()) ? "Pressed" : "Not Pressed");
			output += "\nSound sensor: " + sSound.readValue();
			output += "\nLight sensor: " + sLight.getLightValue();
			output += "\nUltra sensor: " + sUltra.getDistance();
			output += "\n";
		} else if (cmd.equalsIgnoreCase("turnRight")) {
			pilot.rotate(90);
		} else if (cmd.equalsIgnoreCase("turnLeft")) {
			pilot.rotate(-90);
		}else if (cmd.equalsIgnoreCase("right")){
			pilot.rotateRight();
		}else if (cmd.equalsIgnoreCase("left")){
			pilot.rotateLeft();
		}else if (cmd.equalsIgnoreCase("arc right")){
			pilot.arcForward(90);
		}else if (cmd.equalsIgnoreCase("arc left")){
			pilot.arcForward(-90);
		}
		return output;

	}

	private static int decodeSetSpeed(String cmd) {
		if(cmd.length() <= 9){
			return -1;
		}else {
			int ret = Integer.parseInt(cmd.substring(9));
			if (ret <= 0){
				return -1;
			}
			return ret;
		}
	}

}
