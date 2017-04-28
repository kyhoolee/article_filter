package id.co.babe.analysis.filter;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import id.co.babe.analysis.model.Article;
import id.co.babe.analysis.util.Utils;

public class FeatureCalculator {
	public static final int f_title_len = 0;
	public static final int f_title_word = 1;
	public static final int f_sub_len = 2;
	public static final int f_sub_word = 3;
	public static final int f_doc_len = 4;
	public static final int f_doc_word = 5;
	public static final int f_sent_len = 6;
	public static final int f_sent_word = 7;
	public static final int f_sent_num = 8;
	public static final int f_parag_len = 9;
	public static final int f_parag_word = 10;
	public static final int f_parag_num = 11;
	public static final int f_image_num = 12;
	
	public static class ArticlePart {
		public String title;
		public String content;
		public List<String> parags;
		public List<String> sentences;
		public List<String> images;
	}
	public static int[] getFeatureIndces() {
		int[] result = new int[Article.num_feature];
		for(int i = 0 ; i < Article.num_feature; i ++) {
			result[i] = i;
		}
		return result;
	}
	public static double[] getFeatureValues(String article) {
		double[] result = new double[Article.num_feature];
		ArticlePart part = toArticlePart(article);
		
		updateTitle(result, part);
		
		updateSub(result, part);
		
		updateSent(result, part);
		
		updateParag(result, part);
		
		updateImage(result, part);
		
		updateDoc(result, part);
		
		
		
		return result;
	}
	
	public static void updateTitle(double[] result, ArticlePart part) {
		result[Article.f_title_len] = part.title.length();
		result[Article.f_title_word] = part.title.split("\\W+").length; //s.split("\\W+");
	}
	
	public static void updateSub(double[] result, ArticlePart part) {
		result[Article.f_sub_len] = 0;
		result[Article.f_sub_word] = 0;
		
	}
	
	public static void updateDoc(double[] result, ArticlePart part) {
		result[Article.f_doc_len] = 0;
		result[Article.f_doc_word] = 0;
		for(int i = 0 ; i < part.parags.size() ; i ++) {
			String p = part.parags.get(i);
			result[Article.f_doc_len] += p.length();
			result[Article.f_doc_word] = p.split("\\W+").length;
		}
	}
	
	public static void updateSent(double[] result, ArticlePart part) {
		result[Article.f_sent_len] = 0;
		result[Article.f_sent_word] = 0;
		result[Article.f_sent_num] = part.sentences.size();
		
		for(int i = 0 ; i < part.sentences.size() ; i ++) {
			String s = part.sentences.get(i);
			result[Article.f_sent_word] += s.split("\\W+").length;
			result[Article.f_sent_len] += s.length();
		}
		
		result[Article.f_sent_len] *= 1.0 / part.sentences.size();
		result[Article.f_sent_word] *= 1.0 / part.sentences.size();
		
	}
	
	public static void updateParag(double[] result, ArticlePart part) {
		result[Article.f_parag_len] = 0;
		result[Article.f_parag_word] = 0;
		result[Article.f_parag_num] = part.parags.size();
		
		for(int i = 0 ; i < part.parags.size() ; i ++) {
			String s = part.parags.get(i);
			result[Article.f_parag_word] += s.split("\\W+").length;
			result[Article.f_parag_len] += s.length();
		}
		
		result[Article.f_parag_len] *= 1.0 / part.parags.size();
		result[Article.f_parag_word] *= 1.0 / part.parags.size();
	}
	
	public static void updateImage(double[] result, ArticlePart part) {
		result[Article.f_image_num] = part.images.size();
	}
	
	
	
