package databaseSerialization;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import databaseManagement.DatabaseConnection;

public class ImageListSerializer {
	
	protected ArrayList<String> StringArray;
	
	public ImageListSerializer(DatabaseConnection databaseConnection) throws SQLException {
		StringArray = new ArrayList<String>();		
		ResultSet rs = databaseConnection.getBooksImages();
		
		while(rs.next()) {
			StringArray.add(rs.getString("Image"));
		}
		
	}
	
	public ArrayList<String> getStringArray() {
		return StringArray;
	}
	
}
