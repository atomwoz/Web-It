package com.atomwoz.webit;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Responser
{
	static Map<Integer, String> errorMap = new HashMap<>();

	static
	{
		errorMap.put(404, "File not found");
		errorMap.put(500, "Internal server error");
		errorMap.put(403, "Access forbidden");
	}

	public static void writeHttpResponse(SocketChannel socketChannel, int statusCode, String statusMessage,
			String contentType, long contentLength) throws IOException
	{
		String responseHeaders = "HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n" + "Date: " + new Date()
				+ "\r\n" + "Server: Web-It\r\n" + "Content-Type: " + contentType + "\r\n" + "Content-Length: "
				+ contentLength + "\r\n" + "Connection: close\r\n\r\n";

		writeString(socketChannel, responseHeaders);
	}

	public static void writeString(SocketChannel socketChannel, String message) throws IOException
	{
		// Convert the string to bytes using UTF-8 encoding
		byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

		// Create a ByteBuffer and put the message bytes into it
		ByteBuffer buffer = ByteBuffer.wrap(messageBytes);

		// Write the ByteBuffer to the SocketChannel
		while (buffer.hasRemaining())
		{
			socketChannel.write(buffer);
		}
	}

	public static void writeHttpErr(SocketChannel sck, int code)
	{
		try
		{
			writeHttpResponse(sck, code, errorMap.get(code), "", 0);
		}
		catch (IOException e)
		{
		}
	}
}
