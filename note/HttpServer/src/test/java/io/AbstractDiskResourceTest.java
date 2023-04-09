package io;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

public class AbstractDiskResourceTest {
	
	private AbstractDiskResource<byte[]> TEST;
	private byte[] testStringValue;
	private Timer timer;
	
	@Before
	public void beforeTest() throws ClassNotFoundException, IOException {
		TEST = new AbstractDiskResource<byte[]>(byte[].class) {
			
			@Override
			public void update(byte[] newValue) {
				testStringValue = newValue;
			}
			
			@Override
			public int hashCode() {
				return testStringValue != null ? testStringValue.hashCode() : 0;
			}
			
			@Override
			public byte[] getCurrentValue() {
				return testStringValue;
			}
		};
		
	}
	
	@After
	public void afterTest() {
		
		if(timer != null) {
			timer.cancel();
		}
		
		TEST.SAVE_FILE.delete();
	}

	@Test
	public void constructorTest() throws ClassNotFoundException, IOException {
		new AbstractDiskResource<String>(String.class) {
			
			@Override
			public void update(String newValue) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public int hashCode() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String getCurrentValue() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
	
	
	@Test
	public void saveTest() throws ClassNotFoundException, IOException {		
		testStringValue = RandomStringUtils.random(20).getBytes();
		
		TEST.save();
		
		Gson gson = new Gson();
		
		String rawJson = IOUtils.read(TEST.SAVE_FILE.getPath());
		
		byte[] loadedText = gson.fromJson(rawJson, byte[].class);
		
		assertArrayEquals(testStringValue, loadedText);
	}
	
	@Test
	public void loadTest() throws ClassNotFoundException, IOException {		
		byte[] tempString = RandomStringUtils.random(20).getBytes();
				
		Gson gson = new Gson();
		
		String jsonString = gson.toJson(tempString);
		
		IOUtils.write(TEST.SAVE_FILE.getPath(), jsonString);
		
		byte[] loadedText = TEST.load();
		
		assertArrayEquals(tempString, loadedText);	
	}
	
	@Test
	public void runTest() throws Exception {	
		timer = new Timer();
		
		Gson gson = new Gson();
		
		testStringValue = RandomStringUtils.random(20).getBytes();
		
		String rawJson = IOUtils.read(TEST.SAVE_FILE.getPath());
		
		byte[] loadedText = gson.fromJson(rawJson, byte[].class);
		
		assertFalse(Objects.deepEquals(testStringValue, loadedText));
		
		timer.scheduleAtFixedRate(TEST, 0, 500);
		
		Thread.sleep(510);
		
		rawJson = IOUtils.read(TEST.SAVE_FILE.getPath());
		
		loadedText = gson.fromJson(rawJson, byte[].class);
		
		assertArrayEquals(testStringValue, loadedText);
	}

}
