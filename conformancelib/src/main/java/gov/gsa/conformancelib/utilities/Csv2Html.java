package gov.gsa.conformancelib.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;

public class Csv2Html {

	public static String escapeChars(String lineIn) {
		StringBuilder sb = new StringBuilder();
		int lineLength = lineIn.length();
		for (int i = 0; i < lineLength; i++) {
			char c = lineIn.charAt(i);
			switch (c) {
			case '"': 
				sb.append("&quot;");
				break;
			case '&':
				sb.append("&amp;");
				break;
			case '\'':
				sb.append("&apos;");
				break;
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			default: sb.append(c);
			}
		}
		return sb.toString();
	}

	public static void tableHeader(PrintStream ps, String[] columns) {
		ps.print("<tr>");
		for (int i = 0; i < columns.length; i++) {
			String innerHtml = columns[i].replaceAll("^&quot;|&quot;$", "");
			ps.print("<th>");
			ps.print(innerHtml);
			ps.print("</th>");
		}
		ps.println("</tr>");
	}

	public static void tableRow(PrintStream ps, String[] columns) {
		ps.print("<tr>");

		for (int i = 0; i < columns.length; i++) {
			String innerHtml = columns[i].replaceAll("^&quot;|&quot;$", "");
			if (innerHtml.contentEquals("Fail")) {
				ps.print("<td class=\"fail\">");
			} else if (innerHtml.contentEquals("Pass")) {
				ps.print("<td class=\"pass\">");
			} else {
				ps.print("<td>");
			}
			ps.print(innerHtml);
			ps.print("</td>");
		}
		ps.println("</tr>");
	}

	public static void main(String[] args) throws Exception {
		
		if (args.length == 0) {
			System.out.println ("Usage: Csv2Html <filename> [header]\n");
		}
		
		String filename = (args[0]);
		boolean withTableHeader = (args.length > 1);
		
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		 
		File file = new File(classLoader.getResource(filename).getFile());

		BufferedReader br = new BufferedReader(new FileReader(file)); 
		PrintStream stdout = System.out;		
		
		stdout.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
		stdout.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		stdout.println("<head><meta http-equiv=\"Content-type\" content=\"text/html;charset=UTF-8\"/>");
		stdout.println("<title>Test Results</title>");
		stdout.println("<style type=\"text/css\">");
		stdout.println("body{background-color:#FFF;color:#000;font-family:OpenSans,sans-serif;font-size:10px;}");
		stdout.println("body{background-color:#FFF;color:#000;font-family:OpenSans,sans-serif;font-size:10px}");
		stdout.println("table{border:0.2em solid #2F6FAB;border-collapse:collapse}");
		stdout.println("th{border:0.15em solid #2F6FAB;padding:0.5em;background-color:#E9E9E9}");
		stdout.println("td{border:0.1em solid #2F6FAB;padding:0.5em;background-color:#FFFFFF}");
		stdout.println("td.pass{border:0.1em solid #2F6FAB;padding:0.5em;background-color:green;color:black}");
		stdout.println("td.fail{border:0.1em solid #2F6FAB;padding:0.5em;background-color:red;color:yellow}</style>");
		stdout.println("</head><body><h1>Test Results</h1>");

		stdout.println("<table>");
		String stdinLine;
		boolean firstLine = true;
		while ((stdinLine = br.readLine()) != null) {
			String[] columns = escapeChars(stdinLine).split(",");
			if (withTableHeader == true && firstLine == true) {
				tableHeader(stdout, columns);
				firstLine = false;
			} else {
				tableRow(stdout, columns);
			}
		}
		br.close();
		stdout.println("</table></body></html>");
	}
}