package clientBiblioteca;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import imageReceiver.ImageReceiver;
import javafx.util.Pair;

public class Client implements Runnable {

	private Socket socket = null;
	private Gson gson;
	private ArrayList<Category> categoryList;
	private ImageReceiver imageReceiver;
	private BufferedReader in;
	private PrintWriter out;

	public Client() {
		try {
			this.socket = new Socket("localhost", 8000);
			this.gson = new GsonBuilder().create();
			this.categoryList = new ArrayList<Category>();
			this.imageReceiver = new ImageReceiver("images", new Socket("localhost", 8001));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
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

			String s1 = in.readLine();
			String s2 = in.readLine();
			Envelope<String[]> envelope1 = new Envelope<String[]>();
			Envelope<String[]> envelope2 = new Envelope<String[]>();
			envelope1 = gson.fromJson(s1, new TypeToken<Envelope<String[]>>() {
			}.getType());
			envelope2 = gson.fromJson(s2, new TypeToken<Envelope<String[]>>() {
			}.getType());
			if (envelope1.getObject().equals("BOOK_LIST")) {
				String[] cat = envelope1.getContent();
				for (String c : cat) {
					System.out.println(c);
					categoryList.add(gson.fromJson(c, Category.class));
				}
			}
			if (envelope2.getObject().equals("IMAGE_LIST")) {
				String[] im = envelope2.getContent();
				imageReceiver.receiveImagesFromServer(im);
			}

			Envelope<AccessCredentials> envelope3 = new Envelope<AccessCredentials>();
			envelope3.setObject("ACCESS_CREDENTIALS");
			envelope3.setContent(new AccessCredentials("franc1", "bellecose"));
			out.append(gson.toJson(envelope3) + "\n");
			out.flush();

			String input = in.readLine();
			JsonObject jsonObject = new JsonParser().parse(input).getAsJsonObject();

			if (jsonObject.get("object").toString().equals("\"FAILURE\"")) {
				Envelope<String> reportEnvelope = gson.fromJson(input, new TypeToken<Envelope<String>>() {
				}.getType());
				System.out.println(reportEnvelope.getContent());
			} else {
				Envelope<String> userCredentialsEnvelope = gson.fromJson(input, new TypeToken<Envelope<String>>() {
				}.getType());
				Customer c = gson.fromJson(userCredentialsEnvelope.getContent(), Customer.class);
				//System.out.println(c.getUsername());
			}
			return;
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

	public String loginUser(Pair<String, String> login) {
		String input = "";
		Envelope<AccessCredentials> accessCredentialsEnvelope = new Envelope<AccessCredentials>();
		accessCredentialsEnvelope.setObject("ACCESS_CREDENTIALS");
		accessCredentialsEnvelope.setContent(new AccessCredentials(login.getKey(), login.getValue()));
		out.append(gson.toJson(accessCredentialsEnvelope) + "\n");
		out.flush();
		try {
			input = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return input;
	}

	public ArrayList<LentBook> libriNoleggiati(String username) {
		ArrayList<LentBook> noleggiati = new ArrayList<LentBook>();

		Envelope<String> envelope = new Envelope<String>("GET_CUSTOMER_LENT_BOOKS", username);
		System.out.println(username);
		out.append(gson.toJson(envelope) + "\n");
		out.flush();
		String input = "";
		try {
			input = in.readLine();
			System.out.println(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Envelope<String[]> array = gson.fromJson(input, new TypeToken<Envelope<String[]>>() {
		}.getType());

		for (String s : array.getContent()) {
			System.out.println(s);
			noleggiati.add(gson.fromJson(s, LentBook.class));
		}

		return noleggiati;
	}

}
