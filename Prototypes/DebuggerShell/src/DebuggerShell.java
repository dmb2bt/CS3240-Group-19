//CS3240g8b
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.awt.Font;
import javax.swing.SpringLayout;
import java.awt.Component;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import lejos.pc.comm.NXTCommFactory;
import javax.swing.JTextField;
import javax.swing.JLabel;


public class DebuggerShell implements KeyListener, ActionListener, MouseListener {
	
	private JFrame f;
	private JTextPane textPane;
	private String history;
	private Debugger debugger;
	
	private ArrayList<Point> commandFormat;
	private ArrayList<Point> messageFormat;

	private JTextField textField;
	private JTextField textField_1;
	private JScrollPane scrollPane;
	private JTextField txtLightUnread;
	private JTextField txtSoundUnread;
	private JTextField txtUltrasonicUnread;
	private JTextField txtTouchUnread;
	
	public DebuggerShell(Debugger d){
		this.debugger = d;
		
		f = new JFrame();
		commandFormat = new ArrayList<Point>();
		messageFormat = new ArrayList<Point>();
		
		history = "";
		
		f.setTitle("NXT Debugger");
		f.getContentPane().setLayout(null);
		
		JButton btnEstablishConnection = new JButton("Establish Connection");
		btnEstablishConnection.setBounds(516, 13, 191, 25);
		f.getContentPane().add(btnEstablishConnection);
		btnEstablishConnection.addActionListener(this);
		
		JButton btnEndConnection = new JButton("End Connection");
		btnEndConnection.setBounds(516, 51, 191, 25);
		f.getContentPane().add(btnEndConnection);
		btnEndConnection.addActionListener(this);
		
		JButton btnForward = new JButton("Forward");
		btnForward.addMouseListener(this);
		btnForward.setBounds(516, 422, 191, 25);
		f.getContentPane().add(btnForward);
		btnForward.addActionListener(this);
		
		JButton btnLeft = new JButton("Left");
		btnLeft.addMouseListener(this);
		btnLeft.setBounds(516, 460, 89, 25);
		f.getContentPane().add(btnLeft);
		btnLeft.addActionListener(this);
		
		JButton btnRight = new JButton("Right");
		btnRight.addMouseListener(this);
		btnRight.setBounds(617, 460, 90, 25);
		f.getContentPane().add(btnRight);
		btnRight.addActionListener(this);
		
		JButton btnBackward = new JButton("Backward");
		btnBackward.addMouseListener(this);
		btnBackward.setBounds(516, 497, 191, 25);
		f.getContentPane().add(btnBackward);
		btnBackward.addActionListener(this);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 13, 492, 472);
		f.getContentPane().add(scrollPane);
		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setBounds(12, 13, 492, 509);
		textPane.setFont(new Font("Courier New", Font.PLAIN, 14));
		scrollPane.setViewportView(textPane);
		
		textField = new JTextField();
		textField.setBounds(36, 498, 468, 22);
		f.getContentPane().add(textField);
		textField.setColumns(10);
		textField.addKeyListener(this);
		
		textField_1 = new JTextField();
		textField_1.setEditable(false);
		textField_1.setText(">>");
		textField_1.setBounds(12, 498, 24, 22);
		f.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblSensorsclickTo = new JLabel("Sensors");
		lblSensorsclickTo.setBounds(516, 89, 154, 16);
		f.getContentPane().add(lblSensorsclickTo);
		
		txtLightUnread = new JTextField();
		txtLightUnread.setEditable(false);
		txtLightUnread.setText("Light: unread");
		txtLightUnread.setBounds(516, 118, 116, 22);
		f.getContentPane().add(txtLightUnread);
		txtLightUnread.setColumns(10);
		
		txtSoundUnread = new JTextField();
		txtSoundUnread.setEditable(false);
		txtSoundUnread.setText("Sound: unread");
		txtSoundUnread.setColumns(10);
		txtSoundUnread.setBounds(516, 153, 116, 22);
		f.getContentPane().add(txtSoundUnread);
		
		txtUltrasonicUnread = new JTextField();
		txtUltrasonicUnread.setEditable(false);
		txtUltrasonicUnread.setText("Ultrasonic: unread");
		txtUltrasonicUnread.setColumns(10);
		txtUltrasonicUnread.setBounds(516, 188, 116, 22);
		f.getContentPane().add(txtUltrasonicUnread);
		
		txtTouchUnread = new JTextField();
		txtTouchUnread.setEditable(false);
		txtTouchUnread.setText("Touch: unread");
		txtTouchUnread.setColumns(10);
		txtTouchUnread.setBounds(516, 223, 116, 22);
		f.getContentPane().add(txtTouchUnread);
		
