import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultStyledDocument;
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
import java.util.ArrayList;
import java.awt.Font;


public class DebuggerShell implements KeyListener {
	
	private JFrame f;
	private JTextPane textPane;
	private String history;
	
	private ArrayList<Point> commands;
	
	//sorry this is a super lazy solution to what I'm doing
	private Robot robot;
	
	public DebuggerShell(){
		f = new JFrame();
		commands = new ArrayList<Point>();
		
		//lazy solution
		try {
			robot = new Robot();
		} catch (AWTException e) {
		}
		
		history = "";
		
		f.setTitle("NXT Debugger");
		textPane = new JTextPane();
		textPane.setFont(new Font("Courier New", Font.PLAIN, 14));
		f.getContentPane().add(textPane, BorderLayout.CENTER);
		f.setVisible(true);
		f.setSize(500, 500);
		textPane.addKeyListener(this);
		
		printMessage("Debugger Started.");
	}
	
	private void runCommand(String command){
		//temporary code, normally would implement the command
		printMessage("Running: " + command);
		
	}
	
	private void printMessage(String message){
		history += (message+ "\n>> ");
		textPane.setText(history);
		textPane.setCaretPosition(textPane.getText().length());
		color();
	}
	
	public static void main(String[] args){
		DebuggerShell ds = new DebuggerShell();
	}
	
	public void color(){
		StyledDocument sDoc = textPane.getStyledDocument();
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLUE);
		for(Point p: commands){
			sDoc.setCharacterAttributes(p.x, p.y, aset, true);
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == 10){
			int oldLength = history.length();
			history = textPane.getText() + "\n";
			String command = textPane.getText().substring(oldLength, textPane.getText().length());
			commands.add(new Point(oldLength, command.length()));
			
			textPane.setText(history);
			textPane.setCaretPosition(history.length() - 1);
			color();
			
			//lazy solution
			robot.keyPress(8);
			
			runCommand(command);
		}
		else if(arg0.getKeyCode() == 8){
			if(textPane.getCaretPosition() <= history.length()){
				textPane.setText(history + " ");
				textPane.setCaretPosition(history.length() + 1);
				color();
			}
		}else{
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}

}
