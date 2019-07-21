package clientBiblioteca;

public class Customer extends User {
	String userDeadlineStatus;

	public Customer(String username, String password, String name, String surname, String e_Mail,
			String userDeadlineStatus) {
		super(username, password, name, surname, e_Mail);
		this.userDeadlineStatus = userDeadlineStatus;
	}

	public String getUserDeadlineStatus() {
		return userDeadlineStatus;
	}

	@Override
	public String toString() {
		return getUsername() + " - " + getName() + " " + getSurname();
	}
}
