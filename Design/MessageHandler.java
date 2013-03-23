/*
* This class abstracts away the implementation of the communications protocol
* This class contains methods that are required to decode and encode various
* messages that the robot needs to send to the base station.
*/

public Class MessageHandler{
	
	/*
	* decodeMessage takes a message and decodes into parameters for 
	* the Driver to use.
	*/
	public String[] decodeMessage(String message);
	
	/*
	* encodeMessage uses parameters from the Driver to create a message
	* to be sent to the base station.
	*/
	public String encodeMessage(String[] message);
	
	/*
	* Verify checksum verifies if the calculated checksum is equivalent
	* to the checksum sent in the message
	*/
	private boolean verifyChecksum(String message);
	
	/*
	* Calculates the checksum of the provided message
	*/
	private String getChecksum(String message);
}