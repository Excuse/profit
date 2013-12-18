package client.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import all.entities.User;
import all.net.IRecordsActivities;

@SuppressWarnings("serial")
public class ProfitValuationFrame extends JFrame implements WindowListener, ActionListener {
	// ��������
	public static ProfitValuationFrame frame = new ProfitValuationFrame();
	
	// ���������� ���������
	private User user = null;

	// ���������� ��������� ����
	private JTabbedPane tabbedPane = null;
	
	private ChartPanel chartPanel = null;
	private JPanel tablePanel = null;
	private JPanel mainPanel = null;

	private XYDataset diagramDataset = null;
	private JFreeChart chart = null;

	private JTable recordsTable = null;
	private JScrollPane recordsScrollPane = null;

	// ���������� ���� ����
	private JMenuBar menuBar = null;
	private JMenu applicationMenu = null;
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
					"�������� �������� � ������� �����������. ��������� ������������ ����� ������ ������� � ��� �����.",
					"Network Exception", JOptionPane.ERROR_MESSAGE);
			hideFrame();
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
		
		diagramDataset = createDataset();
		chart = createChart(diagramDataset);
		chartPanel.setChart(chart);
	}

	private ProfitValuationFrame() {
		// ��������� ������
		setTitle("������ ����� ������� - �������� ������ ����� �������");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);
		buildMenu();
		addWindowListener(this);
		
		tabbedPane = new JTabbedPane();

		// ���������� �������
		recordsTable = new JTable();
		recordsTable.setEnabled(false);
		recordsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		recordsScrollPane = new JScrollPane(recordsTable);

		// ���������� ������ �� �������
		tablePanel = new JPanel(new BorderLayout());
		tablePanel.add(Box.createHorizontalStrut(20));
		tablePanel.add(recordsScrollPane, BorderLayout.CENTER);
		tablePanel.add(Box.createHorizontalStrut(20));
		
		// ��������� ���������
		diagramDataset = createDataset();
		chart = createChart(diagramDataset);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(500, 270));
		chartPanel.setMouseZoomable(true, false);
		
		tabbedPane.addTab("������", chartPanel);
		tabbedPane.addTab("�������", recordsScrollPane);
		
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(tabbedPane, BorderLayout.CENTER);
		
		add(mainPanel);
	}

	private void buildMenu() {
		menuBar = new JMenuBar();

		chooseModeMI = new JMenuItem("������� ����� ������");
		reloginMI = new JMenuItem("����� �� �������");
		exitMI = new JMenuItem("����� �� ����������");
		chooseModeMI.addActionListener(this);
		reloginMI.addActionListener(this);
		exitMI.addActionListener(this);

		applicationMenu = new JMenu("����������");
		applicationMenu.add(chooseModeMI);
		applicationMenu.add(reloginMI);
		applicationMenu.add(exitMI);

		howToMI = new JMenuItem("��� ����� ������?");
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

	private JFreeChart createChart(XYDataset dataset) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart("������ ����� �������", "���� �������", "�������� �����������, ���.",
				dataset, true, true, false);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		XYItemRenderer r = plot.getRenderer();

		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
		}
		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
		
		return chart;
	}

	private XYDataset createDataset() {
		TimeSeries profitSeries = new TimeSeries("�������");
		TimeSeries cleanProfitSeries = new TimeSeries("������ �������");

		for (int i = 0; i < recordsTable.getRowCount(); ++i) {
			int year = Integer.parseInt(String.valueOf(recordsTable.getValueAt(i, 1)));
			int quarter = Integer.parseInt(String.valueOf(recordsTable.getValueAt(i, 2)));
			int month = quarter * 3;
			double profit = Double.parseDouble(String.valueOf(recordsTable.getValueAt(i, 5)));
			double cleanProfit = Double.parseDouble(String.valueOf(recordsTable.getValueAt(i, 6)));

			profitSeries.add(new Month(month, year), profit);
			cleanProfitSeries.add(new Month(month, year), cleanProfit);
		}

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(profitSeries);
		dataset.addSeries(cleanProfitSeries);

		return dataset;
	}

	private DefaultTableModel getRecordsTableModel() throws RemoteException, NotBoundException {
		DefaultTableModel tableModel = null;

		try {
			Registry registry = LocateRegistry.getRegistry(PreferencesFrame.ADDRESS, PreferencesFrame.PORT);
			IRecordsActivities remoteObject = (IRecordsActivities) registry.lookup("RecordsActivities");
			tableModel = remoteObject.getRecordsTableModel(true);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
		}

		return tableModel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == chooseModeMI) {
			hideFrame();
			WorkModeChoiceFrame.frame.showFrame(user);
		} else if (e.getSource() == reloginMI) {
			hideFrame();
			AuthorizationFrame.frame.showFrame(null);
		} else if (e.getSource() == exitMI) {
			System.exit(0);
		} else if (e.getSource() == howToMI) {
			JOptionPane.showMessageDialog(howToMI, "������������� �� �������� ��� ����������� ����� �������� � �������� � ������� ����� �������.", "��� ������?", JOptionPane.INFORMATION_MESSAGE);
		} else if (e.getSource() == whatIsItMI) {
			JOptionPane.showMessageDialog(howToMI, "���� � ������� ����� �������", "��� ���?", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		if (isVisible() == true) {
			hideFrame();
			RecordsTableFrame.frame.showFrame(user);
		}
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}
}
