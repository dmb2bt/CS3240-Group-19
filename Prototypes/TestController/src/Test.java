import java.util.Scanner;

public class Test {
	public static void main(String[] args) {		
		Scanner in = new Scanner(System.in);
		String input = "";
		
		do {
			System.out.println("Enter a command...");
			input = in.nextLine();
			if (input.equalsIgnoreCase("help") || input.equals("?")) {
				System.out.println(TestController.getCommandHelp());
			} else {
				String message = TestController.createCommand(input);
				if (message.equals("")) {
                    System.out.println("Maformed Input, try again.");
                } else if (message.length() < 10){
                	System.out.println("Message length is too short to be correct!");
                } else {
    				System.out.println(message);
                    System.out.println("Number of Bytes: " + message.getBytes().length);
                }
			}
		} while (!input.equalsIgnoreCase("exit"));
		System.out.println("Exiting command creation test...");
	}
}
	