package com.lexiscn;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Config {
	
	private Map<String, Object> config = null;

	private Logger logger = LogManager.getLogger(Config.class.getName());
	
	public Config() {
		File configFile = new File(System.getProperty("user.dir")+"/config/config.xml");
		SAXReader xmlReader = new SAXReader();
	    Document doc = null;
		try {
			doc = xmlReader.read(configFile);
		} catch (DocumentException e) {
			logger.error("config file not found.");
			System.exit(1);
		}
	    
	    config = new HashMap<String, Object>();
	    
	    Element rootEle = doc.getRootElement();
	    
	    // 获取浏览器类型配置
	    config.put("browser", rootEle.element("browser").getText().trim());

	    // PHPSESSID
	    config.put("phpsessid", rootEle.element("phpsessid").getText().trim());
	    
	    // uuid
	    config.put("uuid", rootEle.element("uuid").getText().trim());
	    
	    // timeout
	    config.put("timeout", rootEle.element("timeout").getText().trim());
	    
	    @SuppressWarnings("unchecked")
		List<Element> eachEnvEle = rootEle.element("envs").elements("env");
	    String[] envs = new String[eachEnvEle.size()];
	    for (int i=0; i<eachEnvEle.size(); i++) {
	    	String envStr = eachEnvEle.get(i).attributeValue("name");
	    	envs[i] = envStr;

		    // 获取所有环境参数
		    Map<String, String> envMap = new HashMap<String, String>();
	    	envMap.put("host", eachEnvEle.get(i).element("host").getText());
	    	envMap.put("username", eachEnvEle.get(i).element("username").getText());
	    	envMap.put("password", eachEnvEle.get(i).element("password").getText());
	    	config.put(envStr, envMap);
	    }

	    // 获取环境配置，以外部的参数为准。没有外部的参数，就使用配置文件里面的
	    String defautEnv = rootEle.element("environment").getText().trim();
	    String cmdLineEnv = System.getProperty("commandline.environment");
	    if (ArrayUtils.contains(envs, cmdLineEnv)) {
		    config.put("environment", cmdLineEnv.trim());
	    } else {
		    config.put("environment", defautEnv);
	    }
	}
	
	public String getEnvironment() {
		return (String) config.get("environment");
	}
	
	public String getBrowserName() {
		return (String) config.get("browser");
	}
	
	public String getPHPSESSID() {
		return (String) config.get("phpsessid");
	}
	
	public String getUUID() {
		return (String) config.get("uuid");
	}
	
	public Long getTimeout() {
		return Long.valueOf(config.get("timeout").toString());
	}
	
	@SuppressWarnings("unchecked")
	public String getHost() {
		return (String) ((Map<String, Object>) config.get(getEnvironment())).get("host");
	}

	@SuppressWarnings("unchecked")
	public String getUsername() {
		return (String) ((Map<String, Object>) config.get(getEnvironment())).get("username");
	}
	
	@SuppressWarnings("unchecked")
	public String getPassword() {
		return (String) ((Map<String, Object>) config.get(getEnvironment())).get("password");
	}
}
