package databaseSerialization;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import databaseManagement.DatabaseConnection;

public class ImageListSerializer {
	
	protected ArrayList<String> stringArray;
	
	public ImageListSerializer(DatabaseConnection databaseConnection) throws SQLException {
		stringArray = new ArrayList<String>();		
		ResultSet rs = databaseConnection.getBooksImages();
		
		while(rs.next()) {
			stringArray.add(rs.getString("Image"));
		}
		
	}
	
	public String[] getStringArray() {
		return stringArray.toArray(new String[stringArray.size()]);
	}
	
}
