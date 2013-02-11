package Commands;

import java.util.ArrayList;


public class CommandFactory {
	private static String stackCommandTermination = ".";
	
	
	public static ArrayList<iCommand> getInstance(String arg){
		if (!arg.endsWith(stackCommandTermination)){
			return null;
		}
		
		String[] cList = arg.substring(0, arg.length()-1) .split(";");
		
		return new ArrayList<iCommand>();
		
	}
}
