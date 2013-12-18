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
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;

import all.entities.Record;
import all.entities.User;
import all.net.IRecordsActivities;
import all.util.SpringUtilities;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

@SuppressWarnings("serial")
public class CreateRecordFrame extends JFrame implements ActionListener, WindowListener {
	public static CreateRecordFrame frame = new CreateRecordFrame();
	private User user = null;

	private JLabel yearLabel = null;
	private SpinnerNumberModel yearSpinnerModel = null;
	private JSpinner yearBox = null;
	private JLabel quarterLabel = null;
	private JComboBox<Integer> quarterBox = null;
	private JLabel profitTaxLabel = null;
	private SpinnerNumberModel profitTaxSpinnerModel = null;
	private JSpinner profitTaxBox = null;
	private JButton createRecordButton = null;

	private JPanel fieldsPanel = null;
	private JPanel buttonPanel = null;
	private JPanel mainPanel = null;

	public void showFrame(User user) {
		this.user = user;
		setVisible(true);
	}

	public void hideFrame() {
		setVisible(false);
		yearBox.setValue(2000);
		quarterBox.setSelectedItem(1);
		profitTaxBox.setValue(12);
	}

	private CreateRecordFrame() {
		setTitle("Добавить запись");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(420, 170);
		setLocationRelativeTo(null);
		addWindowListener(this);

		yearLabel = new JLabel("Год отчёта: ");
		yearSpinnerModel = new SpinnerNumberModel(2000, 1990, 2030, 1);
		yearBox = new JSpinner(yearSpinnerModel);
		quarterLabel = new JLabel("Квартал отчёта: ");
		Integer[] quarters = { 1, 2, 3, 4 };
		quarterBox = new JComboBox<Integer>(quarters);
		profitTaxLabel = new JLabel("Ставка налога на прибыль: ");
		profitTaxSpinnerModel = new SpinnerNumberModel(12, 0, 100, 1);
		profitTaxBox = new JSpinner(profitTaxSpinnerModel);

		createRecordButton = new JButton("Добавить отчёт");
		createRecordButton.addActionListener(this);

		fieldsPanel = new JPanel(new SpringLayout());
		fieldsPanel.add(yearLabel);
		fieldsPanel.add(yearBox);
		fieldsPanel.add(quarterLabel);
		fieldsPanel.add(quarterBox);
		fieldsPanel.add(profitTaxLabel);
		fieldsPanel.add(profitTaxBox);
		SpringUtilities.makeCompactGrid(fieldsPanel, 3, 2, 7, 7, 7, 7);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 1));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPanel.add(createRecordButton);

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(fieldsPanel);
		mainPanel.add(buttonPanel);

		add(mainPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == createRecordButton) {
			try {
				int year = Integer.parseInt(String.valueOf(yearBox.getValue()));
				int quarter = Integer.parseInt(String.valueOf(quarterBox.getSelectedItem()));
				int profitTax = Integer.parseInt(String.valueOf(profitTaxBox.getValue()));
				int userId = user.getId();
	
				Record o_record = new Record(year, quarter, profitTax, userId);
	
				addRecord(o_record);
	
				hideFrame();
				RecordsTableFrame.frame.showFrame(user);
			} catch (NotBoundException | RemoteException e1) {
				JOptionPane.showMessageDialog(this,
						"Возникла проблема с сетевым соединением. Проверьте правильность ввода адреса сервера и его порта.",
						"Network Exception", JOptionPane.ERROR_MESSAGE);
				hideFrame();
				PreferencesFrame.frame.showFrame();
			}
		}
	}

	private void addRecord(Record record) throws NotBoundException, RemoteException {
		try {
			try {
				Registry registry = LocateRegistry.getRegistry(PreferencesFrame.ADDRESS, PreferencesFrame.PORT);
				IRecordsActivities remoteObject = (IRecordsActivities) registry.lookup("RecordsActivities");
				remoteObject.addRecord(record);
				JOptionPane.showMessageDialog(this, "Отчёт успешно добавлен", "Добавление отчёта", JOptionPane.INFORMATION_MESSAGE);
			} catch (MySQLIntegrityConstraintViolationException e) {
				JOptionPane.showMessageDialog(this, "Такая комбинация года и квартала уже существует", "Дублирование полей",
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
			JOptionPane.showMessageDialog(this, "Отчёт не был создан", "Создание отчёта", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		hideFrame();
		RecordsTableFrame.frame.showFrame(user);
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
