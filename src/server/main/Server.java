package server.main;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;

import server.db.DBRecordsActivities;
import server.db.DBSchema;
import server.db.DBUsersActivities;
import server.frames.PreferencesFrame;
import server.frames.ServerMainFrame;
import all.net.IRecordsActivities;
import all.net.IUsersActivities;

public class Server {
	private static int CURRENT_PORT = -1;
	private static Registry registry = null;
	private static IUsersActivities usersActivities = null;
	private static IRecordsActivities recordsActivities = null;
	private static Remote uaStub = null;
	private static Remote raStub = null;
	
	public static void main(String[] args) throws Exception {
		try {
			PreferencesFrame.frame.showFrame();
	
			DBSchema.main(args);
		} catch(Exception e) {
			addMessageToLog(e.getMessage());
		}
	}
	
	public static void configureRegistry(int port) {
		try {
			if(CURRENT_PORT != port) {
				if(registry != null) {
					UnicastRemoteObject.unexportObject(registry, true);
					addMessageToLog("UNBOUND REGISTRY");
				}
				CURRENT_PORT = port;
			}
			
			registry = LocateRegistry.createRegistry(CURRENT_PORT);
			
			if(usersActivities == null) {
				usersActivities = new DBUsersActivities();
			}
			
			if(recordsActivities == null) {
				recordsActivities = new DBRecordsActivities();
			}
	
			if(uaStub == null) {
				uaStub = UnicastRemoteObject.exportObject(usersActivities, 0);
			}
			
			if(raStub == null) {
				raStub = UnicastRemoteObject.exportObject(recordsActivities, 0);
			}
	
			registry.bind("UsersActivities", uaStub);
			registry.bind("RecordsActivities", raStub);

			ServerMainFrame.start();
			
			addMessageToLog("Сервер запущен.");
		} catch(Exception e) {
			addMessageToLog(e.getMessage());
		}
	}
	
	private static void addMessageToLog(String message) {
		Date d = new Date();
        SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");    
		ServerMainFrame.frame.addMessage("[" + format1.format(d) + "] " + message);
	}
}