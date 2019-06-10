package databaseManagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.spi.DirStateFactory.Result;

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
		String sql = "SELECT * FROM Books NATURAL JOIN (SELECT ISBN, COUNT(*) AS Num_of_books FROM Books GROUP BY ISBN) WHERE Category_ID = ? GROUP BY ISBN;";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, category);
		ResultSet rs = pstmt.executeQuery();
		return rs;
	}
	
	public ResultSet getUsersNumBooksGroupedByStatus() throws SQLException
	{
		String sql = "SELECT User_ID, Num_green_books, Num_yellow_books, Num_red_books, Num_black_books\r\n" + 
					 "FROM Books" + 
					 "NATURAL JOIN (SELECT User_ID, COUNT(*) AS Num_green_books FROM Books WHERE Deadline_status = 'GREEN' GROUP BY Deadline_status)" + 
					 "NATURAL JOIN (SELECT User_ID, COUNT(*) AS Num_yellow_books FROM Books WHERE Deadline_status = 'YELLOW' GROUP BY Deadline_status)" + 
					 "NATURAL JOIN (SELECT User_ID, COUNT(*) AS Num_red_books FROM Books WHERE Deadline_status = 'RED' GROUP BY Deadline_status)" + 
					 "NATURAL JOIN (SELECT User_ID, COUNT(*) AS Num_black_books FROM Books WHERE Deadline_status = 'BLACK' GROUP BY Deadline_status)" + 
					 "GROUP BY User_ID;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}
	
	public ResultSet getUserIDWithEmail() throws SQLException
	{
		String sql = "SELECT Username, E_mail FROM User_Account;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}
	
	public ResultSet getUserDeadlineStatus(String username) throws SQLException
	{
		String sql = "SELECT User_deadline_status FROM User_Account WHERE Username = ?;";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, username);
		ResultSet rs = pstmt.executeQuery();
		return rs;
	}
}
