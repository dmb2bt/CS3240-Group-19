package src;
import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;



public class Driver {
	private DifferentialPilot pilot;
	final int DEFAULT_RADIUS = 90;

	public Driver(){
		pilot = new DifferentialPilot(2.25f, 5.5f, Motor.B, Motor.C);
	}
	
	public String[] implementCommand(String[] command){
		
		return new String[1];
	}
	
	private boolean moveStraight(boolean forward, int distance){
		if(forward){
			if(distance == 0){
				pilot.forward();
				return true;
			}else {
				pilot.travel(distance);
				return true;
			}
		}else {
			if(distance == 0){
				pilot.backward();
				return true;
			}else {
				pilot.travel(-distance);
				return true;
			}
		}
	}
	
	private boolean moveArc(boolean forward, boolean right, int distance, int radius){
		
		return true;
	}
	
	private boolean turn(boolean right, int radius){
		
		return true;
	}
	
	private boolean stop(){
		pilot.stop();
		return true;
	}
	
	private String[] read(int sensorNumber){
		
		return new String[1];
	}
	
	
}
