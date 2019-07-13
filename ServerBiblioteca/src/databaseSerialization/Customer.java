package databaseSerialization;

public class Customer extends User{
	String userDeadlineStatus;
	int expiredBooks;
	
	public Customer(String username, String password, String name, String surname, String e_Mail,
			String userDeadlineStatus, int expiredBooks) {
		super(username, password, name, surname, e_Mail);
		this.userDeadlineStatus = userDeadlineStatus;
		this.expiredBooks = expiredBooks;
	}

	public String getUserDeadlineStatus() {
		return userDeadlineStatus;
	}

	public int getExpiredBooks() {
		return expiredBooks;
	}	
}
