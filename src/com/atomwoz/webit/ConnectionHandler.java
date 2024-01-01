package com.atomwoz.webit;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.AccessDeniedException;
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
					HttpRequestParser.HttpRequest req = HttpRequestParser.parseRequest(socketChannel);
					con.println(socketChannel.getRemoteAddress().toString() + " is requesting " + req.getPath());
					Path requestedPath = Paths.get(dir + File.separator + req.getPath()).normalize();
					Path expectedDirectory = Paths.get(dir).normalize();
					if (!requestedPath.startsWith(expectedDirectory))
					{
						con.err("Invalid file path.");
						Responser.writeHttpErr(socketChannel, 404);
						return;
					}
					File requestedFile = requestedPath.toFile();

					// Whenever it not exists or it isn't accessible return 404
					if (!requestedFile.canRead())
					{
						con.err("Requested resource can't be reached, "
								+ (requestedFile.exists() ? "access denied" : "file not exists"));
						Responser.writeHttpErr(socketChannel, 404);
						return;
					}
					FileChannel reqFile = FileChannel.open(requestedPath, StandardOpenOption.READ);
					String[] extensions = requestedPath.toFile().getName().split("\\.");
					if (extensions.length < 2)
					{
						con.err("No file extension");
						Responser.writeHttpResponse(socketChannel, 400, "No file extension", "", 0);
						return;
					}
					String mime = MimeAndDefaultsMapper.mimeMap.get(extensions[1]);
					Responser.writeHttpResponse(socketChannel, 200, "OK", mime, reqFile.size() + 4);
					try (reqFile)
					{
						ByteBuffer fileBuff = ByteBuffer.allocate(8192);
						while (reqFile.read(fileBuff) > 0)
						{
							fileBuff.flip();
							socketChannel.write(fileBuff);
							fileBuff.clear();
						}
						Responser.writeString(socketChannel, "\r\n\r\n");
					}
				}
				catch (AccessDeniedException e)
				{
					con.err("Access denied to file: " + e.getMessage());
					Responser.writeHttpErr(socketChannel, 404);
				}
				catch (IOException e)
				{
					con.err("Internal server error: " + e.getClass().getCanonicalName());
					Responser.writeHttpErr(socketChannel, 500);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
