package com.lexiscn;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class SearchDataProvider {

	private Logger logger = LogManager.getLogger(Config.class.getName());
	
	private String dataFilename = "";
	private HashMap<String, String[]> definedResults = new HashMap<String, String[]>();
	private HashMap<String, String> match = new HashMap<String, String>();
	private HashMap<String, String> searchWay = new HashMap<String, String>();
	private Object[][] data = null;

	public SearchDataProvider(String dataFilename) {
		this.dataFilename = dataFilename;
		initData();
	}
	
	/**
	 * 提供给testNG的DataProvider使用的数据
	 * @return Object[][]
	 */
	public Object[][] getDataProvider() {
		return data;
	}
	
	/**
	 * 从xml文件里面初始化数据
	 */
	protected void initData() {
		SAXReader xmlReader = new SAXReader();
	    Document doc = null;
		try {
			logger.info("reading file " + dataFilename);
			doc = xmlReader.read(new File(dataFilename));
		} catch (DocumentException e) {
			e.printStackTrace();
			logger.error(e.getStackTrace());
			logger.error("data file `" + dataFilename + " not found.");
			System.exit(1);
		}
		Element root = doc.getRootElement();
		
		@SuppressWarnings("unchecked")
		List<Element> eachData = root.elements("data");
		
		data = new Object[eachData.size()][2];

		for (int j=0; j<eachData.size(); j++) {
			// 读取每个data节点
			Element dataEle = eachData.get(j);
			
			// 读取data的match属性
			String dataMatch = dataEle.attributeValue("match");

			// 使用哪种方式搜索，直接使用URL，或者使用输入关键词
			String sw = dataEle.attributeValue("searchway");
			String elementName = null;
			if (null != sw && sw.equals("url")) {
				elementName = "url";
			} else {
				elementName = "keyword";
			}

			String keyword = dataEle.element(elementName).getText().trim();
			if ("" == keyword) {
				continue;
			}
			data[j] = new Object[] {keyword, ""};
			match.put(keyword, dataMatch);
			if (null != sw) {
				searchWay.put(keyword, sw.trim());
			}

			// 读取每个keyword的期望值
			@SuppressWarnings("unchecked")
			List<Element> allExpects = dataEle.elements("expect");
			if (allExpects.size() > 0) {
				String[] expect = new String[allExpects.size()];
				for (int i=0; i<allExpects.size(); i++) {
					expect[i] = allExpects.get(i).getText().trim();
				}
				definedResults.put(keyword, expect);
			}
		}
	}
	
	public String[] getExpect(String keyword) {
		return definedResults.get(keyword);
	}
	
	/**
	 * 取得在xml里面data节点里面定义的match属性值
	 * @param keyword
	 * @return String
	 */
	public String getMatch(String keyword) {
		return match.get(keyword);
	}
	
	public String getSearchWay(String keyword) {
		return searchWay.get(keyword);
	}
}
