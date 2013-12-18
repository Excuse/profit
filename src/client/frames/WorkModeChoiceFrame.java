package client.frames;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import all.entities.User;

@SuppressWarnings("serial")
public class WorkModeChoiceFrame extends JFrame implements ActionListener {
	// ��������
	public static final WorkModeChoiceFrame frame = new WorkModeChoiceFrame();
	
	// ���������� ���������
	private User user = null;

	// ���������� ��������� ����
	private JPanel mainPanel = null;
	private JPanel choicePanel = null;
	private JButton userModeButton = null;
	private JButton administratorModeButton = null;

	// ���������� ���� ����
	private JMenuBar menuBar = null;
	private JMenu applicationMenu = null;
	private JMenuItem reloginMI = null;
	private JMenuItem exitMI = null;
	private JMenu helpMenu = null;
	private JMenuItem howToMI = null;
	private JMenuItem whatIsItMI = null;

	// ���������� ���������
	public void showFrame(User user) {
		this.user = user;

		if (user.IsAdmin()) {
			setVisible(true);
		} else {
			RecordsTableFrame.frame.showFrame(user);
		}
	}
	
	public void hideFrame() {
		setVisible(false);
	}

	private WorkModeChoiceFrame() {
		// ��������� ������
		setTitle("������ ����� ������� - ����� ������ ������");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(420, 150);
		setLocationRelativeTo(null);
		buildMenu();

		// ���������� ������
		userModeButton = new JButton("������� �����");
		administratorModeButton = new JButton("����� �����������������");
		userModeButton.setSize(40, 40);
		userModeButton.addActionListener(this);
		administratorModeButton.setSize(40, 40);
		administratorModeButton.addActionListener(this);

		// ��������� ������ ������
		choicePanel = new JPanel(new GridLayout(1, 2));
		choicePanel.add(userModeButton);
		choicePanel.add(administratorModeButton);

		// ��������� ������� ������ � ������
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(choicePanel);

		add(mainPanel);
	}

	private void buildMenu() {
		menuBar = new JMenuBar();

		applicationMenu = new JMenu("����������");
		reloginMI = new JMenuItem("����� �� �������");
		exitMI = new JMenuItem("����� �� ����������");
		reloginMI.addActionListener(this);
		exitMI.addActionListener(this);
		applicationMenu.add(reloginMI);
		applicationMenu.add(exitMI);

		helpMenu = new JMenu("������");
		howToMI = new JMenuItem("��� ����� ������?");
		whatIsItMI = new JMenuItem("��� ���?");
		howToMI.addActionListener(this);
		whatIsItMI.addActionListener(this);
		helpMenu.add(howToMI);
		helpMenu.add(whatIsItMI);

		menuBar.add(applicationMenu);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == userModeButton) {
			hideFrame();
			RecordsTableFrame.frame.showFrame(user);
		} else if (e.getSource() == administratorModeButton) {
			hideFrame();
			UsersTableFrame.frame.showFrame(user);
		} else if (e.getSource() == reloginMI) {
			hideFrame();
			AuthorizationFrame.frame.showFrame(null);
		} else if (e.getSource() == exitMI) {
			System.exit(0);
		} else if (e.getSource() == howToMI) {
			JOptionPane.showMessageDialog(howToMI, "�������� ������ ����� ����:\n"
					+ "1. ������� ������������� ���������\n" + "2. ���������� ��������������",
					"��� ������?", JOptionPane.INFORMATION_MESSAGE);
		} else if (e.getSource() == whatIsItMI) {
			JOptionPane.showMessageDialog(howToMI, "�������� ������", "��� ���?",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
