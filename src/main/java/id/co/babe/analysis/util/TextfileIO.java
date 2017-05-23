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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class TextfileIO {

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
	
	
	
	
	public static Set<String> readFileFilter(String filePath, StringFilter filter) {
		Set<String> result = new HashSet<>();

		BufferedReader br = null;
		FileReader fr = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filePath), "UTF8"));

			String line;
			int count = 0;
			while ((line = br.readLine()) != null) {
				count ++;
				if(count <= 10)
					System.out.println(line);
				String w = filter.filter(line);
				if (!w.isEmpty()) {
					result.add(w);
				}

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
	
	
	
	public static List<String> readFileLimit(String filePath, int start, int offset) {
		List<String> result = new ArrayList<>();

		BufferedReader br = null;
		FileReader fr = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filePath), "UTF8"));

			String line;
			int count = 0;
			while ((line = br.readLine()) != null) {
				count ++;
				if (count >= start && count <= (start + offset) && line != null && !line.isEmpty())
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

	
	public static void initSetFile(String filePath, Collection<String> c) {
		BufferedReader br = null;
		FileReader fr = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filePath), "UTF8"));

			String line;
			while ((line = br.readLine()) != null) {
				if (line != null && !line.isEmpty())
					c.add(line);

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
