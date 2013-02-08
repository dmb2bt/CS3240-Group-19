import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.Motor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.USB;
import lejos.robotics.navigation.DifferentialPilot;

public class Activator extends Object{

	private static boolean USBtest = false;
	private static DifferentialPilot pilot = new DifferentialPilot(2.25f, 5.5f, Motor.B, Motor.C);
	
	public static void main(String[] args) {
		System.out.println("Waiting...");
		
		// Establish the connection here, for testing purpose, we will use USB connection
		NXTConnection connection = null;
		if (USBtest){
			connection = USB.waitForConnection();
		} else {
			connection = Bluetooth.waitForConnection();
		}
		// An additional check before opening streams
		if (connection==null){
			System.out.println("Failed");
		} else {
			System.out.println("Connected");
		}
		
		// Open two data input and output streams for read and write respectively
	    final DataOutputStream oHandle = connection.openDataOutputStream();
	    final DataInputStream iHandle = connection.openDataInputStream();
	    String input = "",output = "";
	    
	    //Main Program Loop
	    //Read in input from computer and execute command on robot
	    //Currently only support move forward, and turn
	    do {
	    	try {
	    		byte[] buffer = new byte[256]; // allocate a buffer of max size 256 bytes
	    		int count = iHandle.read(buffer); // pass the buffer to the input handle to read
	    		if (count>0){ // check if number of bytes read is more than zero
	    		input = (new String(buffer)).trim(); // convert back to string and trim down the blank space
	    		output=performAction(input); // perform arbitrary actions
	    		
	    		String str = output+" OK";
	    		oHandle.write(str.getBytes()); // ACK
	    		oHandle.flush(); // flush the output bytes 
	    		}
	    		Thread.sleep(10);
	    		
	  	    } catch (Exception e ) {
	  	      System.out.println(" write error "+e); 
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
		System.out.println("PC: "+cmd);
		String output=cmd;
		if (cmd.equalsIgnoreCase("forward")){
			pilot.travel(10);
			output = "Traveling at: "+pilot.getTravelSpeed();
		} else if (cmd.equalsIgnoreCase("turnRight")){
			pilot.rotate(90);
		} else if (cmd.equalsIgnoreCase("turnLeft")){
			pilot.rotate(-90);
		}
		return output;
		
	}
	
}

