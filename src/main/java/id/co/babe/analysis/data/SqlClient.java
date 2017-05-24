package id.co.babe.analysis.data;

import id.co.babe.analysis.model.Category;
import id.co.babe.analysis.util.TextfileIO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
	static final String DB_URL_LOCAL = "jdbc:mysql://localhost/entity_extractor";

	// Database credentials
	static final String BABE_USER = "babe";
	static final String BABE_PASS = "!!babe!!";
	
	static final String LOCAL_USER = "root";
	static final String LOCAL_PASS = "maingames";
	
	
	
	public static List<Category> getBabeCategory() {
		return getCategory();
	}
	
	
	public static void main(String[] args) {
		//writeTagEntity();
		writeWikitagToDB();
		//deleteEntity();
	}
	
	public static void writeWikitagToDB() {
		List<String> entities = TextfileIO.readFile("nlp_data/indo_dict/wiki_tag.txt");
		insertEntity(entities);
	}
	
	public static void writeTagEntity() {
		Set<String> tag = similarEntity("");
		TextfileIO.writeFile("nlp_data/indo_dict/tag_dict.txt", tag);
	}
	
	public static Connection getBabeConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = null;
		conn = DriverManager.getConnection(DB_URL_BABE, BABE_USER, BABE_PASS);
		return conn;
	}
	
	public static Connection getNlpConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = null;
		conn = DriverManager.getConnection(DB_URL_NLP, BABE_USER, BABE_PASS);
		return conn;
	}
	
	public static Connection getEntityConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = null;
		conn = DriverManager.getConnection(DB_URL_LOCAL, LOCAL_USER, LOCAL_PASS);
		return conn;
	}
	
	public static boolean deleteEntity() {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getEntityConnection();
			stmt = conn.createStatement();

			String sql = "DELETE FROM wikiTag;";
			stmt.executeUpdate(sql);
			return true;
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		
		return false;
	}
	
	public static boolean insertEntity(String entity) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getEntityConnection();
			stmt = conn.createStatement();

			String sql = "INSERT INTO "
					+ " wikiTag(word)"
					+ " VALUES ('" + entity + "');";
			stmt.executeUpdate(sql);
			return true;
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		
		return false;
	}
	
	public static void insertEntity(List<String> entities){
		String sql = "INSERT INTO "
				+ " wikiTag(word)"
				+ " VALUES (?);";
	    try {
	        Connection conn = getEntityConnection();
	    		
	        PreparedStatement statement = conn.prepareStatement(sql);
	    
	        int i = 0;

	        for (String entity : entities) {
	        	if(entity.length() > 1) {
		            statement.setString(1, entity);
		            statement.addBatch();
		            i++;
		            System.out.println(entity);
	        	}
	            if (i % 1000 == 0 || i == entities.size()) {
	                statement.executeBatch(); // Execute every 1000 items.
	            }
	        }
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	
	}
	
	
	public static List<Category> getEnabledCategory() {
		String sql = "SELECT * FROM sasha_category where enabled = 1;";
		
		List<Category> cat = getCategory(sql);
		
		return cat;
	}
	
	public static List<Category> getCategory() {
		String sql = "SELECT * FROM sasha_category;";
		
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
