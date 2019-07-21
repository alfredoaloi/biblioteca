package clientBiblioteca;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import imageReceiver.ImageReceiver;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Pair;

public class Client implements Runnable {

	private Socket socket = null;
	private Gson gson;
	private ArrayList<Category> categoryList;
	private ImageReceiver imageReceiver;
	private BufferedReader in;
	private PrintWriter out;

	public Client() {
		try {
			this.socket = new Socket("localhost", 8000);
			this.gson = new GsonBuilder().create();
			this.categoryList = new ArrayList<Category>();
			this.imageReceiver = new ImageReceiver("images", new Socket("localhost", 8001));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
			Thread t = new Thread(this);
			t.start();
		} catch (IOException e) {
//			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Errore");
			alert.setHeaderText(null);
			alert.setContentText("Connessione al server fallita!");
			alert.showAndWait();
			System.exit(0);
		}
	}

	@Override
	public void run() {
		try {
			if (socket == null)
				return;

			String s1 = in.readLine();
			String s2 = in.readLine();
			Envelope<String[]> envelope1 = new Envelope<String[]>();
			Envelope<String[]> envelope2 = new Envelope<String[]>();
			envelope1 = gson.fromJson(s1, new TypeToken<Envelope<String[]>>() {
			}.getType());
			envelope2 = gson.fromJson(s2, new TypeToken<Envelope<String[]>>() {
			}.getType());
			if (envelope1.getObject().equals("BOOK_LIST")) {
				String[] cat = envelope1.getContent();
				for (String c : cat) {
					System.out.println(c);
					categoryList.add(gson.fromJson(c, Category.class));
				}
			}
			if (envelope2.getObject().equals("IMAGE_LIST")) {
				String[] im = envelope2.getContent();
				imageReceiver.receiveImagesFromServer(im);
			}

			Envelope<AccessCredentials> envelope3 = new Envelope<AccessCredentials>();
			envelope3.setObject("ACCESS_CREDENTIALS");
			envelope3.setContent(new AccessCredentials("franc1", "bellecose"));
			out.append(gson.toJson(envelope3) + "\n");
			out.flush();

			String input = in.readLine();
			JsonObject jsonObject = new JsonParser().parse(input).getAsJsonObject();

			if (jsonObject.get("object").toString().equals("\"FAILURE\"")) {
				Envelope<String> reportEnvelope = gson.fromJson(input, new TypeToken<Envelope<String>>() {
				}.getType());
				System.out.println(reportEnvelope.getContent());
			} else {
				Envelope<String> userCredentialsEnvelope = gson.fromJson(input, new TypeToken<Envelope<String>>() {
				}.getType());
				Customer c = gson.fromJson(userCredentialsEnvelope.getContent(), Customer.class);
				System.out.println(c.getUsername());
			}
			envelope3 = new Envelope<AccessCredentials>();
			envelope3.setObject("ACCESS_CREDENTIALS");
			envelope3.setContent(new AccessCredentials("aaaaa", "aaaaaa"));
			out.append(gson.toJson(envelope3) + "\n");
			out.flush();

			input = in.readLine();
			jsonObject = new JsonParser().parse(input).getAsJsonObject();

			if (jsonObject.get("object").toString().equals("\"FAILURE\"")) {
				Envelope<String> reportEnvelope = gson.fromJson(input, new TypeToken<Envelope<String>>() {
				}.getType());
				System.out.println(reportEnvelope.getContent());
			} else {
				Envelope<String> userCredentialsEnvelope = gson.fromJson(input, new TypeToken<Envelope<String>>() {
				}.getType());
				System.out.println(userCredentialsEnvelope.getContent());
			}
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Category> getCategoryList() {
		return categoryList;
	}

	public String loginUser(Pair<String, String> login) {
		String input = "";
		Envelope<AccessCredentials> accessCredentialsEnvelope = new Envelope<AccessCredentials>();
		accessCredentialsEnvelope.setObject("ACCESS_CREDENTIALS");
		accessCredentialsEnvelope.setContent(new AccessCredentials(login.getKey(), login.getValue()));
		out.append(gson.toJson(accessCredentialsEnvelope) + "\n");
		out.flush();
		try {
			input = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return input;
	}

	public ArrayList<LentBook> libriNoleggiati(String username) {
		ArrayList<LentBook> noleggiati = new ArrayList<LentBook>();

		Envelope<String> envelope = new Envelope<String>("GET_CUSTOMER_LENT_BOOKS", username);
		System.out.println(username);
		out.append(gson.toJson(envelope) + "\n");
		out.flush();
		String input = "";
		try {
			input = in.readLine();
			System.out.println(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Envelope<String[]> array = gson.fromJson(input, new TypeToken<Envelope<String[]>>() {
		}.getType());

		for (String s : array.getContent()) {
			System.out.println(s);
			noleggiati.add(gson.fromJson(s, LentBook.class));
		}

		return noleggiati;
	}

	public ArrayList<Customer> getCustomersList() {
		ArrayList<Customer> customers = new ArrayList<Customer>();

		Envelope<String> envelope = new Envelope<String>("GET_CUSTOMER_LIST", "");
		out.append(gson.toJson(envelope) + "\n");
		out.flush();
		String input = "";
		try {
			input = in.readLine();
			System.out.println(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Envelope<String[]> array = gson.fromJson(input, new TypeToken<Envelope<String[]>>() {
		}.getType());

		for (String s : array.getContent()) {
			System.out.println(s);
			customers.add(gson.fromJson(s, Customer.class));
		}

		return customers;
	}
	
	public ArrayList<User> getEmployeesList() {
		ArrayList<User> user = new ArrayList<User>();

		Envelope<String> envelope = new Envelope<String>("GET_EMPLOYEE_LIST", "");
		out.append(gson.toJson(envelope) + "\n");
		out.flush();
		String input = "";
		try {
			input = in.readLine();
			System.out.println(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Envelope<String[]> array = gson.fromJson(input, new TypeToken<Envelope<String[]>>() {
		}.getType());

		for (String s : array.getContent()) {
			System.out.println(s);
			user.add(gson.fromJson(s, User.class));
		}

		return user;
	}

	public String setCustomerLentBook(String username, ArrayList<Book> carrello) {
		Envelope<String> envelope = new Envelope<String>("SET_CUSTOMER_LENT_BOOKS", username);
		Book[] temp = carrello.toArray(new Book[carrello.size()]);
		Envelope<Book[]> array = new Envelope<Book[]>("", temp);
		out.append(gson.toJson(envelope) + "\n");
		out.flush();
		out.append(gson.toJson(array) + "\n");
		out.flush();
		String input = "";
		try {
			input = in.readLine();
			System.out.println(input);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return input;
	}

	public void refreshDB() {
		categoryList.clear();
		Envelope<String> envelope = new Envelope<String>("REFRESH", "");
		out.append(gson.toJson(envelope) + "\n");
		out.flush();

		try {
			String s1 = in.readLine();
			String s2 = in.readLine();
			Envelope<String[]> envelope1 = new Envelope<String[]>();
			Envelope<String[]> envelope2 = new Envelope<String[]>();
			envelope1 = gson.fromJson(s1, new TypeToken<Envelope<String[]>>() {
			}.getType());
			envelope2 = gson.fromJson(s2, new TypeToken<Envelope<String[]>>() {
			}.getType());
			if (envelope1.getObject().equals("BOOK_LIST")) {
				String[] cat = envelope1.getContent();
				for (String c : cat) {
					System.out.println(c);
					categoryList.add(gson.fromJson(c, Category.class));
				}
			}
			if (envelope2.getObject().equals("IMAGE_LIST")) {
				String[] im = envelope2.getContent();
				imageReceiver.receiveImagesFromServer(im);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String returnBook(LentBook book) {
		Book[] temp = new Book[1];
		temp[0] = book;
		Envelope<Book[]> envelope = new Envelope<Book[]>("SET_CUSTOMER_RETURNED_BOOKS", temp);
		out.append(gson.toJson(envelope) + "\n");
		out.flush();
		String input = "";
		try {
			input = in.readLine();
			System.out.println(input);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return input;
	}

	public ImageReceiver getImageReceiver() {
		return imageReceiver;
	}

	public String delCustomer(String username) {
		Envelope<String> envelope = new Envelope<String>("DELETE_CUSTOMER", username);
		out.append(gson.toJson(envelope) + "\n");
		out.flush();
		String input = "";
		try {
			input = in.readLine();
			System.out.println(input);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return input;
	}
	
	public String delUser(String username) {
		Envelope<String> envelope = new Envelope<String>("DELETE_EMPLOYEE", username);
		out.append(gson.toJson(envelope) + "\n");
		out.flush();
		String input = "";
		try {
			input = in.readLine();
			System.out.println(input);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return input;
	}

	public String delBook(Book book) {
		Book[] temp = new Book[1];
		temp[0] = book;
		Envelope<Book[]> envelope = new Envelope<Book[]>("DELETE_BOOK", temp);
		out.append(gson.toJson(envelope) + "\n");
		out.flush();
		String input = "";
		try {
			input = in.readLine();
			System.out.println(input);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return input;
	}
	
	public String addCustomer(Customer customer) {
		Envelope<Customer> envelope = new Envelope<Customer>("NEW_CUSTOMER", customer);
		out.append(gson.toJson(envelope) + "\n");
		out.flush();
		String input = "";
		try {
			input = in.readLine();
			System.out.println(input);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return input;
	}
	
	public String modCustomer(Customer customer) {
		Envelope<Customer> envelope = new Envelope<Customer>("UPDATE_CUSTOMER", customer);
		out.append(gson.toJson(envelope) + "\n");
		out.flush();
		String input = "";
		try {
			input = in.readLine();
			System.out.println(input);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return input;
	}
	
	public String addCategory(Category category) {
		Category[] temp = new Category[1];
		temp[0] = category;
		Envelope<Category[]> envelope = new Envelope<Category[]>("NEW_BOOK", temp);
		out.append(gson.toJson(envelope) + "\n");
		out.flush();
		String input = "";
		try {
			input = in.readLine();
			System.out.println(input);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return input;
	}
	
	public String modCategory(Category category) {
		Category[] temp = new Category[1];
		temp[0] = category;
		Envelope<Category[]> envelope = new Envelope<Category[]>("UPDATE_BOOK", temp);
		out.append(gson.toJson(envelope) + "\n");
		out.flush();
		String input = "";
		try {
			input = in.readLine();
			System.out.println(input);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return input;
	}
	
	public String addUser(User user) {
		Envelope<User> envelope = new Envelope<User>("NEW_EMPLOYEE", user);
		System.out.println(gson.toJson(envelope));
		out.append(gson.toJson(envelope) + "\n");
		out.flush();
		String input = "";
		try {
			input = in.readLine();
			System.out.println(input);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return input;
	}
	
	public String modUser(User user) {
		Envelope<User> envelope = new Envelope<User>("UPDATE_EMPLOYEE", user);
		out.append(gson.toJson(envelope) + "\n");
		out.flush();
		String input = "";
		try {
			input = in.readLine();
			System.out.println(input);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return input;
	}
	
}
