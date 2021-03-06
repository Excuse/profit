package client.frames;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import all.entities.User;
import all.exceptions.DataLengthException;
import all.net.IUsersActivities;
import all.util.SpringUtilities;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

@SuppressWarnings("serial")
public class EditUserFrame extends JFrame implements ActionListener, WindowListener {
	public static EditUserFrame frame = new EditUserFrame();
	private User user = null;
	private User previous = null;

	private JLabel usernameLabel = null;
	private JTextField usernameBox = null;
	private JLabel passwordLabel = null;
	private JTextField passwordBox = null;
	private JLabel rankLabel = null;
	private JComboBox<String> rankBox = null;
	private JButton editUserButton = null;

	private JPanel fieldsPanel = null;
	private JPanel buttonPanel = null;
	private JPanel mainPanel = null;

	public void showFrame(User user, User previous) {
		this.user = user;
		this.previous = previous;
		
		usernameBox.setText(previous.getUsername());
		passwordBox.setText(String.valueOf(previous.getPassword()));
		if (previous.IsAdmin()) {
			rankBox.setSelectedItem("�������������");
		} else {
			rankBox.setSelectedItem("������������");
		}
		
		setVisible(true);
	}

	public void hideFrame() {
		setVisible(false);
	}
	
	private EditUserFrame() {
		setTitle("�������������� ������������");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(420, 170);
		setLocationRelativeTo(null);
		addWindowListener(this);

		usernameLabel = new JLabel("��� ������������: ");
		usernameBox = new JTextField();
		passwordLabel = new JLabel("������ ������������: ");
		passwordBox = new JTextField();
		rankLabel = new JLabel("���� ������������: ");
		String[] ranks = { "�������������", "������������" };
		rankBox = new JComboBox<String>(ranks);

		editUserButton = new JButton("��������� ���������");
		editUserButton.addActionListener(this);

		fieldsPanel = new JPanel(new SpringLayout());
		fieldsPanel.add(usernameLabel);
		fieldsPanel.add(usernameBox);
		fieldsPanel.add(passwordLabel);
		fieldsPanel.add(passwordBox);
		fieldsPanel.add(rankLabel);
		fieldsPanel.add(rankBox);
		SpringUtilities.makeCompactGrid(fieldsPanel, 3, 2, 7, 7, 7, 7);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 1));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPanel.add(editUserButton);

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(fieldsPanel);
		mainPanel.add(buttonPanel);

		add(mainPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == editUserButton) {
			try {
				String username = usernameBox.getText();
				char[] password = passwordBox.getText().toCharArray();
				boolean isAdmin = rankBox.getSelectedItem().equals("�������������") ? true : false;
				
				User current = new User(username, password, isAdmin);
				
				editUser(previous, current);
				
				UsersTableFrame.frame.showFrame(user);
			} catch (NotBoundException | RemoteException e1) {
				JOptionPane.showMessageDialog(this,
						"�������� �������� � ������� �����������. ��������� ������������ ����� ������ ������� � ��� �����.",
						"Network Exception", JOptionPane.ERROR_MESSAGE);
				hideFrame();
				PreferencesFrame.frame.showFrame();
			} 
		}
	}

	private void editUser(User previous, User current) throws NotBoundException, RemoteException {
		try {
			try {
				Registry registry = LocateRegistry.getRegistry(PreferencesFrame.ADDRESS, PreferencesFrame.PORT);
				IUsersActivities remoteObject = (IUsersActivities) registry
						.lookup("UsersActivities");
				remoteObject.editUser(previous, current);
				JOptionPane.showMessageDialog(this, "������������ ������� ��������������",
						"�������������� ������������", JOptionPane.INFORMATION_MESSAGE);
			} catch (MySQLIntegrityConstraintViolationException e) {
				JOptionPane.showMessageDialog(this, "������������ � ����� ������� ��� ����������", "������������ �����", JOptionPane.ERROR_MESSAGE);
				throw e;
			} catch (DataLengthException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Data Length Exception",
						JOptionPane.ERROR_MESSAGE);
				throw e;
			} catch (NotBoundException | RemoteException e) {
				throw e;
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
				throw e;
			}
		} catch (NotBoundException | RemoteException e) {
			throw e;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "������������ �� ��� ��������������",
					"�������������� ������������", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		hideFrame();
		UsersTableFrame.frame.showFrame(user);
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}
}
