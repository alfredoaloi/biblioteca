package clientBiblioteca;

public class User extends AccessCredentials{
	String name;
	String Surname;
	String E_mail;
	
	public User(String username, String password, String name, String surname, String e_mail) {
		super(username, password);
		this.username = username;
		this.password = password;
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
	
	
}
