// Import AWT classes
import java.awt.*;
// Import AWT event classes
import java.awt.event.*;
// Import Swing classes
import javax.swing.*;
import javax.swing.border.LineBorder;

public class UI{
	JFrame f;
	
	public UI(){
		f = new JFrame();
		f.getContentPane().setLayout(null);
		f.setSize(new Dimension(950, 600));
		
		JPanel panel = new ImagePanel("im1.jpg");;
//		panel.setBackground(Color.WHITE);
		panel.setBounds(32, 35, 544, 356);
		f.getContentPane().add(panel);
		
		JLabel lblCameraView = new JLabel("Camera View");
		lblCameraView.setBounds(32, 13, 88, 16);
		f.getContentPane().add(lblCameraView);
		
		JPanel panel_1 = new ImagePanel("im2.jpg");
//		panel_1.setBackground(Color.WHITE);
		panel_1.setBounds(601, 35, 154, 120);
		f.getContentPane().add(panel_1);
		
		JLabel lblOverheadMap = new JLabel("Overhead Map");
		lblOverheadMap.setBounds(601, 13, 88, 16);
		f.getContentPane().add(lblOverheadMap);
		
		JTextPane txtpnMovingForwardAt = new JTextPane();
		txtpnMovingForwardAt.setEditable(false);
		txtpnMovingForwardAt.setBorder(null);
		txtpnMovingForwardAt.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtpnMovingForwardAt.setText("TOUCH SENSED FRONT\r\nSPEED INCREASED TO +.5\r\nMOVING FORWARD AT +.25\r\nTURNING LEFT. . . 80\u00B0");
		txtpnMovingForwardAt.setBounds(601, 207, 154, 183);
		f.getContentPane().add(txtpnMovingForwardAt);
		
		JLabel lblLog = new JLabel("Data Log");
		lblLog.setBounds(601, 179, 56, 16);
		f.getContentPane().add(lblLog);
		
		JTextPane txtpnW = new JTextPane();
		txtpnW.setEditable(false);
		txtpnW.setBorder(new LineBorder(Color.BLUE, 2));
		txtpnW.setBackground(Color.WHITE);
		txtpnW.setText("W");
		txtpnW.setBounds(87, 433, 43, 41);
		f.getContentPane().add(txtpnW);
		
		JTextPane txtpnA = new JTextPane();
		txtpnA.setEditable(false);
		txtpnA.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		txtpnA.setText("A");
		txtpnA.setBounds(32, 480, 43, 41);
		f.getContentPane().add(txtpnA);
		
		JTextPane txtpnS = new JTextPane();
		txtpnS.setEditable(false);
		txtpnS.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		txtpnS.setText("S");
		txtpnS.setBounds(87, 480, 43, 41);
		f.getContentPane().add(txtpnS);
		
		JTextPane txtpnD = new JTextPane();
		txtpnD.setEditable(false);
		txtpnD.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		txtpnD.setText("D");
		txtpnD.setBounds(145, 480, 43, 41);
		f.getContentPane().add(txtpnD);
		
		JLabel lblDirection = new JLabel("Direction");
		lblDirection.setBounds(36, 410, 56, 16);
		f.getContentPane().add(lblDirection);
		
		JLabel lblSpeed = new JLabel("Speed");
		lblSpeed.setBounds(234, 410, 56, 16);
		f.getContentPane().add(lblSpeed);
		
		JTextPane txtpnMsec = new JTextPane();
		txtpnMsec.setBorder(new LineBorder(new Color(0, 0, 0)));
		txtpnMsec.setText(".5 m/sec");
		txtpnMsec.setBounds(234, 433, 88, 25);
		f.getContentPane().add(txtpnMsec);
		
		JButton btne = new JButton("+ (\u2191)");
		btne.setBounds(234, 471, 88, 25);
		f.getContentPane().add(btne);
		
		JButton btnNewButton = new JButton("- (\u2193)");
		btnNewButton.setActionCommand("-");
		btnNewButton.setBounds(234, 500, 88, 25);
		f.getContentPane().add(btnNewButton);
		
		JLabel lblSensors = new JLabel("Sensors");
		lblSensors.setBounds(432, 410, 56, 16);
		f.getContentPane().add(lblSensors);
		
		JLabel lblSensorTouch = new JLabel("1: Touch");
		lblSensorTouch.setBounds(432, 433, 71, 16);
		f.getContentPane().add(lblSensorTouch);
		
		JLabel lblTouch = new JLabel("2: Touch");
		lblTouch.setBounds(525, 433, 71, 16);
		f.getContentPane().add(lblTouch);
		
		JLabel lblLight = new JLabel("3: Light");
		lblLight.setBounds(611, 433, 71, 16);
		f.getContentPane().add(lblLight);
		
		JLabel lblSound = new JLabel("4: Sound");
		lblSound.setBounds(694, 433, 71, 16);
		f.getContentPane().add(lblSound);
		
		JLabel lblMotors = new JLabel("Motors");
		lblMotors.setBounds(779, 410, 56, 16);
		f.getContentPane().add(lblMotors);
		
		JTextPane txtpnARunning = new JTextPane();
		txtpnARunning.setEditable(false);
		txtpnARunning.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtpnARunning.setText(" A - Running");
		txtpnARunning.setBorder(new LineBorder(Color.BLUE, 2));
		txtpnARunning.setBackground(Color.WHITE);
		txtpnARunning.setBounds(779, 431, 141, 25);
		f.getContentPane().add(txtpnARunning);
		
		JTextPane txtpnBRunning = new JTextPane();
		txtpnBRunning.setEditable(false);
		txtpnBRunning.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtpnBRunning.setText(" B - Running");
		txtpnBRunning.setBorder(new LineBorder(Color.BLUE, 2));
		txtpnBRunning.setBackground(Color.WHITE);
		txtpnBRunning.setBounds(779, 462, 141, 25);
		f.getContentPane().add(txtpnBRunning);
		
		JTextPane txtpnCDisconnected = new JTextPane();
		txtpnCDisconnected.setEditable(false);
		txtpnCDisconnected.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtpnCDisconnected.setText(" C - Disconnected");
		txtpnCDisconnected.setBorder(new LineBorder(Color.BLACK));
		txtpnCDisconnected.setBackground(Color.WHITE);
		txtpnCDisconnected.setBounds(779, 496, 141, 25);
		f.getContentPane().add(txtpnCDisconnected);
		
		JTextPane txtpnTouched = new JTextPane();
		txtpnTouched.setEditable(false);
		txtpnTouched.setText("\r\nTOUCHED");
		txtpnTouched.setBorder(new LineBorder(Color.BLUE, 2));
		txtpnTouched.setBackground(Color.WHITE);
		txtpnTouched.setBounds(432, 462, 61, 59);
		f.getContentPane().add(txtpnTouched);
		
		JTextPane txtpnAwaitingInput = new JTextPane();
		txtpnAwaitingInput.setText("\r\n     --   --");
		txtpnAwaitingInput.setEditable(false);
		txtpnAwaitingInput.setBorder(new LineBorder(Color.BLACK, 2));
		txtpnAwaitingInput.setBackground(Color.WHITE);
		txtpnAwaitingInput.setBounds(525, 462, 61, 59);
		f.getContentPane().add(txtpnAwaitingInput);
		
		JTextPane txtpnOff = new JTextPane();
		txtpnOff.setEditable(false);
		txtpnOff.setText("\r\n     OFF");
		txtpnOff.setBorder(new LineBorder(Color.GRAY, 2));
		txtpnOff.setBackground(Color.WHITE);
		txtpnOff.setBounds(611, 462, 61, 59);
		f.getContentPane().add(txtpnOff);
		
		JTextPane txtpnDb = new JTextPane();
		txtpnDb.setEditable(false);
		txtpnDb.setText("\r\n    10 dB");
		txtpnDb.setBorder(new LineBorder(Color.BLUE, 2));
		txtpnDb.setBackground(Color.WHITE);
		txtpnDb.setBounds(694, 462, 61, 59);
		f.getContentPane().add(txtpnDb);
		
		JLabel lblRobotStatusOnline = new JLabel("ROBOT STATUS: ONLINE");
		lblRobotStatusOnline.setBorder(new LineBorder(Color.GREEN));
		lblRobotStatusOnline.setBounds(766, 13, 154, 16);
		f.getContentPane().add(lblRobotStatusOnline);
		
		JButton btnSetRoute = new JButton("Set Route");
		btnSetRoute.setBounds(767, 207, 153, 25);
		f.getContentPane().add(btnSetRoute);
		
		JRadioButton rdbtnHigh = new JRadioButton("High");
		rdbtnHigh.setBounds(330, 444, 127, 25);
		f.getContentPane().add(rdbtnHigh);
		
		JRadioButton rdbtnMedium = new JRadioButton("Medium");
		rdbtnMedium.setBounds(330, 471, 127, 25);
		f.getContentPane().add(rdbtnMedium);
		
		JRadioButton rdbtnLow = new JRadioButton("Low");
		rdbtnLow.setBounds(330, 500, 127, 25);
		f.getContentPane().add(rdbtnLow);
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(rdbtnHigh);
		bg.add(rdbtnMedium);
		bg.add(rdbtnLow);
		
		JCheckBox chckbxPowerToRobot = new JCheckBox("Power to Robot");
		chckbxPowerToRobot.setBounds(763, 35, 157, 25);
		f.getContentPane().add(chckbxPowerToRobot);
		
		JRadioButton chckbxFollowRoute = new JRadioButton("Follow Route");
		chckbxFollowRoute.setBounds(767, 306, 157, 25);
		f.getContentPane().add(chckbxFollowRoute);
		
		JRadioButton chckbxFollowLine = new JRadioButton("Follow Line");
		chckbxFollowLine.setBounds(767, 336, 157, 25);
		f.getContentPane().add(chckbxFollowLine);
		
		JRadioButton chckbxRunAutopilot = new JRadioButton("Run Autopilot");
		chckbxRunAutopilot.setBounds(767, 366, 157, 25);
		f.getContentPane().add(chckbxRunAutopilot);
		
		
		JButton btnSetLine = new JButton("Set Line");
		btnSetLine.setBounds(767, 245, 153, 25);
		f.getContentPane().add(btnSetLine);
		
		JRadioButton rdbtnManualControl = new JRadioButton("Manual Control");
		rdbtnManualControl.setBounds(767, 276, 157, 25);
		f.getContentPane().add(rdbtnManualControl);
		
		ButtonGroup bg2 = new ButtonGroup();
		bg2.add(chckbxFollowRoute);
		bg2.add(chckbxFollowLine);
		bg2.add(chckbxRunAutopilot);
		bg2.add(rdbtnManualControl);
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	public static void main(String[] args){
		UI ui = new UI();
	}
}