		JButton btnReadLight = new JButton("Read");
		btnReadLight.setActionCommand("Read Light");
		btnReadLight.setBounds(638, 118, 69, 25);
		btnReadLight.addActionListener(this);
		f.getContentPane().add(btnReadLight);
		
		JButton btnReadSound = new JButton("Read");
		btnReadSound.setActionCommand("Read Sound");
		btnReadSound.setBounds(638, 152, 69, 25);
		btnReadSound.addActionListener(this);
		f.getContentPane().add(btnReadSound);
		
		JButton btnReadUltrasonic = new JButton("Read");
		btnReadUltrasonic.setActionCommand("Read Ultrasonic");
		btnReadUltrasonic.addActionListener(this);
		btnReadUltrasonic.setBounds(638, 187, 69, 25);
		f.getContentPane().add(btnReadUltrasonic);
		
		JButton btnReadTouch = new JButton("Read");
		btnReadTouch.setBounds(638, 222, 69, 25);
		btnReadTouch.setActionCommand("Read Touch");
		btnReadTouch.addActionListener(this);
		f.getContentPane().add(btnReadTouch);
		
		JLabel lblMovement = new JLabel("Movement");
		lblMovement.setBounds(516, 393, 89, 16);
		f.getContentPane().add(lblMovement);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		f.setSize(737, 584);
		
		printMessage("Debugger Started.\nPlease press \"ESTABLISH CONNECTION\" to begin debugging.");
		

		textField.requestFocus();
		textField.requestFocusInWindow();
	}
	
	public void set(String sensor, int value){
		switch(sensor){
		case "U":
			txtUltrasonicUnread.setText("Ultrasonic: " + value);
			break;
		case "M":
			txtSoundUnread.setText("Sound: " + value);
			break;
		case "L":
			txtLightUnread.setText("Light: " + value);
			break;
		case "T":
			txtTouchUnread.setText("Touch: " + (value == 1 ? "true" : "false"));
			break;
		}
	}
	
	
	public void printRobotMessage(String message){
		messageFormat.add(new Point(history.length(), message.length()));
		
		history += (message+ "\n");
		textPane.setText(history);
		color();
	}
	
	
	public void printMessage(String message){
		history += (message+ "\n");
		textPane.setText(history);
		color();
	}
	
	
	private void color(){
		StyledDocument sDoc = textPane.getStyledDocument();
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLUE);
		AttributeSet asetM = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.RED);
		AttributeSet asetT = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.MAGENTA);
		sDoc.setCharacterAttributes(0, "Debugger Started.".length(), asetT, true);
		
		for(Point p: commandFormat){
			sDoc.setCharacterAttributes(p.x, p.y, aset, true);
		}
		for(Point p: messageFormat){
			sDoc.setCharacterAttributes(p.x, p.y, asetM, true);
		}
		JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
		
		Document d = textPane.getDocument();
		textPane.select(d.getLength(), d.getLength());
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == 10){
			int oldLength = history.length();
			String command = textField.getText();
			textField.setText("");
			history += ">> " + command + "\n";
			commandFormat.add(new Point(oldLength, command.length() + 3));
			
			textPane.setText(history);
			color();
			
			debugger.runCommand(command);
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String ac = arg0.getActionCommand();

		if(ac.equals("Establish Connection")){
			debugger.establishConnection();
		} else if(ac.equals("End Connection")){
			debugger.endConnection();
		} else if(ac.equals("Read Sound")){
			debugger.sendMessage(debugger.createCommand("read m"));
		} else if(ac.equals("Read Light")){
			debugger.sendMessage(debugger.createCommand("read l"));
		} else if(ac.equals("Read Ultrasonic")){
			debugger.sendMessage(debugger.createCommand("read u"));
		} else if(ac.equals("Read Touch")){
			debugger.sendMessage(debugger.createCommand("read t"));
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		Component c = arg0.getComponent();
		if(! (c instanceof JButton)) return;
		JButton b = (JButton) c;
		String ac = b.getActionCommand();
		if(ac.equals("Forward")){
			debugger.sendMessage(debugger.createCommand("move forward"));
		} else if(ac.equals("Backward")){
			debugger.sendMessage(debugger.createCommand("move backward"));
		} else if(ac.equals("Left")){
			debugger.sendMessage(debugger.createCommand("turn left"));
		} else if(ac.equals("Right")){
			debugger.sendMessage(debugger.createCommand("turn right"));
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		Component c = arg0.getComponent();
		if(!(c instanceof JButton)) return;
		JButton b = (JButton) c;
		String ac = b.getActionCommand();
		if(ac.equals("Forward") || ac.equals("Backward") || ac.equals("Left") || ac.equals("Right")){
			debugger.sendMessage(debugger.createStopMessage());
		}
	}
}
