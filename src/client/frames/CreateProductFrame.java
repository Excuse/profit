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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;

import all.entities.Product;
import all.entities.Record;
import all.entities.User;
import all.net.IRecordsActivities;
import all.util.SpringUtilities;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

@SuppressWarnings("serial")
public class CreateProductFrame extends JFrame implements ActionListener, WindowListener {
	public static CreateProductFrame frame = new CreateProductFrame();
	
	private User user = null;
	private Record record = null;

	private JLabel nameLabel = null;
	private JTextField nameField = null;
	private JLabel sellingPriceLabel = null;
	private JSpinner sellingPriceBox = null;
	private JLabel costPriceLabel = null;
	private JSpinner costPriceBox = null;
	private JLabel vatRateLabel = null;
	private JSpinner vatRateBox = null;
	private JLabel quantityLabel = null;
	private JSpinner quantityBox = null;

	private JButton createProductButton = null;

	private JPanel fieldsPanel = null;
	private JPanel buttonPanel = null;
	private JPanel mainPanel = null;

	public void showFrame(User user, Record record) {
		this.user = user;
		this.record = record;
		setVisible(true);
	}

	public void hideFrame() {
		setVisible(false);

		nameField.setText("");
		sellingPriceBox.setValue(0);
		costPriceBox.setValue(0);
		vatRateBox.setValue(12);
		quantityBox.setValue(1);
	}

	private CreateProductFrame() {
		setTitle("Добавить запись");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(420, 220);
		setLocationRelativeTo(null);
		addWindowListener(this);

		nameLabel = new JLabel("Наименование продукта:");
		nameField = new JTextField();
		sellingPriceLabel = new JLabel("Отпускная цена:");
		sellingPriceBox = new JSpinner(new SpinnerNumberModel(0, 0, 10000000, 50));
		costPriceLabel = new JLabel("Себестоимость:");
		costPriceBox = new JSpinner(new SpinnerNumberModel(0, 0, 10000000, 50));
		vatRateLabel = new JLabel("Ставка НДС:");
		vatRateBox = new JSpinner(new SpinnerNumberModel(12, 0, 100, 1));
		quantityLabel = new JLabel("Количество:");
		quantityBox = new JSpinner(new SpinnerNumberModel(1, 1, 1000000, 1));

		fieldsPanel = new JPanel(new SpringLayout());
		fieldsPanel.add(nameLabel);
		fieldsPanel.add(nameField);
		fieldsPanel.add(sellingPriceLabel);
		fieldsPanel.add(sellingPriceBox);
		fieldsPanel.add(costPriceLabel);
		fieldsPanel.add(costPriceBox);
		fieldsPanel.add(vatRateLabel);
		fieldsPanel.add(vatRateBox);
		fieldsPanel.add(quantityLabel);
		fieldsPanel.add(quantityBox);
		SpringUtilities.makeCompactGrid(fieldsPanel, 5, 2, 7, 7, 7, 7);

		createProductButton = new JButton("Добавить продукт");
		createProductButton.addActionListener(this);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 1));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPanel.add(createProductButton);

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(fieldsPanel);
		mainPanel.add(buttonPanel);

		add(mainPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == createProductButton) {
			try {
				String name = nameField.getText();
				int sellingPrice = Integer.parseInt(String.valueOf(sellingPriceBox.getValue()));
				int costPrice = Integer.parseInt(String.valueOf(costPriceBox.getValue()));
				int vatRate = Integer.parseInt(String.valueOf(vatRateBox.getValue()));
				int quantity = Integer.parseInt(String.valueOf(quantityBox.getValue()));
				int recordId = record.getId();
				
				Product o_product = new Product(name, sellingPrice, costPrice, vatRate, quantity, recordId);
				
				addProduct(o_product);
	
				hideFrame();
				ProductsTableFrame.frame.showFrame(user, record);
			} catch (NotBoundException | RemoteException e1) {
				JOptionPane.showMessageDialog(this,
						"Возникла проблема с сетевым соединением. Проверьте правильность ввода адреса сервера и его порта.",
						"Network Exception", JOptionPane.ERROR_MESSAGE);
				hideFrame();
				PreferencesFrame.frame.showFrame();
			} 
		}
	}

	private void addProduct(Product product) throws NotBoundException, RemoteException {
		try {
			try {
				Registry registry = LocateRegistry.getRegistry(PreferencesFrame.ADDRESS, PreferencesFrame.PORT);
				IRecordsActivities remoteObject = (IRecordsActivities) registry.lookup("RecordsActivities");
				remoteObject.addProduct(product);
				JOptionPane
						.showMessageDialog(this, "Продукт успешно добавлен", "Добавление продукта", JOptionPane.INFORMATION_MESSAGE);
			} catch (MySQLIntegrityConstraintViolationException e) {
				JOptionPane.showMessageDialog(this, "Продукт с таким именем уже существует в отчёте", "Дублирование полей",
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
			JOptionPane.showMessageDialog(this, "Продукт не был создан", "Создание продукта", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		hideFrame();
		ProductsTableFrame.frame.showFrame(user, record);
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
