package com.atomwoz.webit;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser
{

	private static final Charset CHARSET = StandardCharsets.UTF_8;
	private static final String CRLF = "\r\n";

	public static HttpRequest parseRequest(SocketChannel socketChannel) throws IOException
	{
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		StringBuilder requestBuilder = new StringBuilder();

		// Read data from the channel into the buffer
		int bytesRead;
		while ((bytesRead = socketChannel.read(buffer)) > 0)
		{
			buffer.flip();
			byte[] data = new byte[buffer.remaining()];
			buffer.get(data);
			requestBuilder.append(new String(data, CHARSET));
			buffer.clear();

			// Check if the request headers have been fully received
			if (requestBuilder.toString().contains(CRLF + CRLF))
			{
				break;
			}
		}
		// Parse the HTTP request
		return parseHttpRequest(requestBuilder.toString());
	}

	private static HttpRequest parseHttpRequest(String httpRequestString)
	{
		String[] lines = httpRequestString.split(CRLF);
		String[] requestLine = lines[0].split(" ");

		// Extract method, path, and HTTP version
		String method = requestLine[0];
		String path = requestLine[1];
		String httpVersion = requestLine[2];

		// Parse headers
		Map<String, String> headers = new HashMap<>();
		for (int i = 1; i < lines.length; i++)
		{
			String[] headerParts = lines[i].split(": ");
			if (headerParts.length == 2)
			{
				headers.put(headerParts[0], headerParts[1]);
			}
		}
		// Create and return an HttpRequest object
		return new HttpRequest(method, path, httpVersion, headers);
	}

	public static class HttpRequest
	{
		private final String method;
		private final String path;
		private final String httpVersion;
		private final Map<String, String> headers;

		public HttpRequest(String method, String path, String httpVersion, Map<String, String> headers)
		{
			this.method = method;
			this.path = path;
			this.httpVersion = httpVersion;
			this.headers = headers;
		}

		public String getMethod()
		{
			return method;
		}

		public String getPath()
		{
			return path;
		}

		public String getHttpVersion()
		{
			return httpVersion;
		}

		public Map<String, String> getHeaders()
		{
			return headers;
		}
	}
}
