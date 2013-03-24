/*
* This class is designed to handle the connection and activating 
* both driving of the robot hardware and message handling.
*/
public class Activator {
	//Driver that controls the hardware side of robot
	private Driver driver;
	
	//MessageHandler that creates, encodes, and decodes messages to be sent
	private MessageHandler messageHandler;
	
	//boolean used to determine whether to allow debugCommands or not
	private boolean debugMode;
	
	//Pipes for reading and writing messages to and from the base station
	private DataInputStream readPipe;
	private DataOutputStream writePipe;
	
	/*
	* NXTConnection that acts as the bluetooth connection between base station
	* and robot
	*/
	private NXTConnection connection;
	
	//buffer used for reading from the stream
	private byte[256] buffer;
	
	/*
	* main method that controls the creation of connection and actual running
	* of the robot system
	*/
	public static void main(String[] args);
	
	/*
	* creates the connection between robot and base station
	* allows for multiple connections to be made
	*/
	public boolean createConnection();
	
	/*
	* method that sends message created by messageHandler to base station
	* message is a message created by messageHandler
	*/
	public void sendMessage(String message);
	
	/*
	* Method that creates the timer for checking timeouts on messages
	*/
	public void timer();
}
