package com.server;

import javax.servlet.ServletException;
import com.undertow.standalone.UndertowServer;

import java.util.concurrent.locks.Condition;


public class Server {

	public static void main(String[] args)
			throws ServletException {

		final String  host = "0.0.0.0";
		final Integer port =  8080;

		final UndertowServer server = new UndertowServer(host, port, "essentialProgramming.jar");
		
		final Condition newCondition = server.LOCK.newCondition();
		
		server.start();
		try {
			while( true )
				newCondition.awaitNanos(100000000);
		} catch ( InterruptedException cause ) {
			server.stop();
		}
	}

}
