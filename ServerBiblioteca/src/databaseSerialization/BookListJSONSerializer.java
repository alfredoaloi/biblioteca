package databaseSerialization;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.google.gson.Gson;

import databaseManagement.DatabaseConnection;

public class BookListJSONSerializer {

	protected Gson gson;
	protected ArrayList<String> jsonStringArray;

	public BookListJSONSerializer(DatabaseConnection databaseConnection) throws SQLException {
		gson = new Gson();
		jsonStringArray = new ArrayList<String>();
		
		ResultSet categoriesResultSet = databaseConnection.getCategories();
		while (categoriesResultSet.next()) {
			Category c = new Category(categoriesResultSet.getString("Category"));
			ResultSet booksResultSet = databaseConnection
					.getBooksFromCategory(categoriesResultSet.getInt("Category_ID"));
			ArrayList<Book> books = new ArrayList<Book>();
			while(booksResultSet.next())
				books.add(new Book(booksResultSet.getInt("Book_ID"),
						booksResultSet.getString("Title"),
						booksResultSet.getString("Author"),
						booksResultSet.getInt("Num_of_pages"),
						booksResultSet.getString("Publisher"),
						booksResultSet.getString("Language"),
						booksResultSet.getString("Description"),
						booksResultSet.getInt("ISBN"),
						booksResultSet.getInt("Num_of_books"),
						booksResultSet.getString("Image"),
						booksResultSet.getInt("Lending_period"),
						booksResultSet.getInt("Fine_increment")));
			c.setBooks(books.toArray(new Book[books.size()]));
			jsonStringArray.add(gson.toJson(c));
		}
	}

	public BookListJSONSerializer() {
		gson = new Gson();
		jsonStringArray = new ArrayList<String>();
	}

	public String[] getJSONStringArray() {
		return jsonStringArray.toArray(new String[jsonStringArray.size()]);
	}
}
