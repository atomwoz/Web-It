package com.atomwoz.webit;

public class ConsoleUtils
{

	final static String APP_INFO = """
			usage: Web-IT <directory to serve>
			"directory to serve" is folder witch content is serving
			""";

	static void println(String toPrint)
	{
		System.out.println(toPrint);
	}

	static void err(String toErr)
	{
		System.err.println("[ERROR] " + toErr);
	}

	static void printUsageInfo()
	{
		println(APP_INFO);
	}

	static class Printer
	{
		String id;

		public Printer(String id)
		{
			this.id = id;
		}

		public void println(String toPrint)
		{
			ConsoleUtils.println("[ID: " + id + "] " + toPrint);
		}

		public void err(String toErr)
		{
			System.err.println("[ERROR ID: " + id + "] " + toErr);
		}
	}
}
