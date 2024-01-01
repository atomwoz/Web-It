package com.atomwoz.webit;

import java.util.HashMap;
import java.util.Map;

public class MimeAndDefaultsMapper
{
	static String[] indexFiles = new String[]
	{ "index.html", "index.htm", "index.php", "index.py" };
	static Map<String, String> mimeMap = new HashMap<>();

	static
	{
		mimeMap.put("html", "text/html");
		mimeMap.put("htm", "text/html");
		mimeMap.put("php", "text/php");
		mimeMap.put("py", "text/python");
		mimeMap.put("txt", "text/plain");
		mimeMap.put("css", "text/css");
		mimeMap.put("js", "application/javascript");
		mimeMap.put("json", "application/json");
		mimeMap.put("xml", "application/xml");
		mimeMap.put("jpg", "image/jpeg");
		mimeMap.put("jpeg", "image/jpeg");
		mimeMap.put("png", "image/png");
		mimeMap.put("gif", "image/gif");
		mimeMap.put("pdf", "application/pdf");
	}
}
