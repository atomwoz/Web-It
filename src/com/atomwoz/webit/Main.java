package com.atomwoz.webit;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Main
{

	final static int PORT = 80;

	public static void main(String[] args)
	{
		if (args.length < 1)
		{
			ConsoleUtils.printUsageInfo();
			System.exit(1);
		}
		if (!args[0].endsWith(File.separator))
			args[0] += File.separator;
		try
		{

			@SuppressWarnings("resource")
			ServerSocketChannel sck = ServerSocketChannel.open().bind(new InetSocketAddress(PORT));
			try (sck)
			{
				ConsoleUtils.println("Server started at port: " + PORT);
				ConsoleUtils.println("Serving: " + args[0]);
				while (true)
				{
					SocketChannel remoteSocket = sck.accept();
					ConnectionHandler ch = new ConnectionHandler(remoteSocket, args[0]);
					new Thread(ch).start();
				}
			}
		}
		catch (IOException e)
		{
			ConsoleUtils.err("There was error when trying to open a " + PORT + " port");
			e.printStackTrace();
		}
	}

}
