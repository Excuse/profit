package server.tests;

import static org.junit.Assert.fail;

import java.rmi.RemoteException;
import java.sql.SQLException;

import org.junit.Test;

import server.db.DBRecordsActivities;
import server.db.DBUsersActivities;
import all.entities.Record;
import all.entities.User;
import all.exceptions.DataLengthException;

public class DatabaseTests {
	
	private User testUser = new User("Random", "12344321".toCharArray(), true);

	@Test
	public void getRecordsTableTest() throws SQLException {
		DBRecordsActivities dbra = new DBRecordsActivities();

		try {
			dbra.getRecordsTableModel(true);
			dbra.getRecordsTableModel(false);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			fail("Тест провален.");
		}
	}

	@Test
	public void getProductTableTest() throws SQLException {
		DBRecordsActivities dbra = new DBRecordsActivities();

		try {
			dbra.getProductsTableModel(new Record(1, 0, 0, 0), null);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			fail("Тест провален.");
		}
	}
	
	@Test
	public void getUsersTableTest() throws SQLException {
		DBUsersActivities dbua = new DBUsersActivities();

		try {
			dbua.getUsersTableModel("");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			fail("Тест провален.");
		}
	}
	
	@Test
	public void addUserTest() throws SQLException {
		DBUsersActivities dbua = new DBUsersActivities();

		try {
			dbua.addUser(testUser);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			fail("Тест провален.");
		} catch (DataLengthException e) {
			// TODO Auto-generated catch block
			fail("Тест провален.");
		}
	}
	
	@Test
	public void deleteUserTest() throws SQLException {
		DBUsersActivities dbua = new DBUsersActivities();

		try {
			dbua.deleteUser(dbua.getUserIdByLogin(testUser.getUsername()));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			fail("Тест провален.");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			fail("Тест провален.");
		}
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void addRecordTest() {
		try {
			Thread.currentThread().sleep(51);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			fail("Тест провален");
		}
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void deleteRecordTest() {
		try {
			Thread.currentThread().sleep(84);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			fail("Тест провален");
		}
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void addProductTest() {
		try {
			Thread.currentThread().sleep(61);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			fail("Тест провален");
		}
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void deleteProductTest() {
		try {
			Thread.currentThread().sleep(92);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			fail("Тест провален");
		}
	}
}
