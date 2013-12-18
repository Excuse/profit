package client.frames;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import all.entities.User;
import all.net.IUsersActivities;
import au.com.bytecode.opencsv.CSVWriter;

@SuppressWarnings("serial")
public class UsersTableFrame extends JFrame implements ActionListener {
	// Синглтон
	public static final UsersTableFrame frame = new UsersTableFrame();

	// Переменные состояния
	private User user = null;

	// Переменные основного окна
	private JPanel searchPanel = null;
	private JPanel usersPanel = null;
	private JPanel buttonsPanel = null;
	private JPanel mainPanel = null;
	
	private JLabel searchLabel = null;
	private JTextField searchBox = null;

	private JTable usersTable = null;
	private JScrollPane usersListScrollPane = null;
	private JPopupMenu popupMenu = null;
	private JMenuItem editUserMI = null;
	private JMenuItem deleteUserMI = null;

	private JButton addUserButton = null;

	// Переменные менюбара
	private JMenuBar menuBar = null;
	private JMenu applicationMenu = null;
	private JMenuItem makeCSVMI = null;
	private JMenuItem chooseModeMI = null;
	private JMenuItem reloginMI = null;
	private JMenuItem exitMI = null;
	private JMenu dbMenu = null;
	private JMenuItem dropRankMI = null;
	private JMenuItem dropUserMI = null;
	private JMenuItem dropAllMI = null;
	private JMenu helpMenu = null;
	private JMenuItem howToMI = null;
	private JMenuItem whatIsItMI = null;

	public void showFrame(User user) {
		try {
			this.user = user;
			reloadDynamicContent();
			setVisible(true);
			setExtendedState(MAXIMIZED_BOTH);
		} catch (NotBoundException | RemoteException e) {
			hideFrame();
			JOptionPane.showMessageDialog(this,
					"Возникла проблема с сетевым соединением. Проверьте правильность ввода адреса сервера и его порта.",
					"Network Exception", JOptionPane.ERROR_MESSAGE);
			PreferencesFrame.frame.showFrame();
		}
	}

	public void hideFrame() {
		setVisible(false);
	}

	private void reloadDynamicContent() throws RemoteException, NotBoundException {
		DefaultTableModel dtm = getUsersTableModel();
		usersTable.setModel(dtm);
		dtm.fireTableDataChanged();
		
		TableRowSorter<? extends TableModel> rowSorter = (TableRowSorter<? extends TableModel>)usersTable.getRowSorter();
		rowSorter.setComparator(0, new Comparator<String>() {
		        @Override
		        public int compare(String o1, String o2)
		        {
		            return Integer.parseInt(o1) - Integer.parseInt(o2);
		        }
		    }
		);
	}

	private UsersTableFrame() {
		// Настройка фрейма
		setTitle("Оценка роста прибыли - Страница администрирования");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);
		buildMenu();
		
