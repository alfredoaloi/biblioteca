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

public class Client implements Runnable {

	private Socket socket = null;
	private Gson gson;
	private ArrayList<Category> categoryList;
	private ImageReceiver imageReceiver;

	public Client() {
		try {
			this.socket = new Socket("localhost", 8000);
			this.gson = new GsonBuilder().create();
			this.categoryList = new ArrayList<Category>();
			this.imageReceiver = new ImageReceiver("images", new Socket("localhost", 8001));
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
			
			String s1 = in.readLine();
			String s2 = in.readLine();
			Envelope<String[]> envelope1 = new Envelope<String[]>();
			Envelope<String[]> envelope2 = new Envelope<String[]>();
			envelope1 = gson.fromJson(s1, new TypeToken<Envelope<String[]>>(){}.getType());
			envelope2 = gson.fromJson(s2, new TypeToken<Envelope<String[]>>(){}.getType());
			if(envelope1.getObject().equals("BOOK_LIST")) {
				String[] cat = envelope1.getContent();
				for(String c : cat) {
					System.out.println(c);
					categoryList.add(gson.fromJson(c, Category.class));
				}
			}
			if(envelope2.getObject().equals("IMAGE_LIST")) {
				String[] im = envelope2.getContent();
				imageReceiver.receiveImagesFromServer(im);
			}
			
			Envelope<AccessCredentials> envelope3 = new Envelope<AccessCredentials>();
			envelope3.setObject("ACCESS_CREDENTIALS");
			envelope3.setContent(new AccessCredentials("aaaaa", "aaaaaa"));
			out.append(gson.toJson(envelope3) + "\n");
			out.flush();
			
			String input = in.readLine();
			JsonObject jsonObject = new JsonParser().parse(input).getAsJsonObject();
			
			if(jsonObject.get("object").toString().equals("\"FAILURE\"")) {
				Envelope<String> reportEnvelope = gson.fromJson(input, new TypeToken<Envelope<String>>(){}.getType());
				System.out.println(reportEnvelope.getContent());
			}
			else {
				Envelope<String> userCredentialsEnvelope = gson.fromJson(input, new TypeToken<Envelope<String>>(){}.getType());
				System.out.println(userCredentialsEnvelope.getContent());
			}
			envelope3 = new Envelope<AccessCredentials>();
			envelope3.setObject("ACCESS_CREDENTIALS");
			envelope3.setContent(new AccessCredentials("aaaaa", "aaaaaa"));
			out.append(gson.toJson(envelope3) + "\n");
			out.flush();
			
			input = in.readLine();
			jsonObject = new JsonParser().parse(input).getAsJsonObject();
			
			if(jsonObject.get("object").toString().equals("\"FAILURE\"")) {
				Envelope<String> reportEnvelope = gson.fromJson(input, new TypeToken<Envelope<String>>(){}.getType());
				System.out.println(reportEnvelope.getContent());
			}
			else {
				Envelope<String> userCredentialsEnvelope = gson.fromJson(input, new TypeToken<Envelope<String>>(){}.getType());
				System.out.println(userCredentialsEnvelope.getContent());
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
}
