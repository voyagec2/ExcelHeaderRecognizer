package core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import wordSeg.IKAnalyzerWordSeg;

public class SimilarityHandler {
	
	private static HashMap<String,String> headerTagMap = null;
	private static HashMap<String,Double> headerWeightMap = null;
	private static HashMap<String,List<String>> matchRecord = null;
	private static TagHandler TH = null;
	private static Word2Vec w2v = null;
	private static double cosDistanceWeight = 0.5;
	private static double editDistanceWeight = 0.5;
	
	/*
	 * 构造函数 
	 */
	public SimilarityHandler() throws IOException {	
		TH = new TagHandler();
		headerTagMap = TH.getHeaderTagMap();
		headerWeightMap = TH.getHeaderWeightMap();
		matchRecord = new HashMap<String,List<String>>();
		w2v = new Word2Vec();
	}
	
	/*
	 * 获取跟当前文本最为相似的表头、对应标签，以及相似度
	 * @return
	 * @param text
	 */
	public List<String> getMostLikelyHeader(String header) {
		if (matchRecord.containsKey(header)) return matchRecord.get(header);
		
		double maxSimilarity = 0;
		String mostSimilarityOne = ""; 	
		String tag = "";
		for (Entry<String, String> entry : headerTagMap.entrySet()) {	
		
			double similarity = calculateSimilarity(header, entry.getKey()) * headerWeightMap.get(entry.getKey());	
			System.out.println("计算“"+header+"”与“"+entry.getKey()+"”的相似度："+similarity);
		    if (similarity > maxSimilarity) {
		    	maxSimilarity = similarity ;
		    	mostSimilarityOne = entry.getKey(); 
		    	tag = entry.getValue();
		    }		  
		}  	
		List<String> res = new ArrayList<String>();
		res.add(mostSimilarityOne);
		res.add(tag);
		res.add(String.valueOf(maxSimilarity));		
		matchRecord.put(header, res);
		
		return res;		
	}
	
	
	/*
	 * 判断是否存在一样的标签
	 * @return
	 * @param text
	 */
	public boolean existLabel(String text) {	
		return headerTagMap.containsKey(text);		
	}
	
	/*
	 * 计算表头相似度
	 * @return double 0~1
	 * @param source target
	 */
	public double calculateSimilarity(String source, String target) {

		IKAnalyzerWordSeg wordSeg = new IKAnalyzerWordSeg();           

		List<String> sourceWordList = new ArrayList<String>();
		List<String> targetWordList = new ArrayList<String>();
		
        String words[] = wordSeg.segMore(source).get("智能切分").split(" ");
        for (int i = 0; i< words.length; i++) {
        	sourceWordList.add(words[i]);
        	//System.out.println(words[i]);
        }
        
        //System.out.println("--------------------------------------------");
        words = wordSeg.segMore(target).get("智能切分").split(" ");
        for (int i = 0; i< words.length; i++) {
        	targetWordList.add(words[i]);
        	//System.out.println(words[i]);
        }

        double similarity = 0;
        int upperSimilarity = 0;
        if (sourceWordList.size() > targetWordList.size())
        	upperSimilarity = sourceWordList.size();
        else
        	upperSimilarity = targetWordList.size();        
        while (sourceWordList.size()>0 && targetWordList.size()>0) {
        	double maxSimilarity = -1;
        	int sw = 0;
        	int tw = 0;
        	for (int i = 0; i< sourceWordList.size(); i++) {
        		String nowWord = sourceWordList.get(i);
        		for (int j = 0; j< targetWordList.size(); j++) {
        			double s1 = levenshteinSimilarity(nowWord, targetWordList.get(j));
        			double s2 = w2v.calSimilarity(nowWord, targetWordList.get(j), s1);
        			//double s3 = s1*cosDistanceWeight + s2*editDistanceWeight;
        			if (s2>maxSimilarity) {
            			maxSimilarity = s2;
                    	sw = i;
                    	tw = j;
            		}
        		}
        	}
        	//System.out.println(maxSimilarity+ " "+ sourceWordList.get(sw) + " " + targetWordList.get(tw));
            similarity = similarity + maxSimilarity;
        	sourceWordList.remove(sw);
        	if (tw>=0)
        		targetWordList.remove(tw);
        }        	
		return similarity / upperSimilarity;
	}
	
	
	/*
	 * 编辑距离
	 * @return double 0~1
	 * @param source target
	 */
	public double levenshteinSimilarity(String source, String target) {
		final int n = target.length();
		final int m = source.length();
		
		if(m == 0 )return m;
		if(n == 0)return n;
		
		int[][] distance_matrix = new int[m+1][n+1];
		distance_matrix[0][0] = 0;
		
		for(int i=0;i <= n;i++){
			distance_matrix[0][i] = i;
		}
		
		for(int j=0;j <= m;j++){
			distance_matrix[j][0] = j;
		}

		for(int i=1;i <= m;i++){
			char ci = source.charAt(i - 1); 
			for(int j=1;j <= n;j++){
				char cj = target.charAt(j - 1);
				int cost = (ci==cj) ? 0 : 2;
				distance_matrix[i][j] = Math.min(distance_matrix[i-1][j-1]+cost, Math.min(distance_matrix[i-1][j]+1, distance_matrix[i][j-1]+1));
			}
		}
		
		double Similarity = 0;
		if (distance_matrix[m][n] == m+n)    // 两字符串没有任何相等的字符，相似度为0 
			 Similarity = 0;
		else if (distance_matrix[m][n] == 0) // 两字符串完全相等，相似度为1
			 Similarity = 1;
		else 
			Similarity = 1 -  distance_matrix[m][n]*1.0 / (m+n);
		return Similarity;
	}	
	
	
}
