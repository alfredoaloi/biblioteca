package clientBiblioteca;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
			while (true) {
				if (in.ready()) {
					String inputLine = in.readLine();
					if (inputLine.contains("ENDOFSTREAM"))
						return;
					Category c = gson.fromJson(inputLine, Category.class);
					System.out.println(c);
					categoryList.add(c);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Category> getCategoryList() {
		return categoryList;
	}
}
