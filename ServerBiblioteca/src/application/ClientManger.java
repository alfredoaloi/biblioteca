package application;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


public class ClientManger implements Runnable {

	private Socket socket;
	private ServerConnection serverConnection;
	
	public ClientManger(Socket socket, ServerConnection serverConnection) throws IOException{
		this.socket = socket;
		this.serverConnection = serverConnection;
	}
	
	@Override
	public void run() {
		try {
			PrintWriter out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
			ArrayList<String> stringArray = serverConnection.getBookListJSONSerializer().getJSONStringArray();
			for(String s : stringArray) {
				out.append(s + "\n");
				out.flush();
			}
			out.append("ENDOFSTREAM" + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
