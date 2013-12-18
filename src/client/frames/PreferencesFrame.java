package client.frames;

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

import all.util.SpringUtilities;

@SuppressWarnings("serial")
public class PreferencesFrame extends JFrame implements ActionListener {
	public static PreferencesFrame frame = new PreferencesFrame();

	// private static String ADDRESS = null;
	public static String ADDRESS = "localhost";
	public static int PORT = 2099;

	private JLabel addressLabel = null; 
	private JTextField addressBox = null;
	private JLabel portLabel = null;
	private JSpinner portBox = null;
	
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
		setSize(420, 150);
		setLocationRelativeTo(null);

		addressLabel = new JLabel("Адрес сервера:");
		addressBox = new JTextField("localhost");
		portLabel = new JLabel("Порт сервера:");
		portBox = new JSpinner(new SpinnerNumberModel(2099, 0, 9999, 1));

		fieldsPanel = new JPanel(new SpringLayout());
		fieldsPanel.add(addressLabel);
		fieldsPanel.add(addressBox);
		fieldsPanel.add(portLabel);
		fieldsPanel.add(portBox);
		SpringUtilities.makeCompactGrid(fieldsPanel, 2, 2, 7, 7, 7, 7);
		
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
			String address = addressBox.getText();
			int port = Integer.parseInt(String.valueOf(portBox.getValue()));
			
			ADDRESS = address;
			PORT = port;
			
			hideFrame();
			AuthorizationFrame.frame.showFrame(null);
		}
	}
}
