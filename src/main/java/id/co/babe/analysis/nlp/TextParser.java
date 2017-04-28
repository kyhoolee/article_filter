package id.co.babe.analysis.nlp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

public class TextParser {
	
	public static SentenceDetectorME sdetector = null;
	public static Tokenizer tokenizer = null;
	
	public static void init() {
		init("nlp-model/en-sent.bin", "nlp-model/en-token.bin");
	}
	
	public static void init(String sentBin, String tokenBin) {
		try {
			InputStream is = new FileInputStream(sentBin);
			SentenceModel sentModel = new SentenceModel(is);
			sdetector = new SentenceDetectorME(sentModel);
			
			is = new FileInputStream(tokenBin);
			TokenizerModel tokenModel = new TokenizerModel(is);
			tokenizer = new TokenizerME(tokenModel);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static String[] tokenize(String sent) {
		String[] tokens = tokenizer.tokenize(sent);
		return tokens;
	}
	
	public static String[] sentenize(String doc) {
		String[] sents = sdetector.sentDetect(doc);
		return sents;
	}
	
	

	public static void main(String args[]) {
		try {
			organization();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void simpleParse() {

		String sentence = " Hi. How are you? Welcome to Tutorialspoint. "
				+ "We provide free tutorials on various technologies";

		String simple = "[.?!]";
		String[] splitString = (sentence.split(simple));
		for (String string : splitString)
			System.out.println(string);
	}

	public static void sentParse() throws InvalidFormatException, IOException {
		String paragraph = "Hi. How are you? This is Mike.";

		// always start with a model, a model is learned from training data
		InputStream is = new FileInputStream("nlp-model/en-sent.bin");
		SentenceModel model = new SentenceModel(is);
		SentenceDetectorME sdetector = new SentenceDetectorME(model);

		String sentences[] = sdetector.sentDetect(paragraph);

		System.out.println(sentences[0]);
		System.out.println(sentences[1]);
		is.close();
	}

	public static void tokenize() throws InvalidFormatException, IOException {
		InputStream is = new FileInputStream("nlp-model/en-token.bin");

		TokenizerModel model = new TokenizerModel(is);

		Tokenizer tokenizer = new TokenizerME(model);

		String tokens[] = tokenizer.tokenize("Hi. How are you? This is Mike.");

		for (String a : tokens)
			System.out.println(a);

		is.close();
	}

	public static void organization() throws Exception {

		InputStream inputStreamTokenizer = new FileInputStream(
				"nlp-model/en-token.bin");
		TokenizerModel tokenModel = new TokenizerModel(inputStreamTokenizer);

		// String paragraph = "Mike and Smith are classmates";
		String paragraph = "Tutorialspoint is located in Hyderabad";

		// Instantiating the TokenizerME class
		TokenizerME tokenizer = new TokenizerME(tokenModel);
		String tokens[] = tokenizer.tokenize(paragraph);

		// Loading the NER-location moodel
		InputStream inputStreamNameFinder = new FileInputStream(
				"nlp-model/en-ner-person.bin");
		TokenNameFinderModel model = new TokenNameFinderModel(
				inputStreamNameFinder);

		// Instantiating the NameFinderME class
		NameFinderME nameFinder = new NameFinderME(model);

		// Finding the names of a location
		Span nameSpans[] = nameFinder.find(tokens);
		// Printing the spans of the locations in the sentence
		for (Span s : nameSpans) {
			System.out.println(s.toString() + "  " + tokens[s.getStart()]);
		}
	}

}
