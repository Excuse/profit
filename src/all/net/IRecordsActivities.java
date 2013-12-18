package all.net;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

import javax.swing.table.DefaultTableModel;

import all.entities.Product;
import all.entities.Record;

public interface IRecordsActivities extends Remote {

	DefaultTableModel getRecordsTableModel(boolean forDiagram) throws RemoteException, SQLException;

	DefaultTableModel getRecordTableModel(Record record) throws RemoteException, SQLException;
	
	DefaultTableModel getProductsTableModel(Record record, String filter) throws RemoteException, SQLException;
	
	void addRecord(Record toAdd) throws RemoteException, SQLException;
	
	void editRecord(Record prev, Record curr) throws RemoteException, SQLException;
	
	void deleteRecord(int recordId) throws RemoteException, SQLException;
	
	void addProduct(Product product) throws RemoteException, SQLException;
	
	void editProduct(Product prev, Product curr) throws RemoteException, SQLException;

	void deleteProduct(int productId) throws RemoteException, SQLException;
}
