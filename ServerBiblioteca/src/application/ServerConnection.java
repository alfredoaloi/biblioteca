package application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import databaseManagement.DatabaseConnection;
import databaseSerialization.BookListJSONSerializer;
import databaseSerialization.ImageListSerializer;
import databaseSerialization.UserListJSONSerializer;
import notificationManager.NotificationManager;

public class ServerConnection implements Runnable {

	private Socket socket;
	private Socket imageSocket;
	private ServerSocket server;
	private ServerSocket imageServerSocket;
	private Thread t;
	private boolean notClosed = true;
	private DatabaseConnection databaseConnection;
	private BookListJSONSerializer bookListSerializer;
	private UserListJSONSerializer userListSerializer;
	private ImageListSerializer imagelistSerializer;

	public ServerConnection(int port) throws IOException, SQLException, ParseException {
		this.server = new ServerSocket(port);
		this.imageServerSocket = new ServerSocket(port + 1);
		this.t = new Thread(this);
		this.t.start();
		this.databaseConnection = new DatabaseConnection();
		this.bookListSerializer = new BookListJSONSerializer(databaseConnection);
		this.userListSerializer = new UserListJSONSerializer(databaseConnection);
		this.imagelistSerializer = new ImageListSerializer("res");

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date scheduledDate = dateFormat.parse(databaseConnection.getNextScheduledDeadlineCheck().getString(1));
		Date now = dateFormat.parse(dateFormat.format(new Date()));

		long delay = scheduledDate.getTime() - now.getTime();
		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
		ses.scheduleAtFixedRate(new NotificationManager(databaseConnection), delay, 1, TimeUnit.DAYS);
	}

	public void close() throws IOException, SQLException {
		this.notClosed = false;
		this.server.close();
		this.imageServerSocket.close();
		this.databaseConnection.close();
	}

	@Override
	public void run() {
		while (notClosed) {
			try {
				socket = server.accept();
				imageSocket = imageServerSocket.accept();
				ClientManger cm = new ClientManger(socket, imageSocket, this);
				Thread ct = new Thread(cm);
				ct.start();
			} catch (IOException e) {
			}
		}
	}

	public BookListJSONSerializer getBookListJSONSerializer() {
		return this.bookListSerializer;
	}

	public UserListJSONSerializer getUserListJSONSerializer() {
		return this.userListSerializer;
	}

	public ImageListSerializer getImageListSerializer() {
		return this.imagelistSerializer;
	}

	public void refreshBookList() throws SQLException {
		this.bookListSerializer = new BookListJSONSerializer(databaseConnection);
	}

	public void refreshUserList() throws SQLException {
		this.userListSerializer = new UserListJSONSerializer(databaseConnection);
	}

	public void refreshImageList() throws SQLException {
		this.imagelistSerializer = new ImageListSerializer("res");
	}

	public DatabaseConnection getDatabaseConnection() {
		return databaseConnection;
	}

}
