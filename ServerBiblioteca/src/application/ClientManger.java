package application;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import databaseManagement.DatabaseConnection;
import databaseSerialization.AccessCredentials;
import databaseSerialization.Book;
import databaseSerialization.Category;
import databaseSerialization.User;
import databaseSerialization.UserLentBooksJSONSerializer;
import envelopeManagement.Envelope;
import imageSender.ImageSender;
import statusReport.Failure;

public class ClientManger implements Runnable {

	private Socket socket;
	private ServerConnection serverConnection;
	private Gson gson;
	private String employeeUsername;
	private ImageSender imageSender;

	public ClientManger(Socket socket, Socket imageSocket, ServerConnection serverConnection) throws IOException {
		this.socket = socket;
		this.serverConnection = serverConnection;
		this.gson = new GsonBuilder().create();
		this.imageSender = new ImageSender("res", imageSocket, serverConnection.getImageListSerializer().getStringArray());
	}

	@Override
	public void run() {
		try {
			PrintWriter out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			Envelope<String[]> envelope = new Envelope<String[]>();
			
			String[] stringArray = serverConnection.getBookListJSONSerializer().getJSONStringArray();
			envelope.setObject("BOOK_LIST");
			envelope.setContent(stringArray);
			out.append(gson.toJson(envelope) + "\n");
			out.flush();
			
			stringArray = serverConnection.getImageListSerializer().getStringArray();
			envelope.setObject("IMAGE_LIST");
			envelope.setContent(stringArray);
			out.append(gson.toJson(envelope) + "\n");
			out.flush();
			imageSender.sendImagesToClient();
			
			while(true) {
				if(in.ready()) {
					String input = in.readLine();
					JsonObject jsonObject = new JsonParser().parse(input).getAsJsonObject();
					try {
						if(jsonObject.get("object").toString().equals("\"ACCESS_CREDENTIALS\"")) {
							Envelope<AccessCredentials> accessCredentialsEnvelope = 
												gson.fromJson(input, new TypeToken<Envelope<AccessCredentials>>(){}.getType());
							AccessCredentials ac = accessCredentialsEnvelope.getContent();
							String[] userList = serverConnection.getUserListJSONSerializer().getJSONCustomerArray();
							String[] employeeList = serverConnection.getUserListJSONSerializer().getJSONEmployeeArray();
							Envelope<String> userCredentialsEnvelope = new Envelope<String>();
							
							if(isUserIn(ac, userList)) {
								userCredentialsEnvelope.setObject("CUSTOMER");
								userCredentialsEnvelope.setContent(getUserFromCredentials(ac, userList));
							}
							else if(isUserIn(ac, employeeList)){
								userCredentialsEnvelope.setObject("EMPLOYEE");
								userCredentialsEnvelope.setContent(getUserFromCredentials(ac, employeeList));
								employeeUsername = ac.getUsername();
							}
							else
								throw new Failure("Credenziali di accesso errate");
							
							out.append(gson.toJson(userCredentialsEnvelope) + "\n");
							out.flush();
						}
						else if(jsonObject.get("object").toString().equals("\"NEW_CUSTOMER\"")) {
							Envelope<User> userEnvelope = gson.fromJson(input, new TypeToken<Envelope<User>>(){}.getType());
							DatabaseConnection db = serverConnection.getDatabaseConnection();
							db.insertNewCustomer(userEnvelope.getContent(), employeeUsername);
							serverConnection.refreshUserList();
						}
						else if(jsonObject.get("object").toString().equals("\"UPDATE_CUSTOMER\"")) {
							Envelope<User> userEnvelope = gson.fromJson(input, new TypeToken<Envelope<User>>(){}.getType());
							DatabaseConnection db = serverConnection.getDatabaseConnection();
							db.updateCustomer(userEnvelope.getContent(), employeeUsername);
							serverConnection.refreshUserList();
						}
						else if(jsonObject.get("object").toString().equals("\"DELETE_CUSTOMER\"")) {
							Envelope<String> usernameEnvelope = gson.fromJson(input, new TypeToken<Envelope<String>>(){}.getType());
							DatabaseConnection db = serverConnection.getDatabaseConnection();
							db.deleteCustomer(usernameEnvelope.getContent(), employeeUsername);
							serverConnection.refreshUserList();
						}
						else if(jsonObject.get("object").toString().equals("\"NEW_EMPLOYEE\"")) {
							Envelope<User> userEnvelope = gson.fromJson(input, new TypeToken<Envelope<User>>(){}.getType());
							DatabaseConnection db = serverConnection.getDatabaseConnection();
							db.insertNewCustomer(userEnvelope.getContent(), employeeUsername);
							serverConnection.refreshUserList();
						}
						else if(jsonObject.get("object").toString().equals("\"UPDATE_EMPLOYEE\"")) {
							Envelope<User> userEnvelope = gson.fromJson(input, new TypeToken<Envelope<User>>(){}.getType());
							DatabaseConnection db = serverConnection.getDatabaseConnection();
							db.updateCustomer(userEnvelope.getContent(), employeeUsername);
							serverConnection.refreshUserList();
						}
						else if(jsonObject.get("object").toString().equals("\"DELETE_EMPLOYEE\"")) {
							Envelope<String> usernameEnvelope = gson.fromJson(input, new TypeToken<Envelope<String>>(){}.getType());
							DatabaseConnection db = serverConnection.getDatabaseConnection();
							db.deleteCustomer(usernameEnvelope.getContent(), employeeUsername);
							serverConnection.refreshUserList();
						}
						else if(jsonObject.get("object").toString().equals("\"NEW_BOOK\"")) {
							Envelope<Category[]> categoryEnvelope = gson.fromJson(input, new TypeToken<Envelope<Category[]>>(){}.getType());
							DatabaseConnection db = serverConnection.getDatabaseConnection();
							for(Category category : categoryEnvelope.getContent()) {
								ResultSet categoryIDResultSet = db.getCategoryIDFromName(category.getCategoryType());
								if(!categoryIDResultSet.next()) {
									//inserisci in database nuova categoria
									categoryIDResultSet = db.getCategoryIDFromName(category.getCategoryType());
								}
								for(Book book : category.getBooks())
									db.insertNewBook(book, categoryIDResultSet.getInt(1), employeeUsername);
							}
							serverConnection.refreshBookList();
							serverConnection.refreshImageList();
						}
						else if(jsonObject.get("object").toString().equals("\"UPDATE_BOOK\"")) {
							Envelope<Category[]> categoryEnvelope = gson.fromJson(input, new TypeToken<Envelope<Category[]>>(){}.getType());
							DatabaseConnection db = serverConnection.getDatabaseConnection();
							for(Category category : categoryEnvelope.getContent()) {
								ResultSet categoryIDResultSet = db.getCategoryIDFromName(category.getCategoryType());
								if(!categoryIDResultSet.next()) {
									//inserisci in database nuova categoria
									categoryIDResultSet = db.getCategoryIDFromName(category.getCategoryType());
								}
								for(Book book : category.getBooks()) {
								ResultSet booksIDISBNResultSet = db.getBooksIDFromISBN(book.getISBN());
									while(booksIDISBNResultSet.next()) {
										db.updateBook(booksIDISBNResultSet.getInt(1), book, categoryIDResultSet.getInt(1), employeeUsername);
									}
								}
							}
							serverConnection.refreshBookList();
							serverConnection.refreshImageList();
						}
						else if(jsonObject.get("object").toString().equals("\"DELETE_BOOK\"")) {
							Envelope<Book[]> bookEnvelope = gson.fromJson(input, new TypeToken<Envelope<Book[]>>(){}.getType());
							DatabaseConnection db = serverConnection.getDatabaseConnection();
							for(Book book : bookEnvelope.getContent())
								db.deleteBook(book.getISBN(), employeeUsername);
							serverConnection.refreshBookList();
							serverConnection.refreshImageList();
						}
						else if(jsonObject.get("object").toString().equals("\"GET_CUSTOMER_LIST\"")) {
							String[] customerList = serverConnection.getUserListJSONSerializer().getJSONCustomerArray();
							Envelope<String[]> customerListEnvelope = new Envelope<String[]>("CUSTOMER_LIST", customerList);
							out.append(gson.toJson(customerListEnvelope) + "\n");
							out.flush();
						}
						else if(jsonObject.get("object").toString().equals("\"GET_EMPLOYEE_LIST\"")) {
							String[] employeeList = serverConnection.getUserListJSONSerializer().getJSONEmployeeArray();
							Envelope<String[]> employeeListEnvelope = new Envelope<String[]>("CUSTOMER_LIST", employeeList);
							out.append(gson.toJson(employeeListEnvelope) + "\n");
							out.flush();
						}
						else if(jsonObject.get("object").toString().equals("\"GET_CUSTOMER_LENT_BOOKS\"")) {
							Envelope<String> usernameEnvelope = gson.fromJson(input, new TypeToken<Envelope<String>>(){}.getType());
							UserLentBooksJSONSerializer lentBookSerializer = new UserLentBooksJSONSerializer(
																				serverConnection.getDatabaseConnection(),
																				usernameEnvelope.getContent());
							Envelope<String[]> customerListEnvelope = new Envelope<String[]>("CUSTOMER_LIST", 
																							 lentBookSerializer.getJSONStringArray());
							out.append(gson.toJson(customerListEnvelope) + "\n");
							out.flush();
						}
						else if(jsonObject.get("object").toString().equals("\"SET_CUSTOMER_LENT_BOOKS\"")) {
							Envelope<String> usernameEnvelope = gson.fromJson(input, new TypeToken<Envelope<String>>(){}.getType());
							Envelope<Book[]> lentBooksEnvelope = gson.fromJson(in.readLine(), new TypeToken<Envelope<Book[]>>(){}.getType());
							DatabaseConnection db = serverConnection.getDatabaseConnection();
							for(Book book : lentBooksEnvelope.getContent()) {
								db.updateCustomerLentBook(book.getISBN(), usernameEnvelope.getContent());
							}
						}
						else if(jsonObject.get("object").toString().equals("\"SET_CUSTOMER_RETURNED_BOOKS\"")) {
							Envelope<Book[]> returnedBooksEnvelope = 
															gson.fromJson(input, new TypeToken<Envelope<Book[]>>(){}.getType());
							DatabaseConnection db = serverConnection.getDatabaseConnection();
							for(Book book : returnedBooksEnvelope.getContent()) {
								db.updateCustomerReturnedBook(book.getBookID());
							}
						}
						else if(jsonObject.get("object").toString().equals("\"REFRESH\"")) {
							stringArray = serverConnection.getBookListJSONSerializer().getJSONStringArray();
							envelope.setObject("BOOK_LIST");
							envelope.setContent(stringArray);
							out.append(gson.toJson(envelope) + "\n");
							out.flush();
							
							stringArray = serverConnection.getImageListSerializer().getStringArray();
							envelope.setObject("IMAGE_LIST");
							envelope.setContent(stringArray);
							out.append(gson.toJson(envelope) + "\n");
							out.flush();
							imageSender.sendImagesToClient();
						}
					} catch (Failure statusReport) {
						Envelope<String> failureEnvelope = new Envelope<String>("FAILURE", statusReport.getMessage());
						out.append(gson.toJson(failureEnvelope) + "\n");
						out.flush();
						out.append(gson.toJson(statusReport) + "\n");
					}
				}
			}
			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}

	private boolean isUserIn(AccessCredentials accessCredentials, String[] arrayList) {
		for (String s : arrayList) {
			User u = gson.fromJson(s, User.class);
			if (accessCredentials.getUsername().equals(u.getUsername())
					&& accessCredentials.getPassword().equals(u.getPassword()))
				return true;
		}

		return false;
	}

	private String getUserFromCredentials(AccessCredentials accessCredentials, String[] arrayList) {
		for (String s : arrayList) {
			User u = gson.fromJson(s, User.class);
			if (accessCredentials.getUsername().equals(u.getUsername())
					&& accessCredentials.getPassword().equals(u.getPassword()))
				return s;
		}

		return null;
	}
}
