package noteServer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.experimental.theories.Theories;

import authentication.Account;
import httpServer.HTTPServer;
import httpServer.ServerDelegator;

public class NoteServer extends HTTPServer {

	private ServerArbiter arbiter;
	private ScheduledExecutorService threadPool;
	private boolean isConnected = true;

	public static void main(String[] args) throws Exception {

		int port = 12345;
		long period = 1;
		TimeUnit timeUnit = TimeUnit.SECONDS;

		if(args.length > 0 && NumberUtils.isParsable(args[0]))
			port = Integer.parseInt(args[0]);
		else if(args.length > 0 && !NumberUtils.isParsable(args[0]))
			System.out.println("Incorrectly formated port");

		if(args.length > 1 && NumberUtils.isParsable(args[1]))
			port = Integer.parseInt(args[1]);
		else if(args.length > 1 && !NumberUtils.isParsable(args[1]))
			System.out.println("Incorrectly formated time period");

		if(args.length > 2 && EnumUtils.isValidEnumIgnoreCase(TimeUnit.class, args[2]))
			timeUnit = EnumUtils.getEnumIgnoreCase(TimeUnit.class, args[2]);
		else if(args.length > 2 && !EnumUtils.isValidEnumIgnoreCase(TimeUnit.class, args[2]))
			System.out.println("Incorrectly formated time unit");

		NoteServer manager = new NoteServer(port, period, timeUnit);

		Scanner keyboard = new Scanner(System.in);
		System.out.println("Press [Enter] to terminate the server");
		keyboard.nextLine();
		manager.stop();
		keyboard.close();

		//TODO create a CLI for easy account creation and document creation
	}

	private static boolean netIsAvailable() {
		try {
			final URL url = new URL("http://www.google.com");
			final URLConnection conn = url.openConnection();
			conn.connect();
			conn.getInputStream().close();
			return true;
		} catch (IOException e) {
			return false;
		} catch (Exception e) {
			System.err.println(e);
			return false;
		}
	}

	void startConnectivityThread() {
		threadPool.scheduleAtFixedRate(()->{

			boolean isConnectNow = netIsAvailable();

			if(!isConnectNow) {
				System.err.println("The server is no longer connected to the internet, it will not receive any requests. Please check your internet connection.");
			}else if(isConnectNow && !isConnected) {
				System.out.println("The server is connected again.");
			}

			isConnected = isConnectNow;

		}, 0, 10, TimeUnit.SECONDS);

	}

	public ServerArbiter getArbiter() {
		return arbiter;
	}

	public NoteServer(int port, long period, TimeUnit timeUnit) throws Exception {
		super(port);
		try {
			arbiter = new ServerArbiter();

			threadPool = Executors.newScheduledThreadPool(2);

			threadPool.scheduleAtFixedRate(arbiter, 0, period, timeUnit);

			ServerWrapper delegator = new ServerWrapper(arbiter);

			setDelegator(delegator);

			startConnectivityThread();
		} catch (Exception e) {
			e.printStackTrace();
			
			this.stop();
		}

	}

	public void stop() {
		super.stop();

		try {
			if(arbiter != null)
				arbiter.save();
		} catch (IOException e) {
			System.err.println("Not all resources were saved prior to termination. Some data may have been lost.");
			e.printStackTrace();
		}

		if(threadPool != null) {
			
			threadPool.shutdown();

			try {
				threadPool.awaitTermination(15, TimeUnit.SECONDS);
			} catch (InterruptedException e) {}
		}
	}

}
