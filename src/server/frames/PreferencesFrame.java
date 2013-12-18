package server.frames;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;

import server.main.Server;
import all.util.SpringUtilities;

@SuppressWarnings("serial")
public class PreferencesFrame extends JFrame implements ActionListener {
	public static PreferencesFrame frame = new PreferencesFrame();

	// private static String ADDRESS = null;
	public static int PORT = 2099;
	public static int DBPORT = 3306;
	public static String DBLOGIN = "root";
	public static String DBPASSWORD = "root";

	/*
	 * private JLabel addressLabel = null; private JTextField addressBox = null;
	 */
	private JLabel portLabel = null;
	private JSpinner portBox = null;
	private JLabel dbPortLabel = null;
	private JSpinner dbPortBox = null;
	private JLabel dbLoginLabel = null;
	private JTextField dbLoginBox = null;
	private JLabel dbPasswordLabel = null;
	private JTextField dbPasswordBox = null;
	
	private JButton confirmChangesButton = null;

	private JPanel fieldsPanel = null;
	private JPanel buttonsPanel = null;
	private JPanel mainPanel = null;

	public void showFrame() {
		setVisible(true);
	}

	public void hideFrame() {
		setVisible(false);
	}

	private PreferencesFrame() {
		setTitle("Настройки");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(420, 190);
		setLocationRelativeTo(null);

		portLabel = new JLabel("Порт сервера:");
		portBox = new JSpinner(new SpinnerNumberModel(2099, 0, 9999, 1));
		dbPortLabel = new JLabel("Порт базы данных:");
		dbPortBox = new JSpinner(new SpinnerNumberModel(3306, 0, 9999, 1));
		dbLoginLabel = new JLabel("Логин базы данных:");
		dbLoginBox = new JTextField("root");
		dbPasswordLabel = new JLabel("Пароль базы данных:");
		dbPasswordBox = new JTextField("root");

		fieldsPanel = new JPanel(new SpringLayout());
		fieldsPanel.add(portLabel);
		fieldsPanel.add(portBox);
		fieldsPanel.add(dbPortLabel);
		fieldsPanel.add(dbPortBox);
		fieldsPanel.add(dbLoginLabel);
		fieldsPanel.add(dbLoginBox);
		fieldsPanel.add(dbPasswordLabel);
		fieldsPanel.add(dbPasswordBox);
		SpringUtilities.makeCompactGrid(fieldsPanel, 4, 2, 7, 7, 7, 7);
		
		confirmChangesButton = new JButton("Сохранить изменения");
		confirmChangesButton.addActionListener(this);
		
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(1, 1));
		buttonsPanel.add(confirmChangesButton);

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(fieldsPanel);
		mainPanel.add(buttonsPanel);
		
		add(mainPanel);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == confirmChangesButton) {
			int port = Integer.parseInt(String.valueOf(portBox.getValue()));
			int dbPort = Integer.parseInt(String.valueOf(dbPortBox.getValue()));
			String dbLogin = String.valueOf(dbLoginBox.getText());
			String dbPassword = String.valueOf(dbPasswordBox.getText());
			
			PORT = port;
			DBPORT = dbPort;
			DBLOGIN = dbLogin.equals("") ? "root" : dbLogin;
			DBPASSWORD = dbPassword.equals("") ? "root" : dbPassword;
			
			hideFrame();
			Server.configureRegistry(PORT);
		}
	}
}
