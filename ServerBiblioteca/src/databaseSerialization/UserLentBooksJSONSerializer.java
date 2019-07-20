package databaseSerialization;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import databaseManagement.DatabaseConnection;

public class UserLentBooksJSONSerializer extends BookListJSONSerializer {

	public UserLentBooksJSONSerializer(DatabaseConnection databaseConnection, String username) throws SQLException {
		super();
		ResultSet booksResultSet = databaseConnection.getUserLentBooksList(username);
		ArrayList<Book> books = new ArrayList<Book>();
		while(booksResultSet.next()) 
			books.add(new LentBook(booksResultSet.getInt("Book_ID"),
					booksResultSet.getString("Title"),
					booksResultSet.getString("Author"),
					booksResultSet.getInt("Num_of_pages"),
					booksResultSet.getString("Publisher"),
					booksResultSet.getString("Language"),
					booksResultSet.getString("Description"),
					booksResultSet.getInt("ISBN"),
					0,
					booksResultSet.getString("Image"),
					booksResultSet.getInt("Lending_period"),
					booksResultSet.getInt("Fine_increment"),
					booksResultSet.getString("User_ID"),
					booksResultSet.getString("Deadline_date"),
					booksResultSet.getDouble("Fine")));
		for(Book book : books)
			jsonStringArray.add(gson.toJson(book));
	}

}
