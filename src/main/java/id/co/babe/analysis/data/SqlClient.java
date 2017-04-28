package id.co.babe.analysis.data;

import id.co.babe.analysis.model.Category;
import id.co.babe.analysis.util.TextfileIO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SqlClient {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL_BABE =  "jdbc:mysql://10.2.15.2:3306/babe";
	static final String DB_URL_NLP =  "jdbc:mysql://10.2.15.2:3306/babe_nlp";

	// Database credentials
	static final String USER = "babe";
	static final String PASS = "!!babe!!";
	
	
	public static void main(String[] args) {
		writeTagEntity();
	}
	
	public static void writeTagEntity() {
		Set<String> tag = similarEntity("");
		TextfileIO.writeFile("nlp_data/indo_dict/tag_dict.txt", tag);
	}
	
	public static Connection getBabeConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = null;
		conn = DriverManager.getConnection(DB_URL_BABE, USER, PASS);
		return conn;
	}
	
	public static Connection getNlpConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = null;
		conn = DriverManager.getConnection(DB_URL_NLP, USER, PASS);
		return conn;
	}
	
	public static List<Category> getCategory() {
		String sql = "SELECT * FROM sasha_category";
		
		List<Category> cat = getCategory(sql);
		
		return cat;
	}
	

	public static List<Category> getCategory(String sql) {
		
		List<Category> result = new ArrayList<Category>();
		
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getBabeConnection();
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				int catId = rs.getInt("id");
				String catName = rs.getString("title");

				Category cat = new Category(catId, catName);
				result.add(cat);
			}
			
			System.out.println("total: " + result.size());
			
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		
		return result;
	}
	
	public static int countExact(String candidate) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getNlpConnection();
			stmt = conn.createStatement();
			String sql = "select count(*) from tbl_entity_tagged where entity_name like '" + candidate + "';";
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				return rs.getInt(1);
			}
			
			//System.out.println("total: " + result.size());
			
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return 0;
	}
	
	
	public static int countSimilar(String candidate) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getNlpConnection();
			stmt = conn.createStatement();
			String sql = "select count(*) from tbl_entity_tagged where entity_name like '%" + candidate + "%';";
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				return rs.getInt(1);
			}
			
			//System.out.println("total: " + result.size());
			
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return 0;
	}
	
	
	public static Set<String> similarEntity(String candidate) {
		Set<String> result = new HashSet<String>();
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getNlpConnection();
			stmt = conn.createStatement();
			String sql = "select * from tbl_entity_tagged where entity_name like '%" + candidate + "%';";
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String entity = rs.getString("entity_name");
				result.add(entity);
			}
			
			//System.out.println("total: " + result.size());
			
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		
		return result;
	}
}
