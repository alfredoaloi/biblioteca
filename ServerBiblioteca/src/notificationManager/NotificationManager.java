package notificationManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import databaseManagement.DatabaseConnection;

public class NotificationManager implements Runnable {
	
	private enum DeadlineStatus {GREEN, YELLOW, RED, BLACK, MAX_STATUS};
	private DatabaseConnection databaseConnection;
	private Map<String, DeadlineStatus[]> userBooksDeadlineStatus;
	
	public NotificationManager() throws SQLException {
		databaseConnection = new DatabaseConnection();
		userBooksDeadlineStatus = new HashMap<String, NotificationManager.DeadlineStatus[]>();
	}
	
	private void createDeadlineStatusMap(Map<String, DeadlineStatus[]> map) throws SQLException {
		ResultSet rs = databaseConnection.getUsersNumBooksGroupedByStatus();
		while(rs.next()) {
			DeadlineStatus[] deadlineArray = new DeadlineStatus[4];
		}
	}

	@Override
	public void run() {
		
	}

}
