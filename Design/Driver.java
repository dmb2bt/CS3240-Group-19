/*This class hides the design decisions behind how to control the actual
*functionality of the robot.
*/

public Class Driver{
	private DifferentialPilot pilot;
	
	//creates the DifferentialPilot 
	public Driver();
	
	/*
	* public method that implements commands
	* command an array that breaks down each parameter avaible for any command
	* type
	*/
	public String[] implementCommand(String[] command);
	
	/*
	* private method that hides how movement in a straight direction works
	* boolean forward move robot forward when true, backwards when false
	* distance is the distance for the robot to move
	*/
	private boolean moveStraight(boolean forward, int distance);
	
	/*
	* private method that hides how movement in an arc works
	* boolean forward moves robot forward in arc when true, and backwards when false
	* boolean right arcs the robot to the right when true, left when false
	* distance determines the distance for the robot to move
	* radius determines the radius to move along
	*/
	private boolean moveArc(boolean forward, boolean right, int distance, int radius);
	
	/*
	* private method that hides how turning works
	* boolean right turns the robot right when true, and left when false
	* radius determines what radius in degrees to turn
	*/
	private boolean turn(boolean right, int radius);
	
	/*
	* private method stop abstracts how stopping works
	*/
	private boolean stop();
	
	/*
	* private method that hides how setting speed works
	* int combination determines which motor or motor combination to set speed for
	* newSpeed determines the new speed to set to
	*/
	private boolean setSpeed(int combination, int newSpeed);
	
	/*
	* private method read controls reading a sensor
	* int sensor number determines the sensor to read from
	*/
	private ArrayList<String> read(int sensorNumber);
	
	/*
	* Does nothing, no operation
	*/
	private boolean noOp();
	}