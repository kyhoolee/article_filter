package id.co.babe.analysis.dao.es;
import java.util.List;
public interface EntityDictionaryEs {
	void insertWord(String word);
	void insertWords(List<String> words);
	List<String> searchRegex(String regex);
	List<String> searchPrefix(String prefix);
	void init() throws Exception;
	
}