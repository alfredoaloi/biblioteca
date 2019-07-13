package clientBiblioteca;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;

public class Client implements Runnable {

	private Socket socket = null;
	private Gson gson;
	private ArrayList<Category> categoryList;

	public Client() {
		try {
			this.socket = new Socket("localhost", 8000);
			this.gson = new Gson();
			this.categoryList = new ArrayList<Category>();
			Thread t = new Thread(this);
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			if (socket == null)
				return;
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
			
			out.append("ACCESS_CREDENTIALS" + "\n");
			out.append(gson.toJson(new AccessCredentials("franc1", "bellecose")) + "\n");
			out.flush();
			
			while (true) {
				if (in.ready()) {
					String inputLine = in.readLine();
					if (inputLine.contains("ENDOFSTREAM"))
						return;
					else if (inputLine.contains("CUSTOMER")) {
						System.out.println(in.readLine());
					}
					else {
						Category c = gson.fromJson(inputLine, Category.class);
						System.out.println(c);
						categoryList.add(c);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Category> getCategoryList() {
		return categoryList;
	}
	
	public static void main(String[] args) {
		Client c = new Client();
	}
}
