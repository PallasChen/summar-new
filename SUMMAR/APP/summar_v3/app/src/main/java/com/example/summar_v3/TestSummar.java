package com.example.summar_v3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import com.qianxinyao.analysis.jieba.keyword.Keyword;
import com.qianxinyao.analysis.jieba.keyword.TFIDFAnalyzer;

class Test {
	String article;
	String title;
	ArrayList<String> sentences;
	String summary;
	ArrayList<String> keyword;

	Test() {

	}

	Test(String a, String t) {
		this.article = a;
		this.title = t;
		// -----斷句
		sentences = new ArrayList<String>();
		sentences = split();
		for (int i = 0; i < sentences.size(); i++) {
			// System.out.println(sentences.get(i));
		}
		System.out.println("共" + sentences.size() + "行");
		// -----關鍵字分析
		int n = 5;// 要取幾個關鍵字
		keyword = new ArrayList<String>();
		keyword = keyword(n);
		// System.out.println();
		// -----語句評分
		int scores[] = set_score(sentences, keyword);
		for (int i = 0; i < scores.length; i++) {
			// System.out.print(scores[i] + " ");
		}
		// System.out.println();
		// -----目標語句分數
		ArrayList<Integer> targetindex = target_score(scores);
		Collections.sort(targetindex);
		for (int i = 0; i < targetindex.size(); i++) {
			// System.out.print(targetindex.get(i) + " ");
		}
		// -----摘要
		summary = summary(targetindex);
		// System.out.println(summary);
	}

	public ArrayList<String> split() {
		ArrayList<String> s = new ArrayList<String>();
		String[] sentences = this.article.split("，|。|？|！|；");// 文章斷句
		String temp = "";
		boolean run = false;// 是否執行
		for (int i = 0; i < sentences.length; i++) {
			if (sentences[i].contains("「")) {// 上引號
				temp = "";
				// System.out.println("第" + i + "行含有「");
				temp += sentences[i];
				run = true;// 開始執行
			} else if (sentences[i].contains("」")) {// 下引號
				// System.out.println("第" + i + "行含有」");
				String[] t = sentences[i].split("」");// 將下引號前後分割
				if (t.length > 1) {
					temp += t[0] + "」";
					s.add(temp);
					s.add(t[1]);
				} else {
					temp += "」";
					s.add(temp);
				}
				run = false;
			} else if (run == true) {// 是否繼續跑
				temp += sentences[i];
			} else {
				s.add(sentences[i]);
			}
		}
		return s;
	}

	public ArrayList<String> keyword(int n) {
		int topN = n;// 取前幾個出來
		ArrayList<String> temp = new ArrayList<String>();
		TFIDFAnalyzer tfidfAnalyzer = new TFIDFAnalyzer();
		List<Keyword> list = tfidfAnalyzer.analyze(this.article, topN);// 藉由上面的方法來分析何者為關鍵字
		for (int i = 0; i < list.size(); i++) {
			String t = list.get(i).getName();
			String mark[] = { "。", "、", "，", "！", "（", "）", "「", "」", "、", ":", ";", "?" };
			int count = 0;
			for (int j = 0; j < mark.length; j++) {
				if (t.contains(mark[j])) {
					count++;
				}
			}
			if (count == 0) {
				temp.add(t);
			}
		}
		return temp;
	}

	public int[] set_score(ArrayList s, ArrayList keyword) {
		int[] scores = new int[s.size()];
		for (int i = 0; i < scores.length; i++) {
			scores[i] = 0;
		}
		for (int i = 0; i < s.size(); i++) {
			int count = 0;
			JiebaSegmenter segmenter = new JiebaSegmenter();
			int n = segmenter.process(s.get(i) + "", SegMode.INDEX).size();
			for (int j = 0; j < n; j++) {
				String temp = segmenter.process(s.get(i) + "", SegMode.INDEX).get(j).toString().split(",")[0]
						.replace("[", "");
				for (int k = 0; k < keyword.size(); k++) {
					if (temp.equals(keyword.get(k))) {
						count++;
					}
				}
			}
			scores[i] = count;
		}
		return scores;
	}

	public ArrayList<Integer> target_score(int[] s) {
		ArrayList<Integer> temp = new ArrayList<Integer>();
		ArrayList<Integer> index = new ArrayList<Integer>();
		for (int i = 0; i < s.length; i++) {
			if (temp.contains(s[i])) {
				// 不做動作
			} else {
				temp.add(s[i]);
			}
		}
		Collections.sort(temp);
		Collections.reverse(temp);
		// 計算20%為比例
		int word_count_limit = (int) (this.sentences.size() * 0.2);
		int count_word = 0;
		// 字數上限為200
		int word_count_max = 200;
		int count_word_max = 0;
		for (int i = 0; i < temp.size(); i++) {
			for (int j = 0; j < s.length; j++) {
				if (temp.get(i) == s[j]) {
					// 判斷是否可繼續存取
					if (count_word_max > 200) {
						break;
					} else if (count_word > word_count_limit) {
						break;
					} else {
						count_word++;
						index.add(j);
						count_word_max += (this.sentences.get(j) + "").length();
					}
				}
			}
		}
		return index;
	}

	public String summary(ArrayList index) {
		summary = "";
		for (int i = 0; i < index.size(); i++) {
			if (i == index.size() - 1) {
				summary += this.sentences.get((int) index.get(i)) + "。";
			} else {
				summary += this.sentences.get((int) index.get(i)) + "，";
			}
		}
		return summary;
	}
}

public class TestSummar {
	public static void main(String[] args) {
		String article = "生涯十九年最後一次在天母球場出賽，中信兄弟隊四十一歲老將彭政閔昨天離場前，按照長年慣例向球場鞠躬，現場爆滿一萬名觀眾幾乎無人離席，不少人高舉他的應援毛巾，向這位中職史上人氣最高的球星致敬，彭政閔說：「心情很複雜。」兄弟、富邦悍將隊雙重賽首戰，彭政閔未上場，第二戰擔任先發第三棒指定打擊者，三個打數未有安打，八局下高飛犧牲打攻下全隊唯一一分，兄弟終場一比八落敗。賽後談到揮別天母球場的感覺，他說：「這場比賽沒表現好，對不起球迷。」兄弟總教練伯納九局上特別安排彭政閔改守一壘，完成兩個出局數後退場，接受滿場球迷歡呼；他走回休息區前，悍將隊一壘教練鄭兆行也趨前握手致意，向這位昔日中華隊老戰友說：「辛苦了。」彭政閔表示，謝謝伯納安排這個橋段，心情有點唐突，也非常感謝他，這是最後一次以球員身分站上天母球場守一壘。彭政閔對天母球場感情深厚，除了二○○二、○三年總冠軍賽，○八年還揮出中職史上第六千支全壘打，就連生涯首支再見安打也在天母，他說：「相信很多球迷和我一樣有美好回憶，十九年來在天母打了很多比賽，感謝大家一起努力吶喊。」天母告別戰結束後，爆滿球迷齊聲大喊「彭政閔」，不少人希望他達成兩百轟紀錄（差九支）再退休，彭政閔笑說：「我累了。」";
		String title = "中職／天母最後一戰 恰恰：對不起球迷";
		Test s = new Test(article, title);
		System.out.println("-----摘要結果-----");
		System.out.println(s.summary);
		System.out.println("-----關鍵字分析-----");
		for (int i = 0; i < s.keyword.size(); i++) {
			System.out.print(s.keyword.get(i) + " ");
		}
	}
}
