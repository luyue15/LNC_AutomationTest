package com.lexiscn;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.lexiscn.Config;

public class TestConfig {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Config config = new Config();
		System.out.println(config);
		System.out.println(config.getHost());
		System.out.println(config.getUsername());
	}

	public void readConfig() {

		File configFile = new File(System.getProperty("user.dir")+"/config.xml");
		SAXReader xmlReader = new SAXReader();
	    Document doc = null;
		try {
			doc = xmlReader.read(configFile);
		} catch (DocumentException e) {
			Logger logger = LogManager.getLogger(Test.class.getName());
			logger.error("config file not found");
			System.exit(1);
		}
	    
	    Map<String, Object> config = new HashMap<String, Object>();
	    
	    Element rootEle = doc.getRootElement();
	    // 获取环境配置
	    config.put("environment", rootEle.element("environment").getText());
	    
	    // 获取浏览器类型配置
	    config.put("browser", rootEle.element("browser").getText());

	    // PHPSESSID
	    config.put("phpsessid", rootEle.element("phpsessid").getText());
	    
	    // uuid
	    config.put("uuid", rootEle.element("uuid").getText());
	    
	    // 获取所有环境参数
	    Map<String, String> envMap = new HashMap<String, String>();
	    @SuppressWarnings("unchecked")
		List<Element> eachEnvEle = rootEle.element("envs").elements("env");
	    for (Element eachEnv: eachEnvEle) {
	    	String envStr = eachEnv.attributeValue("name");
	    	String host = eachEnv.element("host").getText();
	    	String username = eachEnv.element("username").getText();
	    	String password = eachEnv.element("password").getText();
	    	envMap.put("host", host);
	    	envMap.put("username", username);
	    	envMap.put("password", password);
	    	config.put(envStr, envMap);
	    }
	    
	    System.out.println(config);
	    System.out.println(config.get("environment"));
	    System.out.println(config.get("phpsessid"));
	}
	
}
