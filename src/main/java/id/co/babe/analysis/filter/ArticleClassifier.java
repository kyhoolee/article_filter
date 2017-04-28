package id.co.babe.analysis.filter;

import id.co.babe.analysis.data.FileClient;
import id.co.babe.analysis.model.Article;
import id.co.babe.analysis.model.ArticleDataset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.MaxEnt;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.FeatureSequence2FeatureVector;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceLowercase;
import cc.mallet.pipe.iterator.ArrayIterator;
import cc.mallet.types.InstanceList;

public class ArticleClassifier {


	public static ArticleDataset buildData(String good_file, String bad_file,
			double train_percent) {
		ArticleDataset data = new ArticleDataset();

		data.updateData(FileClient.readBadArticle(bad_file), train_percent);
		data.updateData(FileClient.readGoodArticle(good_file), train_percent);

		System.out.println("Train data: ");
		System.out.println(data.train_pos + " -- " + data.train_neg);

		System.out.println("\n\nTest data: ");
		System.out.println(data.test_pos + " -- " + data.test_neg);
		System.out.println("\n\n-------------------");

		return data;
	}


	public static Classifier loadClassifier(File serializedFile)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		Classifier classifier;

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				serializedFile));
		classifier = (Classifier) ois.readObject();
		ois.close();

		return classifier;
	}

	public static void saveClassifier(Classifier classifier, File serializedFile)
			throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				serializedFile));
		oos.writeObject(classifier);
		oos.close();
	}

	public static Classifier trainClassifier(InstanceList trainingInstances) {
		ClassifierTrainer<MaxEnt> trainer = new MaxEntTrainer();
		return trainer.train(trainingInstances);
	}

	public static InstanceList buildInstance(String bad_file, String good_file,
			double train_percent) {
		ArticleDataset data = buildData(bad_file, good_file, train_percent);
		return buildInstance(data);
	}

	public static InstanceList buildInstance(ArticleDataset data) {
		List<String> badTrain = new ArrayList<>();
		List<String> goodTrain = new ArrayList<>();
		for (int i = 0; i < data.train.size(); i++) {
			Article k = data.train.get(i);
			String train_content = k.content;//NorvigSpellCorrector.correctSentence(k.content, NorvigSpellCorrector.dict); 
					//
			if (k.label == Article.NORMAL) {
				goodTrain.add(train_content);
			}
			if (k.label == Article.BAD) {
				badTrain.add(train_content);
			}
		}
		InstanceList instances = new InstanceList(new SerialPipes(new Pipe[] {
				new Target2Label(), 
				new CharSequence2TokenSequence(),
				new TokenSequence2FeatureSequence(),
				new FeatureVectorPipe()
		}));
		
		
		instances.addThruPipe(new ArrayIterator(badTrain, Article.BAD));
		instances.addThruPipe(new ArrayIterator(goodTrain, Article.NORMAL));
		return instances;
	}

	public static Classifier buildClassifier(String bad_file, String good_file,
			double train_percent, String classifier_file) {
		InstanceList instances = buildInstance(bad_file, good_file,
				train_percent);
		Classifier classifier = trainClassifier(instances);
		try {
			saveClassifier(classifier, new File(classifier_file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classifier;
	}
	
	
	public static Classifier buildClassifier(ArticleDataset dataset, String classifier_file) {
		InstanceList instances = buildInstance(dataset);
		Classifier classifier = trainClassifier(instances);
		try {
			saveClassifier(classifier, new File(classifier_file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classifier;
	}

	public static void loadAndEstimate(String bad_file, String good_file,
			double train_percent, String classifier_file) {
		
		ArticleDataset data = buildData(bad_file, good_file, train_percent);
		try {
			Classifier c = loadClassifier(new File(classifier_file));
			estimateClassifier(c, data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void estimateClassifier(Classifier c, ArticleDataset data) {
		int false_pos = 0;
		int false_neg = 0;
		int true_pos = 0;
		int true_neg = 0;

		for (int i = 0; i < data.test.size(); i++) {
			Article k = data.test.get(i);
			String test_content = k.content;
			String res = c.classify(test_content).getLabeling().getBestLabel().toString();

			if (k.label.equals(Article.NORMAL)) {
				if (res.equals(Article.NORMAL)) {
					true_neg++;
				} else {
					false_neg++;
				}
			} else {
				if (res.equals(Article.BAD)) {
					true_pos++;
				} else {
					false_pos++;
				}
			}
		}

		showResult(true_pos, false_neg, false_pos, true_neg);
	}
	
	/**
	 * Calculate and show precision and recall result
	 * @param true_pos
	 * @param false_neg
	 * @param false_pos
	 * @param true_neg
	 */
	public static void showResult(double true_pos, double false_neg, double false_pos, double true_neg) {
		System.out.println();
		System.out.println("true_pos: " + true_pos + " -- false_neg: " + false_neg);
		System.out.println("false_pos: " + false_pos + " -- true_neg: " + true_neg);

		System.out.println();
		System.out.println("true_pos: " + true_pos + " -- true_pos + false_pos: "
				+ (false_pos + true_pos));
		System.out.println("true_pos: " + true_pos + " -- false_neg + true_pos: "
				+ (false_neg + true_pos));

		double precision = true_pos * 1.0 / (true_pos + false_pos);
		double recall = true_pos * 1.0 / (false_neg + true_pos);
		System.out.println("Precision: " + precision + " -- Recall: " + recall);
		double f_score = 2 * precision * recall / (precision + recall);
		System.out.println("F-Score: " + f_score);
		double accuracy = (true_pos + true_neg) * 1.0 / (true_pos + true_neg + false_pos + false_neg);
		System.out.println("Accuracy: " + accuracy);
		
		
		
		System.out.println();
		System.out.println("true_neg: " + true_neg + " -- true_neg + false_neg: "
				+ (true_neg + false_neg));
		System.out.println("true_neg: " + true_neg + " -- false_neg + true_pos: "
				+ (true_neg + false_pos));
		
		double n_precision = true_neg * 1.0 / (true_neg + false_neg);
		double n_recall = true_neg * 1.0 / (true_neg + false_pos);
		
		System.out.println("Neg_Precision: " + n_precision + " -- Ne_Recall: " + n_recall);
		double n_f_score = 2 * n_precision * n_recall / (n_precision + n_recall);
		System.out.println("Neg_F-score: " + n_f_score);
		
		System.out.println("\n--------------------");
	}

}
