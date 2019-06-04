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
}
