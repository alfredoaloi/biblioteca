package application;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.google.gson.Gson;

import databaseManagement.DatabaseConnection;
import databaseSerialization.AccessCredentials;
import databaseSerialization.Book;
import databaseSerialization.BookDeadlineDateIDContainer;
import databaseSerialization.User;
import databaseSerialization.UserLentBooksJSONSerializer;
import imageSender.ImageSender;

public class ClientManger implements Runnable {

	private Socket socket;
	private ServerSocket tmp;
	private ServerConnection serverConnection;
	private Gson gson;
	private String employeeUsername;
	private ImageSender imageSender;

	public ClientManger(Socket socket, ServerConnection serverConnection) throws IOException {
		this.socket = socket;
		this.tmp = new ServerSocket(8001);
		this.serverConnection = serverConnection;
		this.gson = new Gson();
		Socket s = tmp.accept();
		this.imageSender = new ImageSender("res", s, serverConnection.getImageListSerializer().getStringArray());
	}

	@Override
	public void run() {
		try {
			PrintWriter out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			ArrayList<String> stringArray = serverConnection.getBookListJSONSerializer().getJSONStringArray();
			for(String string : stringArray) {
				out.append(string + "\n");
				out.flush();
			}
			out.append("ENDOFSTREAM 1" + "\n");
			out.flush();
			
			for(String string : serverConnection.getImageListSerializer().getStringArray()) {
				out.append(string + "\n");
				out.flush();
			}
			out.append("ENDOFSTREAM" + "\n");
			out.flush();
			
			imageSender.sendImagesToClient();
			tmp.close();
			
			while(true) {
				if(in.ready()) {
					String s = in.readLine();
					if(s.contains("ACCESS_CREDENTIALS")) {
						AccessCredentials ac = gson.fromJson(in.readLine(), AccessCredentials.class);
						ArrayList<String> userList = serverConnection.getUserListJSONSerializer().getJSONCustomerArray();
						
						if(isUserIn(ac, userList))
							out.append("CUSTOMER" + "\n");
						else {
							out.append("EMPLOYEE" + "\n");
							employeeUsername = ac.getUsername();
						}
						
						out.append(getUserFromCredentials(ac, userList) + "\n");
						out.append("ENDOFSTREAM" + "\n");
						out.flush();
					}
					else if(s.contains("INSERT_NEW_CUSTOMER")) {
						User User = gson.fromJson(in.readLine(), User.class);
						DatabaseConnection db = serverConnection.getDatabaseConnection();
						db.insertNewCustomer(User, employeeUsername);
						serverConnection.refreshUserList();
					}
					else if(s.contains("UPDATE_CUSTOMER")) {
						User User = gson.fromJson(in.readLine(), User.class);
						DatabaseConnection db = serverConnection.getDatabaseConnection();
						db.updateCustomer(User, employeeUsername);
						serverConnection.refreshUserList();
					}
					else if(s.contains("DELETE_CUSTOMER")) {
						String user = in.readLine();
						DatabaseConnection db = serverConnection.getDatabaseConnection();
						db.deleteCustomer(user, employeeUsername);
						serverConnection.refreshUserList();
					}
					else if(s.contains("INSERT_NEW_BOOK")) {
						Book book = gson.fromJson(in.readLine(), Book.class);
						DatabaseConnection db = serverConnection.getDatabaseConnection();
						ResultSet categoryIDRS = db.getCategoryIDFromName(in.readLine());
						db.insertNewBook(book, categoryIDRS.getInt(1), employeeUsername);
						serverConnection.refreshBookList();
					}
					else if(s.contains("UPDATE_BOOK")) {
						Book book = gson.fromJson(in.readLine(), Book.class);
						DatabaseConnection db = serverConnection.getDatabaseConnection();
						ResultSet categoryIDResultSet = db.getCategoryIDFromName(in.readLine());
						ResultSet booksIDISBNResultSet = db.getBooksIDFromISBN(book.getISBN());
						while(booksIDISBNResultSet.next()) {
							db.updateBook(booksIDISBNResultSet.getInt(1), book, categoryIDResultSet.getInt(1), employeeUsername);
						}
						serverConnection.refreshBookList();
					}
					else if(s.contains("DELETE_BOOK")) {
						Book book = gson.fromJson(in.readLine(), Book.class);
						DatabaseConnection db = serverConnection.getDatabaseConnection();
						db.deleteBook(book.getISBN(), employeeUsername);
						serverConnection.refreshBookList();
					}
					else if(s.contains("GET_CUSTOMER_LIST")) {
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
					else if(s.contains("GET_CUSTOMER_LENT_BOOKS")) {
						String username = in.readLine();
						UserLentBooksJSONSerializer lentBookSerializer = new UserLentBooksJSONSerializer(
																			serverConnection.getDatabaseConnection(),
																			username);
						out.append("LENT_BOOKS_LIST" + "\n");
						out.flush();
						ArrayList<String> lentBooksList = lentBookSerializer.getJSONStringArray();
						for(String s1 : lentBooksList) {
							out.append(s1 + "\n");
							out.flush();
						}
						out.append("ENDOFSTREAM" + "\n");
						out.flush();
					}
					else if(s.contains("SET_CUSTOMER_LENT_BOOKS")) {
						String username = in.readLine();
						ArrayList<BookDeadlineDateIDContainer> tmp = new ArrayList<BookDeadlineDateIDContainer>();
						DatabaseConnection db = serverConnection.getDatabaseConnection();
						
						String input = in.readLine();
						while(!input.contains("ENDOFSTREAM"))
						{
							tmp.add(gson.fromJson(input, BookDeadlineDateIDContainer.class));
							input = in.readLine();
						}
						
						for(BookDeadlineDateIDContainer dateID : tmp)
							db.updateCustomerLentBook(dateID.getBookID(), username, dateID.getDeadlineDate());					
					}
					else if(s.contains("REFRESH")) {
						stringArray = serverConnection.getBookListJSONSerializer().getJSONStringArray();
						for(String string : stringArray) {
							out.append(string + "\n");
							out.flush();
						}
						out.append("ENDOFSTREAM" + "\n");
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
