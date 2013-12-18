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
	// Синглтон
	public static final WorkModeChoiceFrame frame = new WorkModeChoiceFrame();
	
	// Переменные состояния
	private User user = null;

	// Переменные основного окна
	private JPanel mainPanel = null;
	private JPanel choicePanel = null;
	private JButton userModeButton = null;
	private JButton administratorModeButton = null;

	// Переменные меню бара
	private JMenuBar menuBar = null;
	private JMenu applicationMenu = null;
	private JMenuItem reloginMI = null;
	private JMenuItem exitMI = null;
	private JMenu helpMenu = null;
	private JMenuItem howToMI = null;
	private JMenuItem whatIsItMI = null;

	// Реализация синглтона
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
		// Настройка фрейма
		setTitle("Оценка роста прибыли - Выбор режима работы");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(420, 150);
		setLocationRelativeTo(null);
		buildMenu();

		// Подготовка кнопок
		userModeButton = new JButton("Обычный режим");
		administratorModeButton = new JButton("Режим администрирования");
		userModeButton.setSize(40, 40);
		userModeButton.addActionListener(this);
		administratorModeButton.setSize(40, 40);
		administratorModeButton.addActionListener(this);

		// Настройка панели кнопок
		choicePanel = new JPanel(new GridLayout(1, 2));
		choicePanel.add(userModeButton);
		choicePanel.add(administratorModeButton);

		// Настройка главной панели и фрейма
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(choicePanel);

		add(mainPanel);
	}

	private void buildMenu() {
		menuBar = new JMenuBar();

		applicationMenu = new JMenu("Приложение");
		reloginMI = new JMenuItem("Выйти из профиля");
		exitMI = new JMenuItem("Выйти из приложения");
		reloginMI.addActionListener(this);
		exitMI.addActionListener(this);
		applicationMenu.add(reloginMI);
		applicationMenu.add(exitMI);

		helpMenu = new JMenu("Помощь");
		howToMI = new JMenuItem("Что нужно делать?");
		whatIsItMI = new JMenuItem("Что это?");
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
			JOptionPane.showMessageDialog(howToMI, "Выберите нужный пункт меню:\n"
					+ "1. Обычное использование программы\n" + "2. Управление пользователями",
					"Что делать?", JOptionPane.INFORMATION_MESSAGE);
		} else if (e.getSource() == whatIsItMI) {
			JOptionPane.showMessageDialog(howToMI, "Курсовая работа", "Что это?",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
