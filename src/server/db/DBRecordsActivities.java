package server.db;

import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import server.frames.PreferencesFrame;
import server.frames.ServerMainFrame;
import all.entities.Product;
import all.entities.Record;
import all.net.IRecordsActivities;

public class DBRecordsActivities implements IRecordsActivities {
	private static final String CONN_STRING = "jdbc:mysql://localhost/enterprise?useUnicode=true&characterEncoding=utf8";

	// Возвращает список отчётов
	@Override
	public DefaultTableModel getRecordsTableModel(boolean isDiagram) throws SQLException {
		addMessageToLog("Запрос списка записей");

		Connection connection = null;
		String login = null;
		
		DefaultTableModel dtm = new DefaultTableModel();

		dtm.addColumn("ID записи");
		dtm.addColumn("Год данных");
		dtm.addColumn("Квартал года");
		dtm.addColumn("Ставка налога на прибыль");
		dtm.addColumn("Редактировал пользователь");
		dtm.addColumn("Прибыль");
		dtm.addColumn("Чистая прибыль");
		
		if(isDiagram) {
			dtm.addColumn("Изменение прибыли");
			dtm.addColumn("Изменение чистой прибыли");
		}

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement recordsStatement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet recordsResultSet = recordsStatement
					.executeQuery("SELECT record_id, record_year, record_quarter, profit_tax, user_id " + "FROM Records "
							+ "ORDER BY record_year ASC, record_quarter ASC");
			
			double prevPeriodProfit = 0;
			double prevPeriodCleanProfit = 0;

			while (recordsResultSet.next()) {
				Vector<String> userData = new Vector<String>();

				Statement loginStatement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ResultSet loginResultSet = loginStatement.executeQuery("SELECT login " + "FROM Users " + "WHERE user_id='"
						+ recordsResultSet.getString(5) + "'");
				loginResultSet.next();
				login = loginResultSet.getString(1);

				userData.addElement(recordsResultSet.getString(1));
				userData.addElement(refactorYear(recordsResultSet.getString(2)));
				userData.addElement(recordsResultSet.getString(3));
				userData.addElement(recordsResultSet.getString(4));
				userData.addElement(login);

				double profit = getProfitByRecordId(Integer.parseInt(recordsResultSet.getString(1)));
				double cleanProfit = Double.parseDouble(String.valueOf(profit * ((100 - Double.parseDouble(recordsResultSet.getString(4))) / 100)));
				
				userData.addElement(String.valueOf(profit));
				userData.addElement(String.valueOf(cleanProfit));
				
				if(isDiagram) {
					userData.addElement(String.valueOf(profit - prevPeriodProfit));
					userData.addElement(String.valueOf(profit - prevPeriodCleanProfit));
					prevPeriodProfit = profit;
					prevPeriodCleanProfit = cleanProfit;
				}

				dtm.addRow(userData);
			}
			return dtm;
		} catch (SQLException e) {
			addMessageToLog(e.getMessage());
			throw e;
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					addMessageToLog(e.getMessage());
				}
		}
	}

	// Возвращает данные одного отчёта по ID
	@Override
	public DefaultTableModel getRecordTableModel(Record record) throws SQLException {
		Connection connection = null;
		String login = null;
		DefaultTableModel dtm = new DefaultTableModel();

		dtm.addColumn("ID записи");
		dtm.addColumn("Год данных");
		dtm.addColumn("Квартал года");
		dtm.addColumn("Ставка налога на прибыль");
		dtm.addColumn("Редактировал пользователь");
		dtm.addColumn("Чистая прибыль");
		dtm.addColumn("Прибыль");

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement recordStatement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			ResultSet recordResultSet = recordStatement
					.executeQuery("SELECT record_id, record_year, record_quarter, profit_tax, user_id " + "FROM Records "
							+ "WHERE record_id='" + record.getId() + "'");

			while (recordResultSet.next()) {
				Vector<String> userData = new Vector<String>();

				Statement loginStatement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ResultSet loginResultSet = loginStatement.executeQuery("SELECT login " + "FROM Users " + "WHERE user_id='"
						+ recordResultSet.getString(5) + "'");
				loginResultSet.next();
				login = loginResultSet.getString(1);

				userData.addElement(recordResultSet.getString(1));
				userData.addElement(refactorYear(recordResultSet.getString(2)));
				userData.addElement(recordResultSet.getString(3));
				userData.addElement(recordResultSet.getString(4));
				userData.addElement(login);

				double profit = getProfitByRecordId((Integer.parseInt(recordResultSet.getString(1))));

				userData.addElement(String.valueOf(profit * ((100 - Double.parseDouble(recordResultSet.getString(4))) / 100)));
				userData.addElement(String.valueOf(profit));

				dtm.addRow(userData);
			}
			return dtm;
		} catch (SQLException e) {
			addMessageToLog(e.getMessage());
			throw e;
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					addMessageToLog(e.getMessage());
				}
		}
	}

	// Возвращает список продуктов в отчёте
	@Override
	public DefaultTableModel getProductsTableModel(Record record, String nameFilter) throws SQLException {
		String message = "Запрос списка продуктов записи №" + record.getId();
		
		if(nameFilter != null) {
			message += " с фильтром по названию продукта \"" + nameFilter + "\"";
		}
		
		addMessageToLog(message);

		Connection connection = null;
		DefaultTableModel dtm = new DefaultTableModel();

		dtm.addColumn("ID продукта");
		dtm.addColumn("Название продукта");
		dtm.addColumn("Отпускная цена за единицу");
		dtm.addColumn("Себестоимость за единицу");
		dtm.addColumn("Ставка НДС");
		dtm.addColumn("Количество");
		dtm.addColumn("Прибыль от продажи");

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			String query = "SELECT recordproduct_id, product_name, selling_price, cost_price, vat_rate, quantity, record_id "
					+ "FROM RecordsProducts " 
					+ "JOIN ProductsList "
					+ "ON RecordsProducts.product_id=ProductsList.product_id " 
					+ "WHERE record_id='" + record.getId() + "'";
			
			if(nameFilter != null) {
				query += "AND product_name LIKE '%" + nameFilter + "%'";
			}
			
			ResultSet recordsProductsResultSet = statement.executeQuery(query);

			while (recordsProductsResultSet.next()) {
				Vector<String> userData = new Vector<String>();

				userData.addElement(recordsProductsResultSet.getString(1));
				userData.addElement(recordsProductsResultSet.getString(2));
				userData.addElement(recordsProductsResultSet.getString(3));
				userData.addElement(recordsProductsResultSet.getString(4));
				userData.addElement(recordsProductsResultSet.getString(5));
				userData.addElement(recordsProductsResultSet.getString(6));
				userData.addElement(String.valueOf(getProfitByProductId(Integer.parseInt(recordsProductsResultSet.getString(1)))));

				dtm.addRow(userData);
			}
			return dtm;
		} catch (SQLException e) {
			addMessageToLog(e.getMessage());
			throw e;
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					addMessageToLog(e.getMessage());
				}
		}
	}

	@Override
	public void addRecord(Record toAdd) throws SQLException {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();

			statement.executeUpdate("INSERT INTO Records (record_year, record_quarter, profit_tax, user_id) VALUES ('"
					+ toAdd.getYear() + "','" + toAdd.getQuarter() + "','" + toAdd.getProfitTax() + "','" + toAdd.getUserId() + "')");
		} catch (SQLException e) {
			addMessageToLog(e.getMessage());
			throw e;
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					addMessageToLog(e.getMessage());
				}
		}
	}

	@Override
	public void editRecord(Record prev, Record curr) throws SQLException {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();
			statement.executeUpdate("UPDATE Records " + "SET record_year='" + curr.getYear() + "', " + "record_quarter='"
					+ curr.getQuarter() + "', " + "profit_tax='" + curr.getProfitTax() + "', " + "user_id='" + curr.getUserId() + "' "
					+ "WHERE record_id='" + prev.getId() + "'");
		} catch (SQLException e) {
			addMessageToLog(e.getMessage());
			throw e;
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					addMessageToLog(e.getMessage());
				}
		}
	}

	@Override
	public void deleteRecord(int recordId) throws SQLException {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();

			statement.executeUpdate("DELETE FROM RecordsProducts " + "WHERE record_id='" + recordId + "'");
			statement.executeUpdate("DELETE FROM Records " + "WHERE record_id='" + recordId + "'");
		} catch (SQLException e) {
			addMessageToLog(e.getMessage());
			throw e;
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					addMessageToLog(e.getMessage());
				}
		}
	}

	@Override
	public void addProduct(Product product) throws SQLException {
		Connection connection = null;

		try {
			if(product.getName().equals("")) {
				throw new SQLException("Вы не ввели название продукта");
			} 
			else if(product.getSellingPrice() - product.getCostPrice() <= 0) {
				throw new SQLException("Разность отпускной цены и себестоимости не может быть равна нулю");
			}
			
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement addProductStatement = connection.createStatement();
			addProductStatement
					.executeUpdate("INSERT INTO RecordsProducts (selling_price, cost_price, vat_rate, quantity, record_id, product_id) VALUES ('"
							+ product.getSellingPrice()
							+ "','"
							+ product.getCostPrice()
							+ "','"
							+ product.getVatRate()
							+ "','"
							+ product.getQuantity()
							+ "','"
							+ product.getRecordId()
							+ "','"
							+ getProductIdByName(product.getName())
							+ "')");
		} catch (SQLException e) {
			addMessageToLog(e.getMessage());
			throw e;
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					addMessageToLog(e.getMessage());
				}
		}
	}

	@Override
	public void editProduct(Product prev, Product curr) throws RemoteException, SQLException {
		Connection connection = null;

		try {
			deleteProduct(prev.getId());
			addProduct(curr);
		} catch (SQLException e) {
			addMessageToLog(e.getMessage());
			throw e;
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					addMessageToLog(e.getMessage());
				}
		}
	}

	@Override
	public void deleteProduct(int productId) throws SQLException {
		Connection connection = null;

		int removeFromProductsList = 0;

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);

			Statement s1 = connection.createStatement();
			ResultSet selectProductsIdsResultSet = s1
					.executeQuery("SELECT RecordsProducts.recordproduct_id, RecordsProducts.product_id " + "FROM RecordsProducts "
							+ "JOIN ProductsList " + "ON RecordsProducts.product_id=ProductsList.product_id "
							+ "WHERE recordproduct_id='" + productId + "'");

			selectProductsIdsResultSet.next();

			if (selectProductsIdsResultSet.next() == false) {
				selectProductsIdsResultSet.previous();
				removeFromProductsList = Integer.parseInt(selectProductsIdsResultSet.getString(2));
			}

			Statement s2 = connection.createStatement();
			s2.executeUpdate("DELETE FROM RecordsProducts " + "WHERE recordproduct_id='" + productId + "'");

			Statement s3 = connection.createStatement();
			s3.executeUpdate("DELETE FROM ProductsList WHERE product_id='" + removeFromProductsList + "'");
		} catch (SQLException e) {
			addMessageToLog(e.getMessage());
			throw e;
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					addMessageToLog(e.getMessage());
				}
		}
	}

	private int getProductIdByName(String name) throws SQLException {
		int productId = 0;
		Connection connection = null;

		addMessageToLog(name);

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement productStatement = connection.createStatement();
			ResultSet productResultSet = productStatement.executeQuery("SELECT product_id FROM ProductsList " + "WHERE product_name='"
					+ name + "'");

			if (productResultSet.next() == false) {
				Statement addProductToListStatement = connection.createStatement();
				addProductToListStatement.executeUpdate("INSERT INTO ProductsList (product_name) VALUES ('" + name + "')");
				productResultSet = productStatement.executeQuery("SELECT product_id FROM ProductsList " + "WHERE product_name='"
						+ name + "'");
				productResultSet.next();
				productId = Integer.parseInt(productResultSet.getString(1));
			} else {
				productId = Integer.parseInt(productResultSet.getString(1));
			}

			return productId;
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	private double getProfitByRecordId(int recordId) throws SQLException {
		Connection connection = null;
		double profit = 0;

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();
			ResultSet productsResultSet = statement.executeQuery("SELECT recordproduct_id FROM RecordsProducts " + "WHERE record_id='"
					+ recordId + "'");

			while (productsResultSet.next()) {
				int productId = Integer.parseInt(productsResultSet.getString(1));
				profit += getProfitByProductId(productId);
			}

		} finally {
			if (connection != null)
				connection.close();
		}

		return profit;
	}

	private double getProfitByProductId(int productId) throws SQLException {
		double profit = 0;
		double selling_price = 0;
		double cost_price = 0;
		double vat_rate = 0;
		double quantity = 0;
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();
			ResultSet productResultSet = statement.executeQuery("SELECT selling_price, cost_price, vat_rate, quantity "
					+ "FROM RecordsProducts " + "WHERE recordproduct_id='" + productId + "'");
			productResultSet.next();

			selling_price = Double.parseDouble(productResultSet.getString(1));
			cost_price = Double.parseDouble(productResultSet.getString(2));
			vat_rate = Double.parseDouble(productResultSet.getString(3));
			quantity = Double.parseDouble(productResultSet.getString(4));

			profit = ((selling_price - cost_price) * ((100 - vat_rate) / 100)) * quantity;
		} finally {
			if (connection != null)
				connection.close();
		}

		return profit;
	}

	private String refactorYear(String i_year) {
		String o_year = null;

		o_year = i_year.split("-")[0];

		return o_year;
	}

	private void addMessageToLog(String message) {
		try {
			Date d = new Date();
			SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
			ServerMainFrame.frame.addMessage("[" + format1.format(d) + "] (" + RemoteServer.getClientHost() + ") " + message);
		} catch (ServerNotActiveException e) {
			e.printStackTrace();
		}
	}
}
