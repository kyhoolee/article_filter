package id.co.babe.analysis.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class TextfileIO {

	private static final char DEFAULT_SEPARATOR = ',';
	private static final char DEFAULT_QUOTE = '"';

	public static void writeCsv(String filePath, List<String[]> data) {
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(filePath));
			for (String[] record : data) {
				writer.writeNext(record);
			}
			writer.close();
		} catch (Exception e) {

		}
	}

	public static List<String[]> readCsv(String filePath) {
		try {
			CSVReader reader = new CSVReader(new FileReader(filePath), ',','"', 1);
			List<String[]> allRows = reader.readAll();
			reader.close();
			return allRows;
		} catch (Exception e) {

		}
		return new ArrayList<String[]>();
	}

	public static List<String> parseLine(String cvsLine) {
		return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
	}

	public static List<String> parseLine(String cvsLine, char separators) {
		return parseLine(cvsLine, separators, DEFAULT_QUOTE);
	}

	public static List<String> parseLine(String cvsLine, char separators,
			char customQuote) {
		List<String> result = new ArrayList<>();
		// if empty, return!
		if (cvsLine == null || cvsLine.isEmpty()) {
			return result;
		}
		if (customQuote == ' ') {
			customQuote = DEFAULT_QUOTE;
		}
		if (separators == ' ') {
			separators = DEFAULT_SEPARATOR;
		}
		StringBuffer curVal = new StringBuffer();
		boolean inQuotes = false;
		boolean startCollectChar = false;
		boolean doubleQuotesInColumn = false;

		char[] chars = cvsLine.toCharArray();
		for (char ch : chars) {
			if (inQuotes) {
				startCollectChar = true;
				if (ch == customQuote) {
					inQuotes = false;
					doubleQuotesInColumn = false;
				} else {
					// Fixed : allow "" in custom quote enclosed
					if (ch == '\"') {
						if (!doubleQuotesInColumn) {
							curVal.append(ch);
							doubleQuotesInColumn = true;
						}
					} else {
						curVal.append(ch);
					}
				}
			} else {
				if (ch == customQuote) {
					inQuotes = true;
					// Fixed : allow "" in empty quote enclosed
					if (chars[0] != '"' && customQuote == '\"') {
						curVal.append('"');
					}
					// double quotes in column will hit this!
					if (startCollectChar) {
						curVal.append('"');
					}
				} else if (ch == separators) {
					result.add(curVal.toString());
					curVal = new StringBuffer();
					startCollectChar = false;
				} else if (ch == '\r') {
					// ignore LF characters
					continue;
				} else if (ch == '\n') {
					// the end, break!
					break;
				} else {
					curVal.append(ch);
				}
			}
		}
		result.add(curVal.toString());
		return result;
	}

	public static List<String> readFile(InputStream is) {
		List<String> result = new ArrayList<>();

		BufferedReader br = null;
		FileReader fr = null;

		try {
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			String line;
			while ((line = br.readLine()) != null) {
				result.add(line);

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return result;
	}

	public static List<String> readFile(String filePath) {
		List<String> result = new ArrayList<>();

		BufferedReader br = null;
		FileReader fr = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filePath), "UTF8"));

			String line;
			while ((line = br.readLine()) != null) {
				if (line != null && !line.isEmpty())
					result.add(line);

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return result;
	}

	public static void appendFile(String filePath, List<String> data) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			fw = new FileWriter(filePath, true);
			bw = new BufferedWriter(fw);

			for (int i = 0; i < data.size(); i++) {
				bw.write(data.get(i));
				bw.newLine();
			}
			System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void writeFile(String filePath, Collection<String> data) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			fw = new FileWriter(filePath);
			bw = new BufferedWriter(fw);

			for (String l : data) {
				bw.write(l);
				bw.newLine();
			}
			System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
