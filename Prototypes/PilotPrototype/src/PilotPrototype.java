import lejos.nxt.*;
import lejos.robotics.navigation.*;

public class PilotPrototype {
	public static void main(String[] args){
		System.out.println("Testing move");
		DifferentialPilot driver = new DifferentialPilot(2.1f, 4.4f, Motor.B, Motor.C, true);
		System.out.println("Moving forward");
		driver.travel(5);
		System.out.println("Moving backwards");
		driver.travel(-5);
		System.out.println("Turning Left");
		driver.rotate(90);
		System.out.println("Turning Right");
		driver.rotate(-90);
		System.out.println("Done");
	}
}
