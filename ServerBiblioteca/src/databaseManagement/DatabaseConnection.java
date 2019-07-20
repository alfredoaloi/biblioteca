package databaseManagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import databaseSerialization.Book;
import databaseSerialization.User;
import statusReport.Failure;

public class DatabaseConnection {
	
	String url;
	Connection conn = null;
	
	public DatabaseConnection() throws SQLException {
		url = "jdbc:sqlite:BookDatabase.db";
		conn = DriverManager.getConnection(url);
	}
	
	public void close() throws SQLException{
		if(conn != null)
			conn.close();
	}
	
	public ResultSet getCategories() throws SQLException {
		String sql = "SELECT * FROM Category_Types;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}
	
	public ResultSet getBooksFromCategory(int category) throws SQLException {
		String sql = "SELECT * FROM Books NATURAL JOIN (SELECT ISBN, COUNT(*) AS Num_of_books FROM Books WHERE User_ID IS NULL GROUP BY ISBN) " +
					 "WHERE Category_ID = ? AND User_ID IS NULL GROUP BY ISBN;";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, category);
		ResultSet rs = pstmt.executeQuery();
		return rs;
	}
	
	public ResultSet getUsersNumBooksGroupedByStatus() throws SQLException
	{
		String sql = "SELECT A.User_ID, B.GREEN, C.YELLOW, D.RED, E.BLACK " + 
					 "FROM Books AS A " + 
					 "LEFT JOIN (SELECT User_ID, COUNT(*) AS GREEN FROM Books WHERE Deadline_status = 'GREEN' GROUP BY User_ID) AS B ON A.User_ID = B.User_ID " + 
					 "LEFT JOIN (SELECT User_ID, COUNT(*) AS YELLOW FROM Books WHERE Deadline_status = 'YELLOW' GROUP BY User_ID) AS C ON A.User_ID = C.User_ID " + 
					 "LEFT JOIN (SELECT User_ID, COUNT(*) AS RED FROM Books WHERE Deadline_status = 'RED' GROUP BY User_ID) AS D ON A.User_ID = D.User_ID " + 
					 "LEFT JOIN (SELECT User_ID, COUNT(*) AS BLACK FROM Books WHERE Deadline_status = 'BLACK' GROUP BY User_ID) AS E ON A.User_ID = E.User_ID " + 
					 "WHERE A.User_ID IS NOT NULL "+
					 "GROUP BY A.User_ID;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}
	
	public ResultSet getUserIDWithEmail() throws SQLException
	{
		String sql = "SELECT Username, E_mail FROM Customer_Account;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}
	
	public ResultSet getUserDeadlineStatus(String username) throws SQLException
	{
		String sql = "SELECT User_deadline_status FROM Customer_Account WHERE Username = ?;";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, username);
		ResultSet rs = pstmt.executeQuery();
		return rs;
	}
	
	public ResultSet getCustomersList() throws SQLException
	{
		String sql = "SELECT * FROM Customer_Account;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}
	
	public ResultSet getEmployeesList() throws SQLException
	{
		String sql = "SELECT * FROM Employee_Account;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}
	
	public void insertNewBook(Book book, int categoryID, String userID) throws SQLException, Failure {
		String insert = "INSERT INTO Books(Title, Author, Category_ID, Num_of_pages, Publisher, Language, Description, ISBN, Lending_period, Fine_increment) " + 
						"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		String log 	  = "INSERT INTO Log (User_ID, Book_ID, Date, Update_type) VALUES (?, ?, DATE('now'), 'INSERT');";
		
		PreparedStatement pstmtInsert = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
		PreparedStatement pstmtLog = conn.prepareStatement(log);
		
		conn.setAutoCommit(false);
		
		pstmtInsert.setString(1, book.getTitle());
		pstmtInsert.setString(2, book.getAuthor());
		pstmtInsert.setInt(3, categoryID);
		pstmtInsert.setInt(4, book.getnPages());
		pstmtInsert.setString(5, book.getPublisher());
		pstmtInsert.setString(6, book.getLanguage());
		pstmtInsert.setString(7, book.getDescription());
		pstmtInsert.setInt(8, book.getISBN());
		pstmtInsert.setInt(9, book.getLendingPeriod());
		pstmtInsert.setInt(10, book.getFineIncrement());
		pstmtInsert.executeUpdate();
		
		ResultSet rs = pstmtInsert.getGeneratedKeys();
		int bookID = 0;
		if(rs.next())
			bookID = rs.getInt(1);
		else {
			conn.rollback();
			conn.setAutoCommit(true);
			throw new Failure("Errore del database: Inserimento del libro non riuscito");
		}
		
		pstmtLog.setString(1, userID);
		pstmtLog.setInt(2, bookID);
		pstmtLog.executeUpdate();
		
		conn.commit();
		conn.setAutoCommit(true);
	}
	
	public void updateBook(int bookID, Book book, int categoryID, String userID) throws SQLException, Failure {
		String update = "UPDATE Books " + 
						"SET Title = ?, Author = ?, Category_ID = ?, Num_of_pages = ?, Publisher = ?," + 
						"Language = ?, Description = ?, ISBN = ?, Image = ?, Lending_period = ?, Fine_increment = ?" +
						"WHERE Book_ID = ?;";
		String log 	  = "INSERT INTO Log (User_ID, Book_ID, Date, Update_type) VALUES (?, ?, DATE('now'), 'UPDATE');";
		
		PreparedStatement pstmtUpdate = conn.prepareStatement(update);
		PreparedStatement pstmtLog = conn.prepareStatement(log);
		
		conn.setAutoCommit(false);
		
		pstmtUpdate.setString(1, book.getTitle());
		pstmtUpdate.setString(2, book.getAuthor());
		pstmtUpdate.setInt(3, categoryID);
		pstmtUpdate.setInt(4, book.getnPages());
		pstmtUpdate.setString(5, book.getPublisher());
		pstmtUpdate.setString(6, book.getLanguage());
		pstmtUpdate.setString(7, book.getDescription());
		pstmtUpdate.setInt(8, book.getISBN());
		pstmtUpdate.setString(9, book.getImage());
		pstmtUpdate.setInt(10, book.getLendingPeriod());
		pstmtUpdate.setInt(11, book.getFineIncrement());
		pstmtUpdate.setInt(12, bookID);
		int rowsAffected = pstmtUpdate.executeUpdate();
		
		if(rowsAffected != 1) {
			conn.rollback();
			conn.setAutoCommit(true);
			throw new Failure("Errore del database: Aggiornamento del libro non riuscito");
		}
		
		pstmtLog.setString(1, userID);
		pstmtLog.setInt(2, book.getBookID());
		pstmtLog.executeUpdate();
		
		conn.commit();
		conn.setAutoCommit(true);
	}
	
	public void deleteBook(int ISBN, String userID) throws SQLException, Failure {
		String delete = "DELETE FROM Books " + 
						"WHERE Book_ID = ? ;";
		
		String log 	  = "INSERT INTO Log (User_ID, Book_ID, Date, Update_type) VALUES (?, ?, DATE('now'), 'DELETE');";
		
		String idFirtsBook = "SELECT Book_ID " +
							 "FROM Books " +
							 "WHERE User_ID IS NULL AND ISBN = ?" + 
							 "GROUP BY ISBN;";
		
		PreparedStatement pstmtDelete = conn.prepareStatement(delete);
		PreparedStatement pstmtLog = conn.prepareStatement(log);
		PreparedStatement pstmtIDFirstBook = conn.prepareStatement(idFirtsBook);
		
		conn.setAutoCommit(false);
		
		pstmtIDFirstBook.setInt(1, ISBN);
		ResultSet idFirstBookResultSet = pstmtIDFirstBook.executeQuery();
		
		pstmtDelete.setInt(1, idFirstBookResultSet.getInt(1));
		int rowsAffected = pstmtDelete.executeUpdate();
		
		if(rowsAffected != 1) {
			conn.rollback();
			conn.setAutoCommit(true);
			throw new Failure("Errore del database: Eliminazone del libro non riuscita");
		}
		
		pstmtLog.setString(1, userID);
		pstmtLog.setInt(2, idFirstBookResultSet.getInt(1));
		pstmtLog.executeUpdate();
		
		conn.commit();
		conn.setAutoCommit(true);
	}
	
	public void insertNewCustomer(User customer, String userID) throws Failure {
		try {
			String insert = "INSERT INTO Customer_Account " + 
							"VALUES(?, ?, ?, ?, ?, 'GREEN', 0);";
			String log 	  = "INSERT INTO Log (User_ID, Customer_ID, Date, Update_type) VALUES (?, ?, DATE('now'), 'INSERT');";
			
			PreparedStatement pstmtInsert = conn.prepareStatement(insert);
			PreparedStatement pstmtLog = conn.prepareStatement(log);
			
			conn.setAutoCommit(false);
			
			pstmtInsert.setString(1, customer.getUsername());
			pstmtInsert.setString(2, customer.getPassword());
			pstmtInsert.setString(3, customer.getName());
			pstmtInsert.setString(4, customer.getSurname());
			pstmtInsert.setString(5, customer.getE_mail());
			int rowsAffected = pstmtInsert.executeUpdate();
			
			if(rowsAffected != 1) {
				conn.rollback();
				conn.setAutoCommit(true);
				throw new Failure("Errore del database: Inserimento dell'utente non riuscito");
			}
			
			pstmtLog.setString(1, userID);
			pstmtLog.setString(2, customer.getUsername());
			pstmtLog.executeUpdate();
			
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			throw new Failure("Un'altro utente con lo stesso username e/o password è già presente nel database");
		}
	}
	
	public void updateCustomer(User customer, String userID) throws SQLException, Failure {
		String update = "UPDATE Customer_Account " +
						"SET Username = ?, Password = ?, Name = ?, Surname = ?, E_Mail = ? " +
						"WHERE Username = ?;";
		String log 	  = "INSERT INTO Log (User_ID, Customer_ID, Date, Update_type) VALUES (?, ?, DATE('now'), 'UPDATE');";
		
		PreparedStatement pstmtUpdate = conn.prepareStatement(update);
		PreparedStatement pstmtLog = conn.prepareStatement(log);
		
		conn.setAutoCommit(false);
		
		pstmtUpdate.setString(1, customer.getUsername());
		pstmtUpdate.setString(2, customer.getPassword());
		pstmtUpdate.setString(3, customer.getName());
		pstmtUpdate.setString(4, customer.getSurname());
		pstmtUpdate.setString(5, customer.getE_mail());
		pstmtUpdate.setString(6, customer.getUsername());
		int rowsAffected = pstmtUpdate.executeUpdate();
		
		if(rowsAffected != 1) {
			conn.rollback();
			conn.setAutoCommit(true);
			throw new Failure("Errore del database: Aggiornamento dell'utente non riuscito");
		}
		
		pstmtLog.setString(1, userID);
		pstmtLog.setString(2, customer.getUsername());
		pstmtLog.executeUpdate();
		
		conn.commit();
		conn.setAutoCommit(true);
	}
	
	public void deleteCustomer(String customerID, String userID) throws SQLException, Failure {
		String delete = "DELETE FROM Customer_Account " + 
						"WHERE Username = ?;";
		String log 	  = "INSERT INTO Log (User_ID, Customer_ID, Date, Update_type) VALUES (?, ?, DATE('now'), 'DELETE');";
		
		PreparedStatement pstmtDelete = conn.prepareStatement(delete);
		PreparedStatement pstmtLog = conn.prepareStatement(log);
		
		conn.setAutoCommit(false);
		
		pstmtDelete.setString(1, customerID);
		int rowsAffected = pstmtDelete.executeUpdate();
		
		if(rowsAffected != 1) {
			conn.rollback();
			conn.setAutoCommit(true);
			throw new Failure("Errore del database: Eliminazione dell'utente non riuscita");
		}
		
		pstmtLog.setString(1, userID);
		pstmtLog.setString(2, customerID);
		pstmtLog.executeUpdate();
		
		conn.commit();
		conn.setAutoCommit(true);
	}
	
	public void insertNewEmployee(User employee, String userID) throws Failure {
		try {
			String insert = "INSERT INTO Customer_Account " + 
							"VALUES(?, ?, ?, ?, ?);";
			String log 	  = "INSERT INTO Log (User_ID, Customer_ID, Date, Update_type) VALUES (?, ?, DATE('now'), 'INSERT');";
			
			PreparedStatement pstmtInsert = conn.prepareStatement(insert);
			PreparedStatement pstmtLog = conn.prepareStatement(log);
			
			conn.setAutoCommit(false);
			
			pstmtInsert.setString(1, employee.getUsername());
			pstmtInsert.setString(2, employee.getPassword());
			pstmtInsert.setString(3, employee.getName());
			pstmtInsert.setString(4, employee.getSurname());
			pstmtInsert.setString(5, employee.getE_mail());
			int rowsAffected = pstmtInsert.executeUpdate();
			
			if(rowsAffected != 1) {
				conn.rollback();
				conn.setAutoCommit(true);
				throw new Failure("Errore del database: Inserimento dell'utente non riuscito");
			}
			
			pstmtLog.setString(1, userID);
			pstmtLog.setString(2, employee.getUsername());
			pstmtLog.executeUpdate();
			
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			throw new Failure("Un'altro utente con lo stesso username e/o password è già presente nel database");
		}
	}
	
	public void updateEmployee(User employee, String userID) throws SQLException, Failure {
		String update = "UPDATE Customer_Account " +
						"SET Username = ?, Password = ?, Name = ?, Surname = ?, E_Mail = ? " +
						"WHERE Username = ?;";
		String log 	  = "INSERT INTO Log (User_ID, Customer_ID, Date, Update_type) VALUES (?, ?, DATE('now'), 'UPDATE');";
		
		PreparedStatement pstmtUpdate = conn.prepareStatement(update);
		PreparedStatement pstmtLog = conn.prepareStatement(log);
		
		conn.setAutoCommit(false);
		
		pstmtUpdate.setString(1, employee.getUsername());
		pstmtUpdate.setString(2, employee.getPassword());
		pstmtUpdate.setString(3, employee.getName());
		pstmtUpdate.setString(4, employee.getSurname());
		pstmtUpdate.setString(5, employee.getE_mail());
		pstmtUpdate.setString(6, employee.getUsername());
		int rowsAffected = pstmtUpdate.executeUpdate();
		
		if(rowsAffected != 1) {
			conn.rollback();
			conn.setAutoCommit(true);
			throw new Failure("Errore del database: Aggiornamento dell'utente non riuscito");
		}
		
		pstmtLog.setString(1, userID);
		pstmtLog.setString(2, employee.getUsername());
		pstmtLog.executeUpdate();
		
		conn.commit();
		conn.setAutoCommit(true);
	}
	
	public void deleteEmployee(String employeeID, String userID) throws SQLException, Failure {
		String delete = "DELETE FROM Customer_Account " + 
						"WHERE Username = ?;";
		String log 	  = "INSERT INTO Log (User_ID, Customer_ID, Date, Update_type) VALUES (?, ?, DATE('now'), 'DELETE');";
		
		PreparedStatement pstmtDelete = conn.prepareStatement(delete);
		PreparedStatement pstmtLog = conn.prepareStatement(log);
		
		conn.setAutoCommit(false);
		
		pstmtDelete.setString(1, employeeID);
		int rowsAffected = pstmtDelete.executeUpdate();
		
		if(rowsAffected != 1) {
			conn.rollback();
			conn.setAutoCommit(true);
			throw new Failure("Errore del database: Eliminazione dell'utente non riuscita");
		}
		
		pstmtLog.setString(1, userID);
		pstmtLog.setString(2, employeeID);
		pstmtLog.executeUpdate();
		
		conn.commit();
		conn.setAutoCommit(true);
	}
	
	public ResultSet getUserLentBooksList(String username) throws SQLException
	{
		String sql = "SELECT * FROM Books WHERE User_ID = ?;";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, username);
		ResultSet rs = pstmt.executeQuery();
		return rs;
	}
	
	public ResultSet getCategoryIDFromName(String categoryName) throws SQLException
	{
		String sql = "SELECT Category_ID FROM Category_Types WHERE Category = ?;";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, categoryName);
		ResultSet rs = pstmt.executeQuery();
		return rs;
	}
	
	public void updateCustomerLentBook(int ISBN, String username) throws SQLException, Failure {
		String idFirtsBook = "SELECT Book_ID " +
							 "FROM Books " +
							 "WHERE User_ID IS NULL AND ISBN = ?" + 
							 "GROUP BY ISBN;";
		
		String userLentBook = "UPDATE Books " +
							  "SET User_ID = ?, Deadline_status = 'GREEN', Deadline_date = ? " +
							  "WHERE Book_ID = ?;";
		
		PreparedStatement pstmtIDFirstBook = conn.prepareStatement(idFirtsBook);
		PreparedStatement pstmtUserLentBook = conn.prepareStatement(userLentBook);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, getLendingPeriodFromISBN(ISBN).getInt("Lending_period"));
		
		conn.setAutoCommit(false);
		pstmtIDFirstBook.setInt(1, ISBN);
		ResultSet idFirstBookResultSet = pstmtIDFirstBook.executeQuery();
		
		int temp = idFirstBookResultSet.getInt(1);
		
		pstmtUserLentBook.setString(1, username);
		pstmtUserLentBook.setString(2, new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
		pstmtUserLentBook.setInt(3, temp);
		int rowsAffected = pstmtUserLentBook.executeUpdate();
		
		if(rowsAffected != 1) {
			conn.rollback();
			conn.setAutoCommit(true);
			throw new Failure("Errore del database: Aggiornamento della lista di libri prestati all'utente non riuscito");
		}
		
		conn.commit();
		conn.setAutoCommit(true);
	}
	
	public void updateCustomerReturnedBook(int bookID) throws SQLException, Failure {
		String userReturnedBook = "UPDATE Books " +
				  				  "SET User_ID = NULL, Deadline_status = NULL, Deadline_date = NULL, Fine = NULL " +
				  				  "WHERE Book_ID = ?;";
		PreparedStatement pstmtUserReturnedBook = conn.prepareStatement(userReturnedBook);
		
		conn.setAutoCommit(false);
		pstmtUserReturnedBook.setInt(1, bookID);
		int rowsAffected = pstmtUserReturnedBook.executeUpdate();
		
		if(rowsAffected != 1) {
			conn.rollback();
			conn.setAutoCommit(true);
			throw new Failure("Errore del database: Aggiornamento della lista di libri riportati dall'utente non riuscito");
		}
		
		conn.commit();
		conn.setAutoCommit(true);
	}
	
	public ResultSet getPastExpiredBooksFromCustomer(String customer) throws SQLException {
		String customerPastExpiredBooks = "SELECT Expired_books FROM Customer_Expired_Books_Vault WHERE Username = ?;";
		PreparedStatement pstmtCustomerPastExpiredBooks = conn.prepareStatement(customerPastExpiredBooks);
		
		pstmtCustomerPastExpiredBooks.setString(1, customer);
		ResultSet rs = pstmtCustomerPastExpiredBooks.executeQuery();
		return rs;
	}
	
	public void setNewPastExpiredBooksForCustomer(String customer, int expiredBooks) throws SQLException {
		conn.setAutoCommit(false);
		String customerPastExpiredBooks = "INSERT INTO Customer_Expired_Books_Vault VALUES(?, ?)";
		PreparedStatement pstmtCustomerPastExpiredBooks = conn.prepareStatement(customerPastExpiredBooks);
		
		pstmtCustomerPastExpiredBooks.setString(1, customer);
		pstmtCustomerPastExpiredBooks.setInt(2, expiredBooks);
		pstmtCustomerPastExpiredBooks.executeUpdate();
		conn.commit();
		conn.setAutoCommit(true);
	}
	
	public void updatePastExpiredBooksForCustomer(String customer, int expiredBooks) throws SQLException {
		conn.setAutoCommit(false);
		String customerPastExpiredBooks = "UPDATE Customer_Expired_Books_Vault SET Expired_books = ? WHERE Username = ?;";
		PreparedStatement pstmtCustomerPastExpiredBooks = conn.prepareStatement(customerPastExpiredBooks);
		
		pstmtCustomerPastExpiredBooks.setInt(1, expiredBooks);
		pstmtCustomerPastExpiredBooks.setString(2, customer);
		pstmtCustomerPastExpiredBooks.executeUpdate();
		conn.commit();
		conn.setAutoCommit(true);
	}
	
	public int getCurrentDate() throws SQLException {
		String currentDate = "SELECT DATE('now');";
		Statement stmtCurrentDate = conn.createStatement();
		ResultSet rs = stmtCurrentDate.executeQuery(currentDate);
		
		return rs.getInt(1);
	}

	public void setCustomerStatus(String customer, String newStatus) throws SQLException {
		conn.setAutoCommit(false);
		String customerNewStatus = "UPDATE Customer_Account SET User_deadline_status = ? WHERE Username = ?;";
		PreparedStatement pstmtCustomerNewStatus = conn.prepareStatement(customerNewStatus);
		
		pstmtCustomerNewStatus.setString(1, newStatus);
		pstmtCustomerNewStatus.setString(2, customer);
		pstmtCustomerNewStatus.executeUpdate();
		conn.commit();
		conn.setAutoCommit(true);
	}
	
	public void setBookStatus(int bookID, String newStatus) throws SQLException {
		conn.setAutoCommit(false);
		String bookNewStatus = "UPDATE Books SET Deadline_status = ? WHERE Book_ID = ?;";
		PreparedStatement pstmtBookNewStatus = conn.prepareStatement(bookNewStatus);
		
		pstmtBookNewStatus.setString(1, newStatus);
		pstmtBookNewStatus.setInt(2, bookID);
		pstmtBookNewStatus.executeUpdate();
		conn.commit();
		conn.setAutoCommit(true);
	}
	
	public ResultSet getMailInformationFromCustomer(String customer) throws SQLException {
		String customerMailInformation = "SELECT Title, Deadline_date, (JULIANDAY(Deadline_date) - JULIANDAY(DATE('now'))) AS Remaining_days, Fine " + 
										 "FROM Books " + 
										 "WHERE User_ID = ?;";
		PreparedStatement pstmtCustomerMailInformation = conn.prepareStatement(customerMailInformation);
		
		pstmtCustomerMailInformation.setString(1, customer);
		ResultSet rs = pstmtCustomerMailInformation.executeQuery();
		return rs;
	}
	
	public ResultSet getBooksInformation() throws SQLException {
		String booksInformation = "SELECT Book_ID, Lending_period, Deadline_status, (JULIANDAY(Deadline_date) - JULIANDAY(DATE('now'))) AS Remaining_days, Fine, Fine_increment " + 
								  "FROM Books " + 
								  "WHERE User_ID IS NOT NULL;";
		Statement stmtBooksInformation = conn.createStatement();
		ResultSet rs = stmtBooksInformation.executeQuery(booksInformation);
		return rs;
	}
	
	public void setBookFine(int bookID, int fine) throws SQLException {
		conn.setAutoCommit(false);
		String bookFine = "UPDATE Books SET Fine = ? WHERE Book_ID = ?;";
		PreparedStatement pstmtFine = conn.prepareStatement(bookFine);
		
		pstmtFine.setInt(1, fine);
		pstmtFine.setInt(2, bookID);
		pstmtFine.executeUpdate();
		conn.commit();
		conn.setAutoCommit(true);
	}
	
	public ResultSet getNextScheduledDeadlineCheck() throws SQLException {
		String nextCheck = "SELECT DATE(Scheduled_date) FROM Next_Scheduled_Deadline_Check;";
		Statement stmtNextCheck = conn.createStatement();
		
		ResultSet rs = stmtNextCheck.executeQuery(nextCheck);
		return rs;
	}
	
	public void setNextScheduledDeadlineCheck(Date nextSchedule) throws SQLException {
		conn.setAutoCommit(false);
		String nextCheck = "UPDATE Next_Scheduled_Deadline_Check SET Scheduled_date = ?;";
		PreparedStatement pstmtCheck = conn.prepareStatement(nextCheck);
		
		pstmtCheck.setString(1, new SimpleDateFormat("yyyy-MM-dd").format(nextSchedule));
		pstmtCheck.executeUpdate();
		
		conn.commit();
		conn.setAutoCommit(true);
	}
	
	public ResultSet getBooksImages() throws SQLException {
		String booksImages = "SELECT DISTINCT Image FROM Books;";
		Statement stmtNextCheck = conn.createStatement();
		
		ResultSet rs = stmtNextCheck.executeQuery(booksImages);
		return rs;
	}
	
	public ResultSet getBooksIDFromISBN(int ISBN) throws SQLException {
		String customerMailInformation = "SELECT Book_ID " + 
										 "FROM Books " + 
										 "WHERE ISBN = ?;";
		PreparedStatement pstmtCustomerMailInformation = conn.prepareStatement(customerMailInformation);
		
		pstmtCustomerMailInformation.setInt(1, ISBN);
		ResultSet rs = pstmtCustomerMailInformation.executeQuery();
		return rs;
	}
	
	public ResultSet getLendingPeriodFromISBN(int ISBN) throws SQLException {
		String customerMailInformation = "SELECT DISTINCT Lending_period " + 
										 "FROM Books " + 
										 "WHERE ISBN = ?;";
		PreparedStatement pstmtCustomerMailInformation = conn.prepareStatement(customerMailInformation);
		
		pstmtCustomerMailInformation.setInt(1, ISBN);
		ResultSet rs = pstmtCustomerMailInformation.executeQuery();
		return rs;
	}
	
	public void setNewCategory(String category) throws SQLException {
		String newCategory = "INSERT INTO Category_types(Category) " + 
							 "VALUES(?);";
		PreparedStatement pstmtNewCategory = conn.prepareStatement(newCategory);

		pstmtNewCategory.setString(1, category);
		pstmtNewCategory.executeUpdate();
	}
}
