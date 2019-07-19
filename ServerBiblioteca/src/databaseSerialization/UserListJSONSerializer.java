package databaseSerialization;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.google.gson.Gson;

import databaseManagement.DatabaseConnection;

public class UserListJSONSerializer {
	
	private Gson gson;
	private ArrayList<String> jsonCustomerArray;
	private ArrayList<String> jsonEmployeeArray;

	public UserListJSONSerializer(DatabaseConnection databaseConnection) throws SQLException {
		gson = new Gson();
		jsonCustomerArray = new ArrayList<String>();
		jsonEmployeeArray = new ArrayList<String>();
		ResultSet customerResultSet = databaseConnection.getCustomersList();
		ResultSet employeeResultSet = databaseConnection.getEmployeesList();
		
		while(customerResultSet.next())
		{
			Customer c = new Customer(customerResultSet.getString("Username"), 
									  customerResultSet.getString("Password"), 
									  customerResultSet.getString("Name"), 
									  customerResultSet.getString("Surname"), 
									  customerResultSet.getString("E_Mail"), 
									  customerResultSet.getString("User_deadline_status"));
			jsonCustomerArray.add(gson.toJson(c));
		}
		
		while(employeeResultSet.next())
		{
			User e = new User(employeeResultSet.getString("Username"), 
							  employeeResultSet.getString("Password"), 
							  employeeResultSet.getString("Name"), 
							  employeeResultSet.getString("Surname"), 
							  employeeResultSet.getString("E_Mail"));
			jsonEmployeeArray.add(gson.toJson(e));
		}		
	}

	public String[] getJSONCustomerArray() {
		return jsonCustomerArray.toArray(new String[jsonCustomerArray.size()]);
	}

	public String[] getJSONEmployeeArray() {
		return jsonEmployeeArray.toArray(new String[jsonEmployeeArray.size()]);
	}
}
