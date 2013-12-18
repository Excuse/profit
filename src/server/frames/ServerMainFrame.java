package server.frames;

import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class ServerMainFrame extends JFrame {
	public static ServerMainFrame frame = new ServerMainFrame();
	
	private DefaultListModel<String> dlm = null;
	private JList<String> log = null;
	private JScrollPane jsp = null;
	
	public ServerMainFrame() {
		setTitle("Оценка роста прибыли - Сервер");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 300);
		setLocationRelativeTo(null);
		
		dlm = new DefaultListModel<>();
		log = new JList<String>();
		jsp = new JScrollPane(log);
		jsp.setPreferredSize(new Dimension(1000, 1000));
		
		add(jsp);
		
		setVisible(true);
	}
	
	public void addMessage(String message) {
		dlm.addElement(message);
		log.setModel(dlm);
	}
	
	public static void start() {
	}
}
