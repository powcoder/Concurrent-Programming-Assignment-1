https://powcoder.com
代写代考加微信 powcoder
Assignment Project Exam Help
Add WeChat powcoder
/* 
 * GUI for Concurrent Programming Assignment 1
 * You DO NOT need to change this file
 */

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

// Auxiliary class to buffer key events.


class KeyBuffer extends KeyAdapter {

	static int K = 1000;  
	int in, out, no = 0;

	char[] buf = new char[K];

	synchronized public void keyPressed(KeyEvent e) {
		char c = e.getKeyChar();
		e.consume();
		if (c != KeyEvent.CHAR_UNDEFINED && no < K) { 
			buf[in++] = c;
			in = in % K;
			no++;
			notify();
		}
	}

	synchronized public char Get() {
		while (no==0) try{ wait();} catch (Exception e){}
		char c = buf[out++];
		out = out % K;
		no--;
		return c;
	}

}

// Class that provides a GUI

public class TaskDisplay extends JFrame{

	public JTextField[] tf;
	JTextArea   txt;
	JScrollPane pane;

	static final int width      =   40;
	static final int minhistory = 1000;

	private KeyBuffer kb;

	private void initGUI(int n) {
		addWindowListener(
				new WindowAdapter(){
					public void windowClosing(WindowEvent e) {
						System.exit(0);
					}
				});

		// Create 'n' TextFields

		if (n > 0) tf = new JTextField[n];

		setFont(new Font("Monospaced",Font.BOLD,12));

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0; c.gridy = GridBagConstraints.RELATIVE;
		c.insets = new Insets(5,5,5,5);

		for (int i = 0; i < tf.length; i++) {
			c.gridx = 0; 
			Label l = new Label("Task " + i);
			add(l,c);
			c.gridx = 1;
			tf[i] = new JTextField(width);
			tf[i].setEditable(false);
			tf[i].setForeground(Color.blue);
			tf[i].setBackground(Color.white);

			add(tf[i], c);
		}

		txt = new JTextArea("",8,width);	
		txt.addKeyListener(kb);
		txt.setEditable(false);
		txt.setBackground(Color.white);
		pane = new JScrollPane(txt);
		pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		c.gridx = 0; c.gridwidth = 2;  c.fill = GridBagConstraints.HORIZONTAL;
		add(pane,c);

		pack();
		setVisible(true);
		txt.requestFocus();
	}

	public TaskDisplay(String name, final int n) throws InvocationTargetException, InterruptedException {

		super(name);

		kb = new KeyBuffer();

		// Initialization of Swing components should be done by the GUI thread
		EventQueue.invokeAndWait( new Runnable() {
			public void run() { initGUI(n); }
		});
	}

	// Assumed not to be called by GUI thread (but ok if it is)
	public void println(String s) {
		EventQueue.invokeLater( () -> {
			int last = txt.getDocument().getLength();
			txt.setCaretPosition(last);       // set caret at end
			if (last > 2*minhistory) txt.replaceRange("",0,last-minhistory);
			txt.append(s+"\n");
		});
	}

	// Wrapper for key buffer Get-operation (which is thread-safe)
	public char getKey() {
		return kb.Get();
	}

}




