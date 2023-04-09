package io;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import noteServer.ServerData;

public abstract class AbstractDiskResource<T> extends TimerTask{

	private int previousHash = -1;
	public final File SAVE_FILE;
	private final Gson JSON_READER;
	private final Class<?> WORKING_CLASS;
	private final ReentrantReadWriteLock LOCK;

	public AbstractDiskResource(Class<T> clazz) throws ClassNotFoundException, IOException {
		String jarDir = "";
		
		try {
			jarDir = AbstractDiskResource.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		} catch (Exception e) {
			String path = AbstractDiskResource.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			jarDir = URLDecoder.decode(path, "UTF-8");
		}
		
		if(!jarDir.contains(".jar")) {
			jarDir = System.getProperty("user.dir");
		}else {
			File file = new File(jarDir);
			jarDir = file.getParentFile().getPath();
		}

		SAVE_FILE = new File(jarDir+"/db.data");
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		
		gsonBuilder.registerTypeAdapter(Table.class, new TableDesrializer());
		gsonBuilder.registerTypeAdapter(Table.class, new TableSerializer());
		
		gsonBuilder.setPrettyPrinting();

		JSON_READER = gsonBuilder.create();

		WORKING_CLASS = clazz;

		LOCK = new ReentrantReadWriteLock();

		if(SAVE_FILE.exists()) {
					
			T savedValue = load();
						
			update(savedValue);
			
		}else {
			save();
		}

	}
	
	public ReentrantReadWriteLock getLock() {
		return LOCK;
	}

	@Override
	public void run() {
		try {
		int currentHash = hashCode();

		if(previousHash != currentHash) {
			try {
				save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public abstract int hashCode();

	public abstract void update(T newValue);

	public abstract T getCurrentValue();

	public void save() throws IOException {
		LOCK.writeLock().lock();
		try {
			T currentValue =  getCurrentValue();

			if(Objects.nonNull(currentValue)) {
				String toSave = JSON_READER.toJson(currentValue, WORKING_CLASS);

				IOUtils.write(SAVE_FILE.getPath(), toSave);
			}else {
				IOUtils.write(SAVE_FILE.getPath(), "");
			}

		} finally {
			LOCK.writeLock().unlock();
		}
	}

	@SuppressWarnings("unchecked")
	public T load() throws ClassNotFoundException, IOException {
		LOCK.writeLock().lock();
		try {
			String loadedJson = IOUtils.read(SAVE_FILE.getPath());
			
			T loadedObject = (T) JSON_READER.fromJson(loadedJson, WORKING_CLASS);
			
			return loadedObject;

		} finally {
			LOCK.writeLock().unlock();
		}
	}


}
