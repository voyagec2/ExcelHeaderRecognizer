package core;

import java.util.HashMap;
import java.util.Map.Entry;

public class SimilarityHandler {
	
	private static HashMap<String,String> labelMap = null;
	
	/*
	 * 构造函数 
	 */
	public SimilarityHandler(HashMap<String,String> Header2Label) {		
		labelMap = Header2Label;
		//for (Entry<String, String> entry : labelMap.entrySet()) {		
		//	System.out.println( entry.getKey()+ " " + entry.getValue());
		//}
	}
	
	/*
	 * 获取跟当前文本最为相似的标签
	 * @return
	 * @param text
	 */
	public String getLabel(String text) {		
		double maxSimilarity = text.length()*10;
		String mostSimilarityOne = ""; 		
		for (Entry<String, String> entry : labelMap.entrySet()) {			
			double similarity = levenshteinSimilarity(text, entry.getKey());		    
		    if (similarity < maxSimilarity) {
		    	maxSimilarity = similarity;
		    	mostSimilarityOne = entry.getValue(); 
		    }		  
		}  		
		return mostSimilarityOne;		
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
	 * @return
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
		return distance_matrix[m][n];
	}	
}
