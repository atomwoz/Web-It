package com.atomwoz.webit;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicLong;

public class ConnectionHandler implements Runnable
{

	static AtomicLong TID = new AtomicLong(1);

	final SocketChannel socketChannel;
	final ConsoleUtils.Printer con;
	final String dir;

	public ConnectionHandler(SocketChannel sck, String dir)
	{
		this.socketChannel = sck;
		this.con = new ConsoleUtils.Printer(String.valueOf(TID));
		this.dir = dir;
		TID.getAndIncrement();

	}

	@Override
	public void run()
	{
		try
		{
			socketChannel.configureBlocking(false);
			try (socketChannel)
			{
				try
				{
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					HttpRequestParser.HttpRequest req = HttpRequestParser.parseRequest(socketChannel);
					con.println(socketChannel.getRemoteAddress().toString() + " is requesting " + req.getPath());
					Path requestedPath = Paths.get(dir + File.separator + req.getPath()).normalize();
					Path expectedDirectory = Paths.get(dir).normalize();
					if (!requestedPath.startsWith(expectedDirectory))
					{
						con.err("Invalid file path.");
						return;
					}
					FileChannel reqFile = FileChannel.open(requestedPath, StandardOpenOption.READ);
					String[] extensions = requestedPath.toFile().getName().split("\\.");
					if (extensions.length < 2)
					{
						Responser.writeHttpResponse(socketChannel, 400, "No file extension", "", 0);
						con.err("No file extension");
						return;
					}
					String mime = MimeAndDefaultsMapper.mimeMap.get(extensions[1]);
					Responser.writeHttpResponse(socketChannel, 200, "OK", mime, reqFile.size() + 4);
					try (reqFile)
					{
						ByteBuffer fileBuff = ByteBuffer.allocate(8192);
						while (reqFile.read(fileBuff) > 0)
						{
							fileBuff.rewind();
							socketChannel.write(fileBuff);
						}
						Responser.writeString(socketChannel, "\r\n\r\n");
					}
				}
				catch (IOException e)
				{
					try
					{
						Responser.writeHttpResponse(socketChannel, 500, "Internal server error", "", 0);
					}
					catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					con.err("Error with serving content to client: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
