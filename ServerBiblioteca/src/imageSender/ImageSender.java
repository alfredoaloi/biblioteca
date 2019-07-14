package imageSender;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ImageSender {
	
	private String imageFolderPath;
	private Socket socket;
	private ArrayList<String> imageList;
	
	public ImageSender(String imageFolderPath, Socket socket, ArrayList<String> imageList) {
		this.imageFolderPath = imageFolderPath;
		this.socket = socket;
		this.imageList = imageList;
	}
	
	public void sendImagesToClient() throws IOException, SQLException {
		OutputStream outputStream = socket.getOutputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		for(String image : imageList) {
			BufferedImage bufferedImage = ImageIO.read(new File(imageFolderPath + File.separator + image));
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
	
	        byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
	
			System.out.println(ByteBuffer.wrap(size).asIntBuffer().get());
			
	        outputStream.write(size);
	        outputStream.write(byteArrayOutputStream.toByteArray());
		}
		outputStream.flush();
		
		String s = br.readLine();
		if(s.contains("ack")) return;
	}

}
