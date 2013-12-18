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
import all.entities.User;
import all.exceptions.AuthorizationFailException;
import all.exceptions.DataLengthException;
import all.net.IUsersActivities;

public class DBUsersActivities implements IUsersActivities {
	private static final String CONN_STRING = "jdbc:mysql://localhost/enterprise?useUnicode=true&characterEncoding=utf8";

	// �������� �� ������� ������������ � ������
	@Override
	public User processAuthorization(final String login, final char[] password) throws SQLException, DataLengthException,
			AuthorizationFailException {

		addMessageToLog("������� ����������� [�����: " + login + ", ������: " + String.valueOf(password) + "]");

		if (login.length() > 24 || login.length() == 0) {
			addMessageToLog("������ ����������� [�����: " + login + ", ������: " + String.valueOf(password) + "]");
			throw new DataLengthException("������������ �����");
		} else if (password.length > 24 || password.length == 0) {
			addMessageToLog("������ ����������� [�����: " + login + ", ������: " + String.valueOf(password) + "]");
			throw new DataLengthException("������������ ������");
		}

		Connection connection = null;

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = statement.executeQuery("SELECT user_id, login, password, rank_id " + "FROM Users");

			while (result.next()) {
				// �������� ����� �������
				if (result.getString(2).length() != login.length()) {
					continue;
				}
				// �������� ���������� �������
				else if (!result.getString(2).equals(login)) {
					continue;
				}
				// �������� ����� �������
				else if (result.getString(3).length() != password.length) {
					continue;
				}
				// �������� ���������� �������
				else if (result.getString(3).equals(String.valueOf(password))) {
					// ��������, �������� �� ���������������
					if (result.getString(4).equals("1")) {
						addMessageToLog("����� ����������� [�����: " + login + ", ������: " + String.valueOf(password)
								+ ", �������������]");
						return new User(Integer.parseInt(result.getString(1)), login, password, true);
					}
					// ��������, �������� �� ������� �������������
					else if (result.getString(4).equals("2")) {
						addMessageToLog("����� ����������� [�����: " + login + ", ������: " + String.valueOf(password)
								+ ", ������������]");
						return new User(Integer.parseInt(result.getString(1)), login, password, false);
					}
				}
			}
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

		try {
			throw new AuthorizationFailException("������ ����������� [�����: " + login + ", ������: " + String.valueOf(password) + "]");
		} catch (AuthorizationFailException e) {
			addMessageToLog(e.getMessage());
			throw e;
		}
	}

	// ���������� ������ �������������
	@Override
	public DefaultTableModel getUsersTableModel(String nameFilter) throws SQLException {
		String message = "������ ������ �������������";
		String query = "SELECT user_id, login, password, rank_id " + "FROM Users ";
		if(!nameFilter.equals("")) {
			query += "WHERE login LIKE '%" + nameFilter + "%'";
			message += " � �������� �� ����� ������������ \"" + nameFilter + "\"";
		}
		
		addMessageToLog(message);

		Connection connection = null;
		DefaultTableModel dtm = new DefaultTableModel();

		dtm.addColumn("ID ������������");
		dtm.addColumn("��� ������������");
		dtm.addColumn("������ ������������");
		dtm.addColumn("������ ������������");

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = statement.executeQuery(query);

			if (result == null) {
				return null;
			} else {
				while (result.next()) {
					Vector<String> userData = new Vector<String>();
					if (result.getString(4).equals("1")) {
						userData.addElement(result.getString(1));
						userData.addElement(result.getString(2));
						userData.addElement(result.getString(3));
						userData.addElement("�������������");
					} else {
						userData.addElement(result.getString(1));
						userData.addElement(result.getString(2));
						userData.addElement(result.getString(3));
						userData.addElement("������������");
					}
					dtm.addRow(userData);
				}
				return dtm;
			}
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
	public void addUser(User toAdd) throws SQLException, DataLengthException {
		String message = null;

		if (toAdd.getUsername().length() > 24 || toAdd.getUsername().length() == 0) {
			message = "������������� ����� ������������ �����";
			addMessageToLog(message);
			throw new DataLengthException("������������ �����");
		} else if (toAdd.getPassword().length > 24 || toAdd.getPassword().length == 0) {
			message = "������������� ����� ������������ �����";
			addMessageToLog(message);
			throw new DataLengthException("������������ ������");
		}

		Connection connection = null;
		String login = toAdd.getUsername();
		String password = String.valueOf(toAdd.getPassword());
		String isAdmin = toAdd.IsAdmin() ? "1" : "2";

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();
			statement.executeUpdate("INSERT INTO Users (login, password, rank_id) VALUES ('" + login + "','" + password + "','"
					+ isAdmin + "')");
			message = "������������ [�����: " + toAdd.getUsername() + "] ������� ��������";
		} catch (SQLException e) {
			message = "������������ [�����: " + toAdd.getUsername() + "] �� ��� ��������";
			throw e;
		} finally {
			addMessageToLog(message);
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					addMessageToLog(e.getMessage());
				}
		}
	}

	@Override
	public void editUser(User previous, User current) throws SQLException, DataLengthException {
		String message = null;
		Connection connection = null;

		if (current.getUsername().length() > 24 || current.getUsername().length() == 0) {
			message = "������������� ����� ������������ �����";
			addMessageToLog(message);
			throw new DataLengthException("������������ �����");
		} else if (current.getPassword().length > 24 || current.getPassword().length == 0) {
			message = "������������� ����� ������������ �����";
			addMessageToLog(message);
			throw new DataLengthException("������������ ������");
		}

		String prevLogin = previous.getUsername();
		String currLogin = current.getUsername();
		String currPassword = String.valueOf(current.getPassword());
		String currIsAdmin = current.IsAdmin() ? "1" : "2";

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();

			statement.executeUpdate("UPDATE Users " + "SET login='" + currLogin + "', " + "password='" + currPassword + "', "
					+ "rank_id='" + currIsAdmin + "' " + "WHERE login='" + prevLogin + "'");
			message = "������������ [�����: " + previous.getUsername() + "] ��� ������� ������";
		} catch (SQLException e) {
			message = "������������ [�����: " + previous.getUsername() + "] �� ��� ������";
			throw e;
		} finally {
			addMessageToLog(message);
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					addMessageToLog(e.getMessage());
				}
		}
	}

	@Override
	public void deleteUser(int userId) throws SQLException {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();
			statement.executeUpdate("DELETE FROM Users " + "WHERE user_id='" + userId + "'");
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
	public int getUserIdByLogin(String login) throws RemoteException, SQLException {
		Connection connection = null;
		int id = 0;

		try {
			connection = DriverManager.getConnection(CONN_STRING, PreferencesFrame.DBLOGIN, PreferencesFrame.DBPASSWORD);
			Statement statement = connection.createStatement();
			ResultSet idResultSet = statement.executeQuery("SELECT user_id FROM Users " + "WHERE login='" + login + "'");
			if (idResultSet != null) {
				idResultSet.next();
				id = Integer.parseInt(idResultSet.getString(1));
			}
		} finally {
			if (connection != null)
				connection.close();
		}

		return id;
	}

	private void addMessageToLog(String message) {
		try {
			Date d = new Date();
			SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
			ServerMainFrame.frame.addMessage("[" + format1.format(d) + "] (" + RemoteServer.getClientHost() + ") " + message);
		} catch (ServerNotActiveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
