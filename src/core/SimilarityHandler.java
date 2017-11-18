package core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SimilarityHandler {
	
	private static Map<String,String> labelMap = null;
	
	/*
	 * 构造函数 
	 */
	public SimilarityHandler(Map<String,String> Header2Label) {		
		labelMap = Header2Label;
	}
	
	/*
	 * 获取跟当前文本最为相似的标签
	 * @return
	 * @param text
	 */
	public String getLabel(String text, double threshold) {		
		double maxSimilarty = 0;
		String mostSimilarityOne = ""; 		
		for (Entry<String, String> entry : labelMap.entrySet()) {			
			double similarity = levenshteinSimilarity(text, entry.getKey());		    
		    if (similarity > maxSimilarty) {
		    	maxSimilarty = similarity ;
		    	mostSimilarityOne = entry.getValue(); 
		    }		  
		}  	
		//System.out.println(text + " " + minEdit + " " + mostSimilarityOne + " "+text.length());
		if ( maxSimilarty < threshold) return "";
			else return mostSimilarityOne;		
	}
	
	/*
	 * 判断是否存在一样的标签
	 * @return
	 * @param text
	 */
	public boolean existLabel(String text) {	
		return labelMap.containsKey(text);		
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
