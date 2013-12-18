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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import all.entities.Record;
import all.entities.User;
import all.net.IRecordsActivities;
import all.net.IUsersActivities;
import au.com.bytecode.opencsv.CSVWriter;

@SuppressWarnings("serial")
public class RecordsTableFrame extends JFrame implements ActionListener {
	// Синглтон
	public static final RecordsTableFrame frame = new RecordsTableFrame();

	// Переменные состояния
	private User user = null;

	// Переменные основного окна
	private JPanel tablePanel = null;
	private JPanel buttonsPanel = null;
	private JPanel mainPanel = null;

	private JTable recordsTable = null;
	private JScrollPane recordsListScrollPane = null;
	private JPopupMenu popupMenu = null;
	private JMenuItem viewRecordMI = null;
	private JMenuItem editRecordMI = null;
	private JMenuItem deleteRecordMI = null;

	private JButton addRecordButton = null;

	// Переменные меню бара
	private JMenuBar menuBar = null;
	private JMenu applicationMenu = null;
	private JMenuItem makeCSVMI = null;
	private JMenuItem profitValuationMI = null;
	private JMenuItem chooseModeMI = null;
	private JMenuItem reloginMI = null;
	private JMenuItem exitMI = null;
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
		DefaultTableModel dtm = getRecordsTableModel();
		recordsTable.setModel(dtm);
		dtm.fireTableDataChanged();