	public static ArticlePart toArticlePart(String article) {
		ArticlePart result = new ArticlePart();
		
		String[] titleBody = article.split(Article.sep_title);
		if(titleBody.length <= 1)
			return result;
		
		result.title = titleBody[0];
		result.content = titleBody[1];
		
		String[] parags = result.content.split(Article.sep_parag);
		
		result.parags = new ArrayList<String>();
		result.sentences = new ArrayList<String>();
		result.images = new ArrayList<String>();
		
		System.out.println(parags.length);
		
		for(int i = 0 ; i < parags.length ; i ++) {
			String p = parags[i];
			if(!p.contains(Article.babe_asset)) {
				result.parags.add(p);
				List<String> sents = sentBreak(p);
				result.sentences.addAll(sents);
			} else {
				result.images.add(p);
			}
		}
		
		for(int i = 0 ; i < result.sentences.size() ; i ++) {
			System.out.println(result.sentences.get(i));
		}
		
		
		return result;
	}
	
	
	public static List<String> sentBreak(String para) {
		List<String> result = new ArrayList<String>();
		
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		String source = para;
		iterator.setText(source);
		int start = iterator.first();
		for (int end = iterator.next();
		    end != BreakIterator.DONE;
		    start = end, end = iterator.next()) {
			result.add(source.substring(start,end));
		}
		
		return result;
	}
	
	
	public static void main(String[] args) {
		String title = "Wow! Cat Kuku Cantik Ini Terbuat dari Cokelat dan Bisa Dimakan";
		String article = "Setiap wanita pastinya selalu memperhatikan penampilan dan kecantikkan yang ada pada dirinya. Mulai dari ujung kepala sampai ujung kaki selalu diberikan tampilan yang sempurna. Salah satunya yang selalu identik dengan perempuan, yaitu **cat kuku** yang memiliki warna cantik dan menarik.\r\n\r\nCat kuku mungkin tidak bisa lepas dari perempuan. Biasanya, cat kuku itu dibuat dari bahan-bahan kimia. Tetapi, kali ini ada sebuah cat kuku yang tidak terbuat dari bahan kimia, melainkan **cokelat** dan uniknya bisa dimakan.\r\n\r\n![Wow! Cat Kuku Cantik Ini Terbuat dari Cokelat dan Bisa Dimakan_2](https://assets.babe.news/assets/cache/0/0//gallery/82ad9d189da4e3384ba08ec01819aabd/2016/12/23/wow-cat-kuku-cantik-ini-terbuat-dari-cokelat-dan-bisa-dimakan-2.jpeg)\r\n\r\nDilansir dari _asiantown.net_, cat kuku cokelat ini dibuat oleh seorang pecinta _nail art_, **Jessie Mills** yang berasal dari **New Zealand**. Jessie Mills memang dikenal sebagai pecinta cat kuku. Ia sering berbagi mengenai tips dan trik bagaimana mengecat kuku yang baik di media sosialnya, yakni Facebook, Instagram, dan YouTube.\r\n\r\nSebelum cat kuku dari cokelat, Jessie telah membagikan _tutorial_ cat kuku yang unik, seperti konsep Sushi, McDonald’s, es krim, hingga burger. Dan baru-baru ini, Jessie telah membagikan _tutorial_ cat kuku dari cokelat yang menarik juga mengesankan.\r\n\r\n![Wow! Cat Kuku Cantik Ini Terbuat dari Cokelat dan Bisa Dimakan_1.jpg](https://assets.babe.news/assets/cache/0/0//gallery/82ad9d189da4e3384ba08ec01819aabd/2016/12/23/wow-cat-kuku-cantik-ini-terbuat-dari-cokelat-dan-bisa-dimakan-1-jpg.png)\r\n\r\nDi saluran YouTube miliknya, Jessie membagikan _tutorial_ cat kuku cokelatnya dan bahan-bahan apa saja yang diperlukan. Untuk membuat cat kuku unik tersebut, Jessie membutuhkan beberapa cokelat putih dan dark cokelat serta warna lainnya.\r\n\r\nAgar semakin cantik, Jessie juga menambahkan aksesoris pelengkap, seperti bola-bola silver dari berbagai makanan ringan. Hanya dalam beberapa menit, Jessie pun berhasil menyulap kukunya menjadi sangat cantik. Tak sampai di situ, Jessie juga menunjukkan bahwa cat kuku kreasinya bisa dimakan langsung.\r\n\r\nDari apa yang dibuat oleh Jessie benar-benar cantik dan mengesankan. Nah, apakah kamu juga tertarik untuk  membuat cat kuku yang bisa dimakan juga?";
		article = title + Article.sep_title + article;
		double[] result = getFeatureValues(article);
		Utils.printArray(result);
	}
	
	
	
	
	
	

}
