package all.net;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

import javax.swing.table.DefaultTableModel;

import all.entities.User;
import all.exceptions.AuthorizationFailException;
import all.exceptions.DataLengthException;

public interface IUsersActivities extends Remote {

	User processAuthorization(final String login, final char[] password) throws RemoteException,
			SQLException, AuthorizationFailException, DataLengthException;

	DefaultTableModel getUsersTableModel(String nameFilter) throws RemoteException, SQLException;

	void addUser(User toAdd) throws RemoteException, SQLException, DataLengthException;

	void editUser(User previous, User current) throws RemoteException, SQLException,
			DataLengthException;

	void deleteUser(int userId) throws RemoteException, SQLException;

	int getUserIdByLogin(String login) throws RemoteException, SQLException;
}
