package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ConfigHandler {
	private static Map<String,String> configMap = null;
	private static String configPath = "config.ini";	
	private static String userDir = System.getProperty("user.dir");
	
	public ConfigHandler() throws IOException {
		//---------------------从文件中读取配置----------------------------

		FileInputStream config = new FileInputStream(userDir + "\\"+ configPath);   
		InputStreamReader isr = new InputStreamReader(config, "UTF-8");   
		BufferedReader br = new BufferedReader(isr);   //构造一个BufferedReader类来读取文件
	
		
		System.out.println("开始读取配置文件信息:");
		String s = null;
		Map<String,String> map = new HashMap<String,String>();
		while((s = br.readLine())!=null){//使用readLine方法，一次读一行
			String[] info = null;
			info = s.split("=");
			if (info.length<2) continue;
			System.out.println(info[0]+" =  " + info[1]);
			map.put(info[0],info[1]);
		}	
		configMap = map;
	}
	
	/*
	 * 返回对应配置值
	 * @return
	 * @param text
	 */
	public String getConfig(String key) {	
		return configMap.get(key);		
	}
	
}
