package application;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import databaseManagement.DatabaseConnection;
import databaseSerialization.AccessCredentials;
import databaseSerialization.Book;
import databaseSerialization.BookDeadlineDateIDContainer;
import databaseSerialization.Customer;
import databaseSerialization.User;
import databaseSerialization.UserLentBooksJSONSerializer;
import envelopeManagement.Envelope;
import imageSender.ImageSender;
import statusReport.Failure;
import statusReport.Success;

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
							ArrayList<String> userList = serverConnection.getUserListJSONSerializer().getJSONCustomerArray();
							ArrayList<String> employeeList = serverConnection.getUserListJSONSerializer().getJSONEmployeeArray();
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
							throw new Success();
						}
						else if(envelope.getObject().equals("INSERT_NEW_CUSTOMER")) {
							User User = gson.fromJson(in.readLine(), User.class);
							DatabaseConnection db = serverConnection.getDatabaseConnection();
							db.insertNewCustomer(User, employeeUsername);
							serverConnection.refreshUserList();
							throw new Success();
						}
						else if(envelope.getObject().equals("UPDATE_CUSTOMER")) {
							User User = gson.fromJson(in.readLine(), User.class);
							DatabaseConnection db = serverConnection.getDatabaseConnection();
							db.updateCustomer(User, employeeUsername);
							serverConnection.refreshUserList();
							throw new Success();
						}
						else if(envelope.getObject().equals("DELETE_CUSTOMER")) {
							String user = in.readLine();
							DatabaseConnection db = serverConnection.getDatabaseConnection();
							db.deleteCustomer(user, employeeUsername);
							serverConnection.refreshUserList();
							throw new Success();
						}
						else if(envelope.getObject().equals("INSERT_NEW_BOOK")) {
							Book book = gson.fromJson(in.readLine(), Book.class);
							DatabaseConnection db = serverConnection.getDatabaseConnection();
							ResultSet categoryIDResultSet = db.getCategoryIDFromName(in.readLine());
							db.insertNewBook(book, categoryIDResultSet.getInt(1), employeeUsername);
							serverConnection.refreshBookList();
							throw new Success();
						}
						else if(envelope.getObject().equals("UPDATE_BOOK")) {
							Book book = gson.fromJson(in.readLine(), Book.class);
							DatabaseConnection db = serverConnection.getDatabaseConnection();
							ResultSet categoryIDResultSet = db.getCategoryIDFromName(in.readLine());
							ResultSet booksIDISBNResultSet = db.getBooksIDFromISBN(book.getISBN());
							while(booksIDISBNResultSet.next()) {
								db.updateBook(booksIDISBNResultSet.getInt(1), book, categoryIDResultSet.getInt(1), employeeUsername);
							}
							serverConnection.refreshBookList();
							throw new Success();
						}
						else if(envelope.getObject().equals("DELETE_BOOK")) {
							Book book = gson.fromJson(in.readLine(), Book.class);
							DatabaseConnection db = serverConnection.getDatabaseConnection();
							db.deleteBook(book.getISBN(), employeeUsername);
							serverConnection.refreshBookList();
							throw new Success();
						}
						else if(envelope.getObject().equals("GET_CUSTOMER_LIST")) {
							out.append("CUSTOMER_LIST" + "\n");
							out.flush();
							ArrayList<String> userList = serverConnection.getUserListJSONSerializer().getJSONCustomerArray();
							for(String s1 : userList) {
								out.append(s1 + "\n");
								out.flush();
							}
							out.append("ENDOFSTREAM" + "\n");
							out.flush();
						}
						else if(envelope.getObject().equals("GET_CUSTOMER_LENT_BOOKS")) {
							String username = in.readLine();
							UserLentBooksJSONSerializer lentBookSerializer = new UserLentBooksJSONSerializer(
																				serverConnection.getDatabaseConnection(),
																				username);
							out.append("LENT_BOOKS_LIST" + "\n");
							out.flush();
							String[] lentBooksList = lentBookSerializer.getJSONStringArray();
							for(String s1 : lentBooksList) {
								out.append(s1 + "\n");
								out.flush();
							}
							out.append("ENDOFSTREAM" + "\n");
							out.flush();
						}
						//QUARANTENA
						else if(envelope.getObject().equals("SET_CUSTOMER_LENT_BOOKS")) {
							String username = in.readLine();
							ArrayList<BookDeadlineDateIDContainer> tmp = new ArrayList<BookDeadlineDateIDContainer>();
							DatabaseConnection db = serverConnection.getDatabaseConnection();
							
							String input2 = in.readLine();
							while(!input2.contains("ENDOFSTREAM"))
							{
								tmp.add(gson.fromJson(input, BookDeadlineDateIDContainer.class));
								input2 = in.readLine();
							}
							
							for(BookDeadlineDateIDContainer dateID : tmp)
								db.updateCustomerLentBook(dateID.getBookID(), username, dateID.getDeadlineDate());
							throw new Success();
						}
						//
						else if(envelope.getObject().equals("REFRESH")) {
							stringArray = serverConnection.getBookListJSONSerializer().getJSONStringArray();
							for(String string : stringArray) {
								out.append(string + "\n");
								out.flush();
							}
							out.append("ENDOFSTREAM" + "\n");
							out.flush();
						}
					} catch (Failure statusReport) {
						Envelope<String> failureEnvelope = new Envelope<String>("FAILURE", statusReport.getMessage());
						out.append(gson.toJson(failureEnvelope) + "\n");
						out.flush();
						out.append(gson.toJson(statusReport) + "\n");
					} catch (Success e) {
						Envelope<String> succesEnvelope = new Envelope<String>("SUCCESS", null);
						out.append(gson.toJson(succesEnvelope) + "\n");
						out.flush();
					}
				}
			}
			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}

	private boolean isUserIn(AccessCredentials accessCredentials, ArrayList<String> arrayList) {
		for (String s : arrayList) {
			User u = gson.fromJson(s, User.class);
			if (accessCredentials.getUsername().equals(u.getUsername())
					&& accessCredentials.getPassword().equals(u.getPassword()))
				return true;
		}

		return false;
	}

	private String getUserFromCredentials(AccessCredentials accessCredentials, ArrayList<String> arrayList) {
		for (String s : arrayList) {
			User u = gson.fromJson(s, User.class);
			if (accessCredentials.getUsername().equals(u.getUsername())
					&& accessCredentials.getPassword().equals(u.getPassword()))
				return s;
		}

		return null;
	}
}
