// Переработать дропы БД

package server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import server.frames.PreferencesFrame;

public class DBSchema {
	private static final String CONN_STRING = "jdbc:mysql://localhost/enterprise?useUnicode=true&characterEncoding=utf8";

	private static final String DROP_DATABASE = "DROP DATABASE IF EXISTS Enterprise";
	private static final String DROP_RANKS_TABLE = "DROP TABLE IF EXISTS Ranks";
	private static final String DROP_USERS_TABLE = "DROP TABLE IF EXISTS Users";
	private static final String DROP_RECORDS_TABLE = "DROP TABLE IF EXISTS Records";
	private static final String DROP_PRODUCTS_TABLE = "DROP TABLE IF EXISTS ProductsList";
	private static final String DROP_RECORDSPRODUCTS_TABLE = "DROP TABLE IF EXISTS RecordsProducts";

	private static final String CREATE_DATABASE = "CREATE DATABASE IF NOT EXISTS Enterprise";
	
	private static final String CREATE_RANKS_TABLE = "CREATE TABLE IF NOT EXISTS Ranks ("
			+ "rank_id int(11) NOT NULL AUTO_INCREMENT," 
			+ "rank_name varchar(10) NOT NULL,"
			+ "PRIMARY KEY (rank_id)" 
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8";

	private static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS Users ("
			+ "user_id int(11) NOT NULL AUTO_INCREMENT," 
			+ "login varchar(24) UNIQUE NOT NULL,"
			+ "password varchar(24) NOT NULL," 
			+ "rank_id int(11) NOT NULL DEFAULT 2,"
			+ "PRIMARY KEY (user_id)," 
			+ "FOREIGN KEY (rank_id) REFERENCES Ranks (rank_id)"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8";

	private static final String CREATE_RECORDS_TABLE = "CREATE TABLE IF NOT EXISTS Records ("
			+ "record_id int(11) NOT NULL AUTO_INCREMENT," 
			+ "record_year year(4) NOT NULL,"
			+ "record_quarter int(1) NOT NULL,"
			+ "profit_tax int(11) NOT NULL,"
			+ "user_id int(11) NOT NULL," 
			+ "PRIMARY KEY (record_id, record_year, record_quarter),"
			+ "FOREIGN KEY (user_id) REFERENCES Users (user_id),"
			+ "UNIQUE(record_year, record_quarter)"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8";

	private static final String CREATE_PRODUCTS_TABLE = "CREATE TABLE IF NOT EXISTS ProductsList ("
			+ "product_id int(11) NOT NULL AUTO_INCREMENT,"
			+ "product_name varchar(24) NOT NULL,"
			+ "PRIMARY KEY (product_id)"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
	
	private static final String CREATE_RECORDSPRODUCTS_TABLE = "CREATE TABLE IF NOT EXISTS RecordsProducts ("
			+ "recordproduct_id int(11) NOT NULL AUTO_INCREMENT,"
			+ "selling_price int(11) NOT NULL,"
			+ "cost_price int(11) NOT NULL,"
			+ "quantity int(11) NOT NULL,"
			+ "vat_rate int(4) NOT NULL,"
			+ "record_id int(11) NOT NULL,"
			+ "product_id int(11) NOT NULL,"
			+ "PRIMARY KEY (recordproduct_id),"
			+ "FOREIGN KEY (record_id) REFERENCES Records (record_id),"
			+ "FOREIGN KEY (product_id) REFERENCES ProductsList (product_id),"
			+ "UNIQUE (record_id, product_id)"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8";

	public static void createDatabase() throws SQLException {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost/", PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();
			statement.executeUpdate(CREATE_DATABASE);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void dropDatabase() throws SQLException {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();
			statement.executeUpdate(DROP_DATABASE);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void createRanksTable() throws SQLException {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();
			statement.executeUpdate(CREATE_RANKS_TABLE);
			statement.executeUpdate("INSERT INTO Ranks (rank_id, rank_name) VALUES ('1', 'Admin')");
			statement.executeUpdate("INSERT INTO Ranks (rank_id, rank_name) VALUES ('2', 'User')");
		} catch (SQLException e) {
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	public static void dropRanksTable() throws SQLException {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();
			statement.executeUpdate(DROP_RANKS_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	public static void createUsersTable() throws SQLException {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();
			statement.executeUpdate(CREATE_USERS_TABLE);
			statement.executeUpdate("INSERT INTO Users (user_id, login, password, rank_id) VALUES ('1', 'Admin', 'pwd', '1')");
		} catch (SQLException e) {
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	public static void dropUsersTable() throws SQLException {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();
			statement.executeUpdate(DROP_USERS_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	public static void createRecordsTable() throws SQLException {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();
			statement.executeUpdate(CREATE_RECORDS_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	public static void dropRecordsTable() throws SQLException {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();
			statement.executeUpdate(DROP_RECORDS_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	public static void createProductsTable() throws SQLException {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();
			statement.executeUpdate(CREATE_PRODUCTS_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	public static void dropProductsTable() throws SQLException {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();
			statement.executeUpdate(DROP_PRODUCTS_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void createRecordsProductsTable() throws SQLException {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();
			statement.executeUpdate(CREATE_RECORDSPRODUCTS_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void dropRecordsProductsTable() throws SQLException {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();
			statement.executeUpdate(DROP_RECORDSPRODUCTS_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void main(String[] args) {
		try {
			DBSchema.createDatabase();
			DBSchema.createRanksTable();
			DBSchema.createUsersTable();
			DBSchema.createProductsTable();
			DBSchema.createRecordsTable();
			DBSchema.createRecordsProductsTable();
		} catch (SQLException e) {
			System.err.println(e);
		}
	}
}