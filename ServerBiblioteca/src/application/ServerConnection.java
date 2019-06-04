package application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import databaseManagement.DatabaseConnection;
import databaseSerialization.BookListJSONSerializer;
public class ServerConnection implements Runnable{

    private Socket socket;
    private ServerSocket server;
    private Thread t;
    private boolean notClosed = true;
    private DatabaseConnection databaseConnection;
    private BookListJSONSerializer bookListSerializer;
    
    public ServerConnection(int port) throws IOException, SQLException {
		this.server = new ServerSocket(port);
		this.t = new Thread(this);
		this.t.start();
		this.databaseConnection = new DatabaseConnection();
		this.bookListSerializer = new BookListJSONSerializer(databaseConnection);
		System.out.println(bookListSerializer.getJSONStringArray());
	}
    
    public void close() throws IOException, SQLException {
    	this.notClosed = false;
    	this.server.close();
    	this.databaseConnection.close();
    }
    
	@Override
	public void run() {
		while(notClosed) {
			try {
				socket = server.accept();
				ClientManger cm = new ClientManger(socket, this);
				Thread ct = new Thread(cm);
				ct.start();
				System.out.println("Client Connesso");
			} catch (IOException e) {
				System.out.println("Attenzione: ServerSocket chiuso.");
			}
		}
	}
	
	public BookListJSONSerializer getBookListJSONSerializer() {
		return this.bookListSerializer;
	}
}
