package core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Word2Vec {
	private static String dataPath = "vectors.bin";
	private static String userDir = System.getProperty("user.dir");
	private static Map<String, Vector<Double>> W2VMap = new HashMap<>();  //文本词汇到词向量的映射
	
	public Word2Vec() {	
        try {
        	System.out.println("开始加载词向量：");
            FileReader fR = new FileReader(userDir+"\\"+ dataPath);
            BufferedReader brTrain = new BufferedReader(fR);
            String str = null;

            str = brTrain.readLine();
            int VecDim = 0;

            if(str != null){
                String[] strs = str.split(" ");
                VecDim = Integer.parseInt(strs[1]);
            }
            int total=0;
            while((str = brTrain.readLine()) != null) {
                String[] w2v = str.split(" ");

                String s = w2v[0];
                Vector<Double> vec = new Vector<>();
                for(int i = 1; i < VecDim + 1; i++) {
                    vec.add(Double.parseDouble(w2v[i]));
                }
                W2VMap.put(s, vec);
                total++;                                
            }
            System.out.println("加载词向量成功，共计"+total+"个词汇。");
            brTrain.close();
            fR.close();

        }
        catch (Exception ex){
            System.out.println("加载词向量异常");
        }

    }
    
	public double calSimilarity(String word1, String word2, double editDistance) {
		
		Vector<Double> vec1 = W2VMap.get(word1);
		Vector<Double> vec2 = W2VMap.get(word2);
		
		if(vec1 == null || vec1.isEmpty() || vec2 == null || vec2.isEmpty()) {
			return editDistance;      //如果有其中一个不在词典中，因无法计算，直接返回编辑距离的值
		}
		
		double molecular = 0;
		for (int i = 0 ;i < vec1.size(); i++) 
			molecular += vec1.get(i) * vec2.get(i);
		
		double denominator = 1;
		double multi1 = 0;
		double multi2 = 0;
		for (int i = 0; i< vec1.size(); i++) {			
			multi1 += vec1.get(i) * vec1.get(i);
			multi2 += vec2.get(i) * vec2.get(i);			
		}
		
		denominator *= Math.sqrt(multi1) * Math.sqrt(multi2);
		return molecular / denominator;   //余弦相似度
		
	}
}
