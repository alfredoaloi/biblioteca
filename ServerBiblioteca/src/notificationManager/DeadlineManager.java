package notificationManager;

import java.sql.ResultSet;
import java.sql.SQLException;

import databaseManagement.DatabaseConnection;

public class DeadlineManager {
	
	private enum DeadlineStatus {
		GREEN(6, Integer.MAX_VALUE), 
		YELLOW(3, 5), 
		RED(0, 2),
		BLACK(Integer.MIN_VALUE, -1);
		
		private int lowerLimit;
		private int upperLimit;
		private DeadlineStatus(Integer lowerLimit, Integer upperLimit)
		{
				this.lowerLimit = lowerLimit;
				this.upperLimit = upperLimit;
		}
		
		public Integer getLowerLimit() { return lowerLimit; }
		public Integer getUpperLimit() { return upperLimit; }
	};
	DatabaseConnection databaseConnection;
	
	public DeadlineManager(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}
	
	public void updateBookStatuses() throws SQLException
	{
		ResultSet booksInformationResultSet = databaseConnection.getBooksInformation();
		
		while(booksInformationResultSet.next())
		{
			DeadlineStatus d = DeadlineStatus.valueOf(booksInformationResultSet.getString("Deadline_status"));
			int remainingDays = booksInformationResultSet.getInt("Remaining_days");
			
			if(d.ordinal() == DeadlineStatus.BLACK.ordinal()) {
				databaseConnection.setBookFine(booksInformationResultSet.getInt("Book_ID"), 
											   booksInformationResultSet.getInt("Fine") + 
											   booksInformationResultSet.getInt("Fine_increment"));		
			}
			
			else {
				if(remainingDays < d.getLowerLimit())
				{
					for(int i = d.ordinal() + 1; i < DeadlineStatus.values().length; i++)
					{
						if(remainingDays >= DeadlineStatus.values()[i].getLowerLimit() 
						   && remainingDays <= DeadlineStatus.values()[i].getUpperLimit())
						{
							if(DeadlineStatus.values()[i].ordinal() == DeadlineStatus.BLACK.ordinal())
								databaseConnection.setBookFine(booksInformationResultSet.getInt("Book_ID"), 
															   booksInformationResultSet.getInt("Fine_increment"));
							d = DeadlineStatus.values()[i];
							break;
						}
					}
					databaseConnection.setBookStatus(booksInformationResultSet.getInt("Book_ID"), d.toString());
				}
			}
		}
	}
}
