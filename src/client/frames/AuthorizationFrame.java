package client.frames;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import all.entities.User;
import all.exceptions.AuthorizationFailException;
import all.exceptions.DataLengthException;
import all.net.IUsersActivities;
import all.util.SpringUtilities;

@SuppressWarnings("serial")
public class AuthorizationFrame extends JFrame implements ActionListener, KeyListener {
	// ��������
	public static final AuthorizationFrame frame = new AuthorizationFrame();

	// ���������� �������� ������������
	private User user = null;

	// ���������� ��������� ����
	private JPanel mainPanel = null;
	private JPanel fieldsPanel = null;
	private JPanel buttonsPanel = null;
	private JTextField loginBox = null;
	private JPasswordField passwordBox = null;
	private JLabel loginLabel = null;
	private JLabel passwordLabel = null;
	private JButton okayButton = null;
	private JButton clearButton = null;

	// ���������� ��������
	private JMenuBar menuBar = null;
	private JMenu applicationMenu = null;
	private JMenuItem preferencesMI = null;
	private JMenuItem exitMI = null;
	private JMenu helpMenu = null;
	private JMenuItem howToMI = null;
	private JMenuItem whatIsItMI = null;

	public void showFrame(User user) {
		this.user = user;
		setVisible(true);
	}

	private void hideFrame() {
		setVisible(false);
		clearButton.doClick();
	}

	private AuthorizationFrame() {
		int i = 123;
		// ��������� ������
		setTitle("������ ����� ������� - ��������� ��������");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(420, 170);
		setLocationRelativeTo(null);
		buildMenu();

		// ������������� ����� � ������� ������ � ������
		loginBox = new JTextField(10);
		passwordBox = new JPasswordField(10);
		Font fieldsFont = new Font("SansSerif", Font.PLAIN, 16);
		loginBox.setFont(fieldsFont);
		loginLabel = new JLabel("�����: ", JLabel.TRAILING);
		passwordLabel = new JLabel("������: ", JLabel.TRAILING);
		loginBox.addKeyListener(this);
		passwordBox.addKeyListener(this);

		// ���������� ������ ������ � ������
		fieldsPanel = new JPanel(new SpringLayout());
		fieldsPanel.add(loginLabel);
		fieldsPanel.add(loginBox);
		fieldsPanel.add(passwordLabel);
		fieldsPanel.add(passwordBox);
		SpringUtilities.makeCompactGrid(fieldsPanel, 2, 2, 7, 7, 7, 7);

		// ������������� ������
		okayButton = new JButton(" ����� ");
		clearButton = new JButton("��������");
		okayButton.addActionListener(this);
		clearButton.addActionListener(this);

		// ���������� ������ ������
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(1, 2));
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonsPanel.add(okayButton);
		buttonsPanel.add(clearButton);

		// ���������� ������� ������
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(fieldsPanel);
		mainPanel.add(buttonsPanel);

		add(mainPanel);
	}

	// ��������� ����
	private void buildMenu() {
		menuBar = new JMenuBar();

		preferencesMI = new JMenuItem("��������� �����������");
		preferencesMI.addActionListener(this);
		exitMI = new JMenuItem("����� �� ���������");
		exitMI.addActionListener(this);

		applicationMenu = new JMenu("����������");
		applicationMenu.add(preferencesMI);
		applicationMenu.add(exitMI);

		howToMI = new JMenuItem("��� ������?");
		whatIsItMI = new JMenuItem("��� ���?");
		howToMI.addActionListener(this);
		whatIsItMI.addActionListener(this);

		helpMenu = new JMenu("������");
		helpMenu.add(howToMI);
		helpMenu.add(whatIsItMI);

		menuBar.add(applicationMenu);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);
	}

	private void processAuthorization(String username, char[] password) {
		try {
			try {
				Registry registry = LocateRegistry.getRegistry(PreferencesFrame.ADDRESS, PreferencesFrame.PORT);
				IUsersActivities remoteObject = (IUsersActivities) registry.lookup("UsersActivities");
				user = remoteObject.processAuthorization(username, password);
			} catch (NotBoundException | RemoteException e) {
				JOptionPane.showMessageDialog(this,
						"�������� �������� � ������� �����������. ��������� ������������ ����� ������ ������� � ��� �����.",
						"Network Exception", JOptionPane.ERROR_MESSAGE);
				hideFrame();
				PreferencesFrame.frame.showFrame();
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
				throw e;
			} catch (DataLengthException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Data Length Exception", JOptionPane.ERROR_MESSAGE);
				throw e;
			} catch (AuthorizationFailException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Authorization Exception", JOptionPane.ERROR_MESSAGE);
				throw e;
			}
		} catch (Exception e) {
			user = null;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getSource() == loginBox || e.getSource() == passwordBox) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				okayButton.doClick();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okayButton) {
			processAuthorization(loginBox.getText(), passwordBox.getPassword());
			if (user != null) {
				hideFrame();
				WorkModeChoiceFrame.frame.showFrame(user);
			}
		} else if (e.getSource() == clearButton) {
			loginBox.setText("");
			passwordBox.setText("");
		} else if (e.getSource() == preferencesMI){
			hideFrame();
			PreferencesFrame.frame.showFrame();
		} else if (e.getSource() == exitMI) {
			System.exit(0);
		} else if (e.getSource() == howToMI) {
			JOptionPane.showMessageDialog(howToMI, "������� ����� � ������, �������� ������ \"�����\" ��� ������� ������� Enter",
					"��� ������?", JOptionPane.INFORMATION_MESSAGE);
		} else if (e.getSource() == whatIsItMI) {
			JOptionPane.showMessageDialog(howToMI, "�������� ������", "��� ���?", JOptionPane.INFORMATION_MESSAGE);
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
