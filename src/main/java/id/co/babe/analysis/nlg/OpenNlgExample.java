package id.co.babe.analysis.nlg;

import simplenlg.features.Feature;
import simplenlg.features.InterrogativeType;
import simplenlg.features.Tense;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.XMLLexicon;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

public class OpenNlgExample {
	
	public static void main(String[] args) {
		lexicon();
	}
	
	public static void example() {
		Lexicon lexicon = Lexicon.getDefaultLexicon();
        NLGFactory nlgFactory = new NLGFactory(lexicon);
        Realiser realiser = new Realiser(lexicon);
        
        NLGElement s1 = nlgFactory.createSentence("my dog is happy");
        
        String output = realiser.realiseSentence(s1);
        System.out.println(output);
	}
	
	public static void lexicon() {
		Lexicon lexicon = Lexicon.getDefaultLexicon();
        NLGFactory nlgFactory = new NLGFactory(lexicon);
        Realiser realiser = new Realiser(lexicon);
        NLGElement s1 = nlgFactory.createSentence(" while my dog is happy");
        SPhraseSpec p = nlgFactory.createClause();
        p.setSubject("Mary");
        p.setVerb("chase");
        p.setObject("the mokey");
        p.setFeature(Feature.TENSE, Tense.PAST);
        p.setFeature(Feature.NEGATED, true);
        p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_SUBJECT);
        p.addComplement(s1);
        System.out.println(realiser.realiseSentence(p));
	}

}
