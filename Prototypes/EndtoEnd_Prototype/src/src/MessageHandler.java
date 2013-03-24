package src;

public class MessageHandler {
	
	 public MessageHandler() {	 
	 }
	 
	 public String[] decodeMessage(String message){
		 if(message.length() != 11){
			 return new String[]{"nul"};
		 } else {
			//Decode the message
		 }
		 
		 return new String[1];
	 }
	 
	 public String encodeMessage(String[] message){
		 
		 return "";
	 }
	
	 private boolean verifyChecksum(String message){
		 return true;
	 }
	 
	 private String getChecksum(String message){
		 return "";
	 }
}