		TableRowSorter<? extends TableModel> rowSorter = (TableRowSorter<? extends TableModel>) recordsTable.getRowSorter();
		for (int i = 0; i <= 6; ++i) {
			if (i == 0 || i == 1 || i == 2 || i == 3) {
				rowSorter.setComparator(i, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return Integer.parseInt(o1) - Integer.parseInt(o2);
					}
				});
			}
			if (i == 5 || i == 6) {
				rowSorter.setComparator(i, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return (int) Double.parseDouble(o1) - (int) Double.parseDouble(o2);
					}
				});
			}
		}

		if (user.IsAdmin()) {
			editRecordMI.setVisible(true);
			deleteRecordMI.setVisible(true);
		} else {
			editRecordMI.setVisible(false);
			deleteRecordMI.setVisible(false);
		}
	}

	private RecordsTableFrame() {
		// Настройка фрейма
		setTitle("Оценка роста прибыли - Страница просмотра отчётов");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);
		buildMenu();

		recordsTable = new JTable();
		recordsTable.setAutoCreateRowSorter(true);
		recordsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		popupMenu = new JPopupMenu();
		viewRecordMI = new JMenuItem("Просмотреть отчёт");
		editRecordMI = new JMenuItem("Редактировать данные об отчёте");
		deleteRecordMI = new JMenuItem("Удалить отчёт");
		viewRecordMI.addActionListener(this);
		editRecordMI.addActionListener(this);
		deleteRecordMI.addActionListener(this);
		popupMenu.add(viewRecordMI);
		popupMenu.add(editRecordMI);
		popupMenu.add(deleteRecordMI);

		recordsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					int row = recordsTable.rowAtPoint(e.getPoint());
					if (!recordsTable.isRowSelected(row))
						recordsTable.changeSelection(row, 0, false, false);
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		recordsListScrollPane = new JScrollPane(recordsTable);
		recordsListScrollPane.setPreferredSize(new Dimension(1000, 1000));

		// Наполнение панели со списком
		tablePanel = new JPanel();
		tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.X_AXIS));
		tablePanel.add(Box.createHorizontalStrut(10));
		tablePanel.add(recordsListScrollPane);
		tablePanel.add(Box.createHorizontalStrut(10));

		// Конфигурирование пользовательских кнопок
		addRecordButton = new JButton("Добавить отчёт");
		addRecordButton.addActionListener(this);

		// Наполенеие пользовательской панели
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		buttonsPanel.add(Box.createGlue());
		buttonsPanel.add(addRecordButton);
		buttonsPanel.add(Box.createGlue());

		// Наполнение основной панели
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(recordsListScrollPane);
		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(buttonsPanel);
		mainPanel.add(Box.createVerticalStrut(10));

		add(mainPanel);
	}

	private void buildMenu() {
		menuBar = new JMenuBar();

		makeCSVMI = new JMenuItem("Экспортировать данные в .csv");
		profitValuationMI = new JMenuItem("Оценка роста прибыли по отчётам");
		chooseModeMI = new JMenuItem("Выбрать режим работы");
		reloginMI = new JMenuItem("Выйти из профиля");
		exitMI = new JMenuItem("Выйти из приложения");
		makeCSVMI.addActionListener(this);
		profitValuationMI.addActionListener(this);
		chooseModeMI.addActionListener(this);
		reloginMI.addActionListener(this);
		exitMI.addActionListener(this);

		applicationMenu = new JMenu("Приложение");
		applicationMenu.add(makeCSVMI);
		applicationMenu.add(profitValuationMI);
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

		menuBar.add(applicationMenu);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);
	}

	private DefaultTableModel getRecordsTableModel() throws RemoteException, NotBoundException {
		DefaultTableModel tableModel = null;

		try {
			Registry registry = LocateRegistry.getRegistry(PreferencesFrame.ADDRESS, PreferencesFrame.PORT);
			IRecordsActivities remoteObject = (IRecordsActivities) registry.lookup("RecordsActivities");
			tableModel = remoteObject.getRecordsTableModel(false);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
		}

		return tableModel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addRecordButton) {
			hideFrame();
			CreateRecordFrame.frame.showFrame(user);
		} else if (e.getSource() == editRecordMI || e.getSource() == viewRecordMI) {
			int recordId = Integer.parseInt(String.valueOf(recordsTable.getValueAt(recordsTable.getSelectedRow(), 0)));
			int year = Integer.parseInt(String.valueOf(recordsTable.getValueAt(recordsTable.getSelectedRow(), 1)));
			int quarter = Integer.parseInt(String.valueOf(recordsTable.getValueAt(recordsTable.getSelectedRow(), 2)));
			int vatRate = Integer.parseInt(String.valueOf(recordsTable.getValueAt(recordsTable.getSelectedRow(), 3)));
			int userId = getUserIdByLogin(String.valueOf(recordsTable.getValueAt(recordsTable.getSelectedRow(), 4)));

			Record o_record = new Record(recordId, year, quarter, vatRate, userId);

			if (e.getSource() == editRecordMI) {
				hideFrame();
				EditRecordFrame.frame.showFrame(user, o_record);
			} else {
				hideFrame();
				ProductsTableFrame.frame.showFrame(user, o_record);
			}
		} else if (e.getSource() == deleteRecordMI) {
			int recordId = Integer.parseInt(String.valueOf(recordsTable.getValueAt(recordsTable.getSelectedRow(), 0)));
			deleteRecord(recordId);
			showFrame(user);
		} else if (e.getSource() == makeCSVMI) {
			makeCSVFile();
		} else if (e.getSource() == profitValuationMI) {
			hideFrame();
			ProfitValuationFrame.frame.showFrame(user);
		} else if (e.getSource() == chooseModeMI) {
			hideFrame();
			WorkModeChoiceFrame.frame.showFrame(user);
		} else if (e.getSource() == reloginMI) {
			hideFrame();
			AuthorizationFrame.frame.showFrame(null);
		} else if (e.getSource() == exitMI) {
			System.exit(0);
		} else if (e.getSource() == howToMI) {
			JOptionPane
					.showMessageDialog(
							howToMI,
							"Нажмите кнопку \"Добавить отчёт\", чтобы перейти в фрейм создания отчёта\n"
									+ "Нажатие правой кнопки мыши по отчётам в таблице позволит вам просмотреть список товаров в отчёте, редактировать отчёт или удалить его.",
							"Что делать?", JOptionPane.INFORMATION_MESSAGE);
		} else if (e.getSource() == whatIsItMI) {
			JOptionPane.showMessageDialog(howToMI, "Фрейм просмотра отчётов", "Что это?", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void makeCSVFile() {
		CSVWriter csvw = null;
		
		try {
			if (recordsTable.getRowCount() != 0) {
				Date d = new Date();
				SimpleDateFormat format1 = new SimpleDateFormat("ddMMyyyyhhmmss");
				String timestamp = format1.format(d);
				
				File f = new File("Records" + timestamp + ".csv");
				Writer wr = new OutputStreamWriter(new FileOutputStream(f), "cp1251");
				csvw = new CSVWriter(wr, ';');
	
				List<String[]> l = new Vector<String[]>();
				
				for (int i = 0; i < recordsTable.getRowCount(); ++i) {
					if (i == 0) {
						l.add(new String[] { 
								"ID записи", 
								"Год данных", 
								"Квартал года", 
								"Ставка налога на прибыль",
								"Редактировал пользователь", 
								"Прибыль", 
								"Чистая прибыль" 
								});
					}
					l.add(new String[] { 
							String.valueOf(recordsTable.getValueAt(i, 0)),
							String.valueOf(recordsTable.getValueAt(i, 1)), 
							String.valueOf(recordsTable.getValueAt(i, 2)),
							String.valueOf(recordsTable.getValueAt(i, 3)), 
							String.valueOf(recordsTable.getValueAt(i, 4)),
							String.valueOf(recordsTable.getValueAt(i, 5)), 
							String.valueOf(recordsTable.getValueAt(i, 6))
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

	private void deleteRecord(int recordId) {
		try {
			Registry registry = LocateRegistry.getRegistry(PreferencesFrame.ADDRESS, PreferencesFrame.PORT);
			IRecordsActivities remoteObject = (IRecordsActivities) registry.lookup("RecordsActivities");
			remoteObject.deleteRecord(recordId);
			JOptionPane.showMessageDialog(this, "Отчёт успешно удалён", "Удаление отчёт", JOptionPane.INFORMATION_MESSAGE);
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

	private int getUserIdByLogin(String login) {
		int id = 0;

		try {
			Registry registry = LocateRegistry.getRegistry(PreferencesFrame.ADDRESS, PreferencesFrame.PORT);
			IUsersActivities remoteObject = (IUsersActivities) registry.lookup("UsersActivities");
			id = remoteObject.getUserIdByLogin(login);
		} catch (NotBoundException | RemoteException e) {
			JOptionPane.showMessageDialog(this,
					"Возникла проблема с сетевым соединением. Проверьте правильность ввода адреса сервера и его порта.",
					"Network Exception", JOptionPane.ERROR_MESSAGE);
			PreferencesFrame.frame.showFrame();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
		}

		return id;
	}
}
