/*
* This class abstracts away the implementation of the communications protocol
* This class contains methods that are required to decode and encode various
* messages that the robot needs to send to the base station.
*/

public Class MessageHandler{
	
	/*
	* decodeMessage takes a message and decodes into parameters for 
	* the Driver to use.
	* Parameter message is the message to be decoded
	*/
	public ArrayList<String> decodeMessage(String message);
	
	/*
	* encodeMessage uses parameters from the Driver to create a message
	* to be sent to the base station.
	* Parameter message is ArrayList of Strings to be used to crease message
	*/
	public String encodeMessage(ArrayList<String> message);
	
	/*
	* Verify checksum verifies if the calculated checksum is equivalent
	* to the checksum sent in the message
	* Parameter message is String on which to check checksum
	*/
	private boolean verifyChecksum(String message);
	
	/*
	* Calculates the checksum of the provided message
	* Parameter message is the message on which to get the checksum
	*/
	private String getChecksum(String message);
	
	/*
	* Checks to see if number is of a numeric type (i.e. it can be converted to number)
	* Parameter number is the String to check whether the number is a boolean
	*/
	private boolean isNumeric(String number);
}