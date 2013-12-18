package client.frames;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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

import all.entities.Product;
import all.entities.Record;
import all.entities.User;
import all.net.IRecordsActivities;
import au.com.bytecode.opencsv.CSVWriter;

@SuppressWarnings("serial")
public class ProductsTableFrame extends JFrame implements ActionListener, WindowListener {
	// Синглтон
	public static final ProductsTableFrame frame = new ProductsTableFrame();

	// Переменные состояния
	private User user = null;
	private Record record = null;

	// Переменные основного окна
	private JPanel searchPanel = null;
	private JPanel productsPanel = null;
	private JPanel recordPanel = null;
	private JPanel buttonsPanel = null;
	private JPanel mainPanel = null;
	
	private JLabel searchLabel = null;
	private JTextField searchBox = null;

	private JTable recordTable = null;
	private JScrollPane recordListScrollPane = null;
	
	private JTable productsTable = null;
	private JScrollPane productsListScrollPane = null;
	private JPopupMenu popupMenu = null;
	private JMenuItem editProductMI = null;
	private JMenuItem deleteProductMI = null;
	
	private JButton addProductButton = null;

	// Переменные меню бара
	private JMenuBar menuBar = null;
	private JMenuItem makeCSVMI = null;
	private JMenu applicationMenu = null;
	private JMenuItem chooseModeMI = null;
	private JMenuItem reloginMI = null;
	private JMenuItem exitMI = null;
	private JMenu helpMenu = null;
	private JMenuItem howToMI = null;
	private JMenuItem whatIsItMI = null;

	public void showFrame(User user, Record record) {
		try {
			this.user = user;
			this.record = record;
			reloadDynamicContent();
			setVisible(true);
			setExtendedState(MAXIMIZED_BOTH);
		} catch (NotBoundException | RemoteException e) {
			JOptionPane.showMessageDialog(this,
					"Возникла проблема с сетевым соединением. Проверьте правильность ввода адреса сервера и его порта.",
					"Network Exception", JOptionPane.ERROR_MESSAGE);
			hideFrame();
			PreferencesFrame.frame.showFrame();
		}
	}

	public void hideFrame() {
		searchBox.setText("");
		setVisible(false);
	}

	private void reloadDynamicContent() throws RemoteException, NotBoundException {
		DefaultTableModel dtmr = getRecordTableModel(record);
		DefaultTableModel dtmp = getProductsTableModel(record);
		
		recordTable.setModel(dtmr);
		dtmr.fireTableDataChanged();

		productsTable.setModel(dtmp);
		dtmp.fireTableDataChanged();
		
		TableRowSorter<? extends TableModel> rowSorter = (TableRowSorter<? extends TableModel>) productsTable.getRowSorter();
		
		for(int i = 0; i <= 6; ++i) {
			if(i == 0 || i == 2 || i == 3 || i == 4) {
				rowSorter.setComparator(i, new Comparator<String>() {
				        @Override
				        public int compare(String o1, String o2)
				        {
				            return Integer.parseInt(o1) - Integer.parseInt(o2);
				        }
				    }
				);
			}
			if(i == 5) {
				rowSorter.setComparator(i, new Comparator<String>() {
				        @Override
				        public int compare(String o1, String o2)
				        {
				        	return (int)Double.parseDouble(o1) - (int)Double.parseDouble(o2);
				        }
				    }
				);
			}
		}
		
		if (user.IsAdmin()) {
			editProductMI.setVisible(true);
			deleteProductMI.setVisible(true);
		} else {
			editProductMI.setVisible(false);
			deleteProductMI.setVisible(false);
		}
	}

	private ProductsTableFrame() {
		// Настройка фрейма
		setTitle("Оценка роста прибыли - Страница просмотра продуктов");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);
		buildMenu();
		addWindowListener(this);
		
		searchLabel = new JLabel("Поиск по названию продукта");
		searchBox = new JTextField();
		searchBox.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				showFrame(user, record);
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				showFrame(user, record);
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

		// Подготовка таблицы
		productsTable = new JTable() {
			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			}
		};
		productsTable.setAutoCreateRowSorter(true);
		productsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		popupMenu = new JPopupMenu();
		editProductMI = new JMenuItem("Редактировать продукт");
		deleteProductMI = new JMenuItem("Удалить продукт");
		editProductMI.addActionListener(this);
		deleteProductMI.addActionListener(this);
		popupMenu.add(editProductMI);
		popupMenu.add(deleteProductMI);
		
		productsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) { 
				if (e.isPopupTrigger())
                {
                    int row = productsTable.rowAtPoint( e.getPoint() );
                    if (!productsTable.isRowSelected(row))
                    	productsTable.changeSelection(row, 0, false, false);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
			}
		});
		
		productsListScrollPane = new JScrollPane(productsTable);
		productsListScrollPane.setPreferredSize(new Dimension(1000, 1000));

		// Наполнение панели со списком
		productsPanel = new JPanel();
		productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.X_AXIS));
		productsPanel.add(Box.createHorizontalStrut(10));
		productsPanel.add(productsListScrollPane);
		productsPanel.add(Box.createHorizontalStrut(10));
		
		recordTable = new JTable() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		recordTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		recordTable.setEnabled(false);
		recordListScrollPane = new JScrollPane(recordTable);
		recordListScrollPane.setPreferredSize(new Dimension(40, 50));
		
		recordPanel = new JPanel();
		recordPanel.setLayout(new BoxLayout(recordPanel, BoxLayout.X_AXIS));
		recordPanel.add(Box.createHorizontalStrut(10));
		recordPanel.add(recordListScrollPane);
		recordPanel.add(Box.createHorizontalStrut(10));
		
		addProductButton = new JButton("Добавить продукт");
		addProductButton.addActionListener(this);
		
		// Наполенеие пользовательской панели
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		buttonsPanel.add(Box.createGlue());
		buttonsPanel.add(addProductButton);
		buttonsPanel.add(Box.createGlue());

		// Наполнение основной панели
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(searchPanel);
		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(productsPanel);
		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(recordPanel);
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

		menuBar.add(applicationMenu);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);
	}

	private DefaultTableModel getProductsTableModel(Record record) throws RemoteException, NotBoundException {
		DefaultTableModel tableModel = null;

		try {
			Registry registry = LocateRegistry.getRegistry(PreferencesFrame.ADDRESS, PreferencesFrame.PORT);
			IRecordsActivities remoteObject = (IRecordsActivities) registry.lookup("RecordsActivities");
			if(searchBox.getText().equals("")) {
				tableModel = remoteObject.getProductsTableModel(record, null);
			}
			else {
				tableModel = remoteObject.getProductsTableModel(record, searchBox.getText());
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
		}

		return tableModel;
	}
	
	private DefaultTableModel getRecordTableModel(Record record) throws RemoteException, NotBoundException {
		DefaultTableModel tableModel = null;

		try {
			Registry registry = LocateRegistry.getRegistry(PreferencesFrame.ADDRESS, PreferencesFrame.PORT);
			IRecordsActivities remoteObject = (IRecordsActivities) registry.lookup("RecordsActivities");
			tableModel = remoteObject.getRecordTableModel(record);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
		}

		return tableModel;
	}

	private void deleteProduct(int productId) {
		try {
			Registry registry = LocateRegistry.getRegistry(PreferencesFrame.ADDRESS, PreferencesFrame.PORT);
			IRecordsActivities remoteObject = (IRecordsActivities) registry.lookup("RecordsActivities");
			remoteObject.deleteProduct(productId);
			JOptionPane.showMessageDialog(this, "Продукт успешно удалён", "Удаление продукта",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (NotBoundException | RemoteException e) {
			JOptionPane.showMessageDialog(this,
					"Возникла проблема с сетевым соединением. Проверьте правильность ввода адреса сервера и его порта.",
					"Network Exception", JOptionPane.ERROR_MESSAGE);
			hideFrame();
			PreferencesFrame.frame.showFrame();
		}catch (SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void makeCSVFile() {
		CSVWriter csvw = null;
		
		try {
			if (productsTable.getRowCount() != 0) {
				Date d = new Date();
				SimpleDateFormat format1 = new SimpleDateFormat("ddMMyyyyhhmmss");
				String timestamp = format1.format(d);
				
				File f = new File("Products(R" + record.getId() + ")" + timestamp + ".csv");
				Writer wr = new OutputStreamWriter(new FileOutputStream(f), "cp1251");
				csvw = new CSVWriter(wr, ';');
	
				List<String[]> l = new Vector<String[]>();
				
				for (int i = 0; i < productsTable.getRowCount(); ++i) {
					if (i == 0) {
						l.add(new String[] { 
								"ID продукта", 
								"Название продукта", 
								"Отпускная цена", 
								"Себестоимость",
								"Ставка НДС", 
								"Количество", 
								"Прибыль" 
								});
					}
					l.add(new String[] { 
							String.valueOf(productsTable.getValueAt(i, 0)),
							String.valueOf(productsTable.getValueAt(i, 1)), 
							String.valueOf(productsTable.getValueAt(i, 2)),
							String.valueOf(productsTable.getValueAt(i, 3)), 
							String.valueOf(productsTable.getValueAt(i, 4)),
							String.valueOf(productsTable.getValueAt(i, 5)), 
							String.valueOf(productsTable.getValueAt(i, 6))
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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addProductButton) {
			hideFrame();
			CreateProductFrame.frame.showFrame(user, record);
		} else if (e.getSource() == editProductMI) {
			int id = Integer.parseInt(String.valueOf(productsTable.getValueAt(productsTable.getSelectedRow(), 0)));
			String name = String.valueOf(productsTable.getValueAt(productsTable.getSelectedRow(), 1));
			int sellingPrice = Integer.parseInt(String.valueOf(productsTable.getValueAt(productsTable.getSelectedRow(), 2)));
			int costPrice = Integer.parseInt(String.valueOf(productsTable.getValueAt(productsTable.getSelectedRow(), 3)));
			int vatRate = Integer.parseInt(String.valueOf(productsTable.getValueAt(productsTable.getSelectedRow(), 4)));;
			int quantity = Integer.parseInt(String.valueOf(productsTable.getValueAt(productsTable.getSelectedRow(), 5)));;
			
			Product o_product = new Product(id, name, sellingPrice, costPrice, vatRate, quantity);
			
			hideFrame();
			EditProductFrame.frame.showFrame(user, o_product, record);
		} else if (e.getSource() == deleteProductMI) {
			int productId = Integer.parseInt(String.valueOf(productsTable.getValueAt(productsTable.getSelectedRow(), 0)));
			deleteProduct(productId);
			showFrame(user, record);
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
			JOptionPane.showMessageDialog(howToMI, "Нажмите кнопку \"Добавить продукт\", чтобы перейти в окно добавления продукта в отчёт\n"
					+ "Нажатие правой кнопки мыши по продуктам в таблице позволит вам редактировать отчёт или удалить его (для администраторов)", "Что делать?", JOptionPane.INFORMATION_MESSAGE);
		} else if (e.getSource() == whatIsItMI) {
			JOptionPane.showMessageDialog(howToMI, "Курсовая работа", "Что это?", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		if(isVisible() == true) {
			hideFrame();
			RecordsTableFrame.frame.showFrame(user);
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
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
}
