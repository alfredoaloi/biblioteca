package clientBiblioteca;

public class User extends AccessCredentials{
	private String name;
	private String Surname;
	private String E_mail;
	
	public User(String username, String password, String name, String surname, String e_mail) {
		super(username, password);
		this.name = name;
		Surname = surname;
		E_mail = e_mail;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return Surname;
	}

	public String getE_mail() {
		return E_mail;
	}
	
	@Override
	public String toString() {
		return getUsername() + " - " + name + " " + Surname;
	}
	
}
