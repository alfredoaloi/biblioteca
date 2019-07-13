package notificationManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import databaseManagement.DatabaseConnection;

public class MailTemplateBuilder {

	private DatabaseConnection databaseConnection;

	public MailTemplateBuilder(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	public String buildMailForCustomer(String customer) throws SQLException, IOException {
		ResultSet customerInformationResultSet = databaseConnection.getMailInformationFromCustomer(customer);
		BufferedReader in = new BufferedReader(
				new InputStreamReader(new FileInputStream("res" + File.separator + "NotificationTemplate.html")));
		String text = null;
		while (in.ready()) {
			if (text == null)
				text = in.readLine();
			text += in.readLine();
		}
		
		Document doc = Jsoup.parse(text);
		Element bookTable = doc.getElementById("table");
		
		while(customerInformationResultSet.next())
		{
			String remainingDays;
			if(customerInformationResultSet.getInt("Remaining_days") < 0)
				remainingDays = "SCADUTO";
			else
				remainingDays = Integer.toString(customerInformationResultSet.getInt("Remaining_days"));
			bookTable.append("<tr align=\"center\" bgcolor=\"#ffffff\" style=\"color: black; font-family: Bahnschrift, sans-serif; font-size: 12px\">" +
							 "<td>" + customerInformationResultSet.getString("Title") + "</td>" +
							 "<td>" + customerInformationResultSet.getString("Deadline_date") + "</td>" +
							 "<td>" + remainingDays + "</td>" +
							 "<td>" + customerInformationResultSet.getDouble("Fine") + "</td>" +
							 "</tr>");
		}
		
		text = doc.toString();
		in.close();
		return text;
	}
}
