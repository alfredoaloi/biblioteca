package notificationManager;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import databaseManagement.DatabaseConnection;

public class NotificationManager implements Runnable {
	
	private enum DeadlineStatus {GREEN, YELLOW, RED, BLACK};
	private DatabaseConnection databaseConnection;
	private Map<String, int[]> userBooksDeadlineStatuses;
	private Map<String, String> userMails;
	
	public NotificationManager(DatabaseConnection databaseConnection) throws SQLException {
		this.databaseConnection = databaseConnection;
		this.userBooksDeadlineStatuses = new HashMap<String, int[]>();
		this.userMails = new HashMap<String, String>();
	}
	
	private void createDeadlineStatusMap() throws SQLException {
		ResultSet rs = databaseConnection.getUsersNumBooksGroupedByStatus();
		DeadlineStatus[] deadlineStatuses = DeadlineStatus.values();
		
		while(rs.next()) {
			int[] deadlineArray = new int[DeadlineStatus.values().length];
			for(DeadlineStatus d : deadlineStatuses)
			{
				deadlineArray[d.ordinal()] = rs.getInt(d.toString());
			}
			System.out.println(deadlineArray);
			userBooksDeadlineStatuses.put(rs.getString("User_ID"), deadlineArray);
		}
	}
	
	private void createUserMailsMap() throws SQLException {
		ResultSet IdMailResultSet = databaseConnection.getUserIDWithEmail();
		
		while(IdMailResultSet.next())
		{
			ResultSet userStatusResultSet = databaseConnection.getUserDeadlineStatus(IdMailResultSet.getString(1));
			int[] userNumBooksStatus = userBooksDeadlineStatuses.get(IdMailResultSet.getString(1));
			DeadlineStatus d = DeadlineStatus.valueOf(userStatusResultSet.getString(1));
			if(userNumBooksStatus[d.ordinal()] == 0)
				for(int i = d.ordinal() - 1; i >= 0; i--)
					if(userNumBooksStatus[i] > 0 || i == DeadlineStatus.GREEN.ordinal()) {
						databaseConnection.setCustomerStatus(IdMailResultSet.getString(1), DeadlineStatus.values()[i].toString());
						d = DeadlineStatus.values()[i];
				}
			if(d.ordinal() == DeadlineStatus.BLACK.ordinal()) {
				ResultSet customerPastExpiredBooks = databaseConnection.getPastExpiredBooksFromCustomer(IdMailResultSet.getString(1));				
				if(customerPastExpiredBooks.getInt(1) < userNumBooksStatus[d.ordinal()]) {
					databaseConnection.updatePastExpiredBooksForCustomer(IdMailResultSet.getString(1), userNumBooksStatus[d.ordinal()]);
					userMails.put(IdMailResultSet.getString(1), IdMailResultSet.getString(2));
				}
			}
			else
			{
				for(int i = d.ordinal() + 1; i < DeadlineStatus.values().length; i++) {
					if(userNumBooksStatus[i] > 0) {
						if(i == DeadlineStatus.BLACK.ordinal()) { System.out.println(userNumBooksStatus[i]);
							databaseConnection.setNewPastExpiredBooksForCustomer(IdMailResultSet.getString(1), userNumBooksStatus[i]);}
						userMails.put(IdMailResultSet.getString(1), IdMailResultSet.getString(2));
						databaseConnection.setCustomerStatus(IdMailResultSet.getString(1), DeadlineStatus.values()[i].toString());
					}
				}
			}
		}
	}
	
	@Override
	public void run() {
		try {
			DeadlineManager deadlineManager = new DeadlineManager(databaseConnection);
			MailTemplateBuilder templateBuilder = new MailTemplateBuilder(databaseConnection);
			SMTPServerManager serverManager = new SMTPServerManager();
			deadlineManager.updateBookStatuses();
			createDeadlineStatusMap();
			createUserMailsMap();
			System.out.println(userBooksDeadlineStatuses);
			System.out.println(userMails);
			Set<Map.Entry<String, String>> entrySet = userMails.entrySet();
			
			for(Map.Entry<String, String> entry : entrySet) {
				serverManager.sendEmail(templateBuilder.buildMailForCustomer(entry.getKey()), entry.getValue());
			}
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DATE, 1);
			
			System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
			
			databaseConnection.setNextScheduledDeadlineCheck(calendar.getTime());
			
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/*public static void main(String[] args) {
		try {
			NotificationManager tmp = new NotificationManager();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/

}