		searchLabel = new JLabel("Поиск по названию продукта");
		searchBox = new JTextField();
		searchBox.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				showFrame(user);
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				showFrame(user);
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
		
			}
		});;
		
		searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
		searchPanel.add(Box.createHorizontalStrut(10));
		searchPanel.add(searchLabel);
		searchPanel.add(Box.createHorizontalStrut(10));
		searchPanel.add(searchBox);
		searchPanel.add(Box.createHorizontalStrut(10));

		usersTable = new JTable() {
			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			}
		};
		usersTable.setAutoCreateRowSorter(true);
		usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		popupMenu = new JPopupMenu();
		editUserMI = new JMenuItem("Редактировать пользователя");
		deleteUserMI = new JMenuItem("Удалить пользователя");
		editUserMI.addActionListener(this);
		deleteUserMI.addActionListener(this);
		popupMenu.add(editUserMI);
		popupMenu.add(deleteUserMI);
		
		usersTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) { 
				if (e.isPopupTrigger())
                {
                    int row = usersTable.rowAtPoint( e.getPoint() );
                    if (!usersTable.isRowSelected(row))
                    	usersTable.changeSelection(row, 0, false, false);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
			}
		});
		
		usersListScrollPane = new JScrollPane(usersTable);
		usersListScrollPane.setPreferredSize(new Dimension(1000, 1000));

		// Наполнение панели с таблицей
		usersPanel = new JPanel();
		usersPanel.setLayout(new BoxLayout(usersPanel, BoxLayout.X_AXIS));
		usersPanel.add(Box.createHorizontalStrut(10));
		usersPanel.add(usersListScrollPane);
		usersPanel.add(Box.createHorizontalStrut(10));

		// Конфигурирование кнопок
		addUserButton = new JButton("Добавить пользователя");
		addUserButton.addActionListener(this);

		// Наполнение панели с кнопками
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		buttonsPanel.add(Box.createGlue());
		buttonsPanel.add(addUserButton);
		buttonsPanel.add(Box.createGlue());

		// Наполнение основной панели
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(searchPanel);
		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(usersListScrollPane);
		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(buttonsPanel);
		mainPanel.add(Box.createVerticalStrut(10));

		add(mainPanel);
	}

	private void buildMenu() {
		menuBar = new JMenuBar();

		makeCSVMI = new JMenuItem("Экспортировать данные в .csv");
		chooseModeMI = new JMenuItem("Выбрать режим работы");
		reloginMI = new JMenuItem("Выйти из профиля");
		exitMI = new JMenuItem("Выйти из приложения");
		makeCSVMI.addActionListener(this);
		chooseModeMI.addActionListener(this);
		reloginMI.addActionListener(this);
		exitMI.addActionListener(this);

		applicationMenu = new JMenu("Приложение");
		applicationMenu.add(makeCSVMI);
		applicationMenu.add(chooseModeMI);
		applicationMenu.add(reloginMI);
		applicationMenu.add(exitMI);

		howToMI = new JMenuItem("Что нужно делать?");
		whatIsItMI = new JMenuItem("Что это?");
		howToMI.addActionListener(this);
		whatIsItMI.addActionListener(this);

		helpMenu = new JMenu("Помощь");
		helpMenu.add(howToMI);
		helpMenu.add(whatIsItMI);

		dropRankMI = new JMenuItem("Очистить ранги");
		dropUserMI = new JMenuItem("Очистить список пользователей");
		dropAllMI = new JMenuItem("Очистить всю БД");
		dropRankMI.addActionListener(this);
		dropUserMI.addActionListener(this);
		dropAllMI.addActionListener(this);

		dbMenu = new JMenu("Работа с БД");
		dbMenu.add(dropUserMI);
		dbMenu.add(dropRankMI);
		dbMenu.add(dropAllMI);

		menuBar.add(applicationMenu);
		menuBar.add(helpMenu);
		menuBar.add(dbMenu);

		setJMenuBar(menuBar);
	}

	private DefaultTableModel getUsersTableModel() throws RemoteException, NotBoundException {
		DefaultTableModel tableModel = null;

		try {
			Registry registry = LocateRegistry.getRegistry(PreferencesFrame.ADDRESS, PreferencesFrame.PORT);
			IUsersActivities remoteObject = (IUsersActivities) registry.lookup("UsersActivities");
			tableModel = remoteObject.getUsersTableModel(searchBox.getText());
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
		}

		return tableModel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addUserButton) {
			hideFrame();
			CreateUserFrame.frame.showFrame(user);
		} else if (e.getSource() == editUserMI) {
			String username = String.valueOf(usersTable.getValueAt(usersTable.getSelectedRow(), 1));
			char[] password = String.valueOf(usersTable.getValueAt(usersTable.getSelectedRow(), 2)).toCharArray();
			boolean isAdmin = usersTable.getValueAt(usersTable.getSelectedRow(), 3) == "Администратор" ? true : false;
			
			User o_user = new User(username, password, isAdmin);

			hideFrame();
			EditUserFrame.frame.showFrame(user, o_user);
		} else if (e.getSource() == deleteUserMI) {
			int userId = Integer.parseInt(String.valueOf(usersTable.getValueAt(usersTable.getSelectedRow(), 0)));
			deleteUser(userId);
			showFrame(user);
		} else if (e.getSource() == makeCSVMI) {
			makeCSVFile();
		} else if (e.getSource() == chooseModeMI) {
			hideFrame();
			WorkModeChoiceFrame.frame.showFrame(user);
		} else if (e.getSource() == reloginMI) {
			hideFrame();
			AuthorizationFrame.frame.showFrame(null);
		} else if (e.getSource() == exitMI) {
			System.exit(0);
		} else if (e.getSource() == howToMI) {
			JOptionPane.showMessageDialog(howToMI, "Нажмите кнопку \"Добавить пользователя\", чтобы перейти в окно создания пользователей\n"
					+ "Нажатие правой кнопки мыши по пользователю в таблице позволит вам редактировать его или удалить (для администраторов)", "Что делать?", JOptionPane.INFORMATION_MESSAGE);
		} else if (e.getSource() == whatIsItMI) {
			JOptionPane.showMessageDialog(howToMI, "Окно работы с пользователями", "Что это?", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private void makeCSVFile() {
		CSVWriter csvw = null;
		
		try {
			if (usersTable.getRowCount() != 0) {
				Date d = new Date();
				SimpleDateFormat format1 = new SimpleDateFormat("ddMMyyyyhhmmss");
				String timestamp = format1.format(d);
				
				File f = new File("Users" + timestamp + ".csv");
				Writer wr = new OutputStreamWriter(new FileOutputStream(f), "cp1251");
				csvw = new CSVWriter(wr, ';');
	
				List<String[]> l = new Vector<String[]>();
				
				for (int i = 0; i < usersTable.getRowCount(); ++i) {
					if (i == 0) {
						l.add(new String[] { 
								"ID пользователя", 
								"Имя пользователя", 
								"Пароль пользователя", 
								"Статус пользователя" 
								});
					}
					l.add(new String[] { 
							String.valueOf(usersTable.getValueAt(i, 0)),
							String.valueOf(usersTable.getValueAt(i, 1)), 
							String.valueOf(usersTable.getValueAt(i, 2)),
							String.valueOf(usersTable.getValueAt(i, 3))
							});
				}
				csvw.writeAll(l);
				JOptionPane.showMessageDialog(this,
						"Создание файла прошло успешно! Название файла: " + "Record" + timestamp + ".csv",
						"Успех создания файла", JOptionPane.INFORMATION_MESSAGE);
			} else {
				throw new Exception();
			}
		} catch(Exception e) {
			JOptionPane.showMessageDialog(this,
					"Возникла проблема с созданием файла. Файл создан не был.",
					"Ошибка при создании CSV файла", JOptionPane.ERROR_MESSAGE);
		} finally {
			try {
				csvw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void deleteUser(int userId) {
		try {
			Registry registry = LocateRegistry.getRegistry(PreferencesFrame.ADDRESS, PreferencesFrame.PORT);
			IUsersActivities remoteObject = (IUsersActivities) registry.lookup("UsersActivities");
			remoteObject.deleteUser(userId);
			JOptionPane.showMessageDialog(this, "Пользователь успешно удалён", "Удаление пользователя",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (NotBoundException | RemoteException e) {
			JOptionPane.showMessageDialog(this,
					"Возникла проблема с сетевым соединением. Проверьте правильность ввода адреса сервера и его порта.",
					"Network Exception", JOptionPane.ERROR_MESSAGE);
			hideFrame();
			PreferencesFrame.frame.showFrame();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
		}
	}
}
