package id.co.babe.analysis.model;


import java.util.ArrayList;
import java.util.List;

public class ArticleDataset {
	

	public List<Article> train = new ArrayList<>();
	public List<Article> test = new ArrayList<>();
	
	public int test_pos = 0;
	public int test_neg = 0;
	
	public int train_pos = 0;
	public int train_neg = 0;
	
	public ArticleDataset() {
		this.train = new ArrayList<>();
		this.test = new ArrayList<>();
		this.test_neg = 0;
		this.test_pos = 0;
		this.train_neg = 0;
		this.train_pos = 0;
	}
	
	public void updateData(List<Article> data, double train_prob) {
		for(int i = 0 ; i < data.size() ; i ++) {
			double rand = Math.random();
			Article e = data.get(i);
			
			if(train_prob > rand) {
				train.add(e);
				if(e.label == Article.BAD) {
					train_pos ++;
				} else {
					train_neg ++;
				}
			} else {
				test.add(e);
				if(e.label == Article.BAD) {
					test_pos ++;
				} else {
					test_neg ++;
				}
			}
		}
	}


}
