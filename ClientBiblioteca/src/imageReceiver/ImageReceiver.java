package imageReceiver;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

public class ImageReceiver {
	
	private String imageFolderPath;
	private Socket socket;
	
	public ImageReceiver(String imageFolderPath, Socket socket) {
		this.imageFolderPath = imageFolderPath;
		this.socket = socket;
	}
	
	public void receiveImagesFromServer(String[] im) throws IOException{
		InputStream inputStream = socket.getInputStream();
		PrintWriter pw = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
		
		for(String string : im) {
			byte[] sizeAr = new byte[4];
			
	        inputStream.read(sizeAr);
	        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
	
	        byte[] imageAr = new byte[size];
	        inputStream.read(imageAr);
	        
	        System.out.println(size);
	
	        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
	        ImageIO.write(image, "jpg", new File(imageFolderPath + File.separator + string));
		}
		
		pw.append("ack" + "\n");
		pw.flush();
	}
	
	public void clearImageFolder() {
		File imageFolder = new File(imageFolderPath);
		File[] imageFolderFileNames = imageFolder.listFiles();
		for(File image : imageFolderFileNames)
			image.delete();
	}

	public String[] getImageNames() {
		File imageFolder = new File(imageFolderPath);
		String[] imageFolderFileNames = imageFolder.list();
		return imageFolderFileNames;
	}
	
}
