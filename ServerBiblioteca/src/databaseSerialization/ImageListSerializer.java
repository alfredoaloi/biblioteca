package databaseSerialization;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ImageListSerializer {

	protected ArrayList<String> stringArray;

	public ImageListSerializer(String imagePath) {
		stringArray = new ArrayList<String>();
		for (String image : new File(imagePath).list()) {
			if (Pattern.matches(".+\\.jpg", image))
				stringArray.add(image);
		}
	}

	public String[] getStringArray() {
		return stringArray.toArray(new String[stringArray.size()]);
	}

}
