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

@SuppressWarnings("serial")
public class EditProductFrame extends JFrame implements ActionListener, WindowListener {
	public static EditProductFrame frame = new EditProductFrame();
	
	private User user = null;
	private Product previous = null;
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

	private JButton editProductButton = null;

	private JPanel fieldsPanel = null;
	private JPanel buttonPanel = null;
	private JPanel mainPanel = null;

	public void showFrame(User user, Product previous, Record record) {
		this.user = user;
		this.previous = previous;
		this.record = record;
		
		nameField.setText(previous.getName());
		sellingPriceBox.setValue(previous.getSellingPrice());
		costPriceBox.setValue(previous.getCostPrice());
		vatRateBox.setValue(previous.getVatRate());
		quantityBox.setValue(previous.getQuantity());
		
		setVisible(true);
	}

	public void hideFrame() {
		setVisible(false);

		nameField.setText("");
		sellingPriceBox.setValue(0);
		costPriceBox.setValue(0);
		vatRateBox.setValue(12);
		quantityBox.setValue(0);
	}

	private EditProductFrame() {
		setTitle("Редактирование продукта");
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
		quantityBox = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));

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

		editProductButton = new JButton("Сохранить изменения");
		editProductButton.addActionListener(this);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 1));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPanel.add(editProductButton);

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(fieldsPanel);
		mainPanel.add(buttonPanel);

		add(mainPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == editProductButton) {
			try {
				String name = nameField.getText();
				int sellingPrice = Integer.parseInt(String.valueOf(sellingPriceBox.getValue()));
				int costPrice = Integer.parseInt(String.valueOf(costPriceBox.getValue()));
				int vatRate = Integer.parseInt(String.valueOf(vatRateBox.getValue()));
				int quantity = Integer.parseInt(String.valueOf(quantityBox.getValue()));
				int recordId = record.getId();
				
				Product current = new Product(name, sellingPrice, costPrice, vatRate, quantity, recordId);
				
				editProduct(previous, current);
	
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

	private void editProduct(Product prev, Product curr) throws NotBoundException, RemoteException {
		try {
			try {
				Registry registry = LocateRegistry.getRegistry(PreferencesFrame.ADDRESS, PreferencesFrame.PORT);
				IRecordsActivities remoteObject = (IRecordsActivities) registry.lookup("RecordsActivities");
				remoteObject.editProduct(prev, curr);
				JOptionPane
						.showMessageDialog(this, "Продукт успешно изменён", "Редактирование продукта", JOptionPane.INFORMATION_MESSAGE);
			} catch (NotBoundException | RemoteException e) {
				throw e;
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
				throw e;
			}
		} catch (NotBoundException | RemoteException e) {
			throw e;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Продукт не был изменён", "Редактирование продукта", JOptionPane.ERROR_MESSAGE);
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
