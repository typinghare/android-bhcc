package io;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class IOUtils {
	@SuppressWarnings({ "resource", "unchecked" })
	public static <T> T read(String filePath) throws IOException, ClassNotFoundException{

		T returnObject;

		// Reading the object from a file 
		FileInputStream file = null;
		ObjectInputStream in = null;

		try {
			file = new FileInputStream(filePath); 
			in = new ObjectInputStream(file); 

			// Method for deserialization of object of type T
			returnObject = (T)in.readObject();

			return returnObject;

		}catch (java.io.InvalidClassException e) {
			System.out.println("YOU ARE ATTEMPTING TO OPEN A FILE VERSION THAT IS NEWER THAN THIS PROGRAM CAN HANDLE.");
			throw e;
		}catch (EOFException | StreamCorruptedException e) {
			return null;			
		} catch (ClassNotFoundException | IOException e) {
			throw e;
		}finally {
			if(Objects.nonNull(in)) {
				in.close();
			}

			if(Objects.nonNull(file)) {
				file.close();
			}
		}
	}

	@SuppressWarnings("resource")
	public static void write(String filePath, Object object) throws IOException {   

		//Saving of object in a file 
		FileOutputStream file = null;
		ObjectOutputStream out = null;

		try {

			file = new FileOutputStream(filePath); 
			out = new ObjectOutputStream(file); 

			// Method for serialization of object 
			out.writeObject(object);

		}finally {
			if(Objects.nonNull(out)) {
				out.close();
			}

			if(Objects.nonNull(file)) {
				file.close();
			}
		}

	}

	public static Map<String, List<String>> splitQuery(URL url) throws UnsupportedEncodingException {
		final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
		
		if(Objects.isNull(url.getQuery())) {
			return query_pairs;
		}
		
		final String[] pairs = url.getQuery().split("&");
		
		for (String pair : pairs) {
			final int idx = pair.indexOf("=");
			final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
			if (!query_pairs.containsKey(key)) {
				query_pairs.put(key, new LinkedList<String>());
			}
			final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
			query_pairs.get(key).add(value);
		}
		return query_pairs;
	}
}

