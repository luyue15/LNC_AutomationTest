package com.lexiscn.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

public class TestMI {

	public static void main(String[] args) {
		TestMI tmi = new TestMI();
		// 读取口语化的词
		String[] spokenWords = tmi.getSpokenWords();
//		String[] csk = tmi.getCustomerSearchedKeywords();
		
		/*
		Map<String, String[]> csk = tmi.getGoogleSuggestion();
		for (int i=0; i<spokenWords.length; i++) {
			String[] googleSuggest = csk.get(spokenWords[i]);
			if (null != googleSuggest) {
				for (int j=0; j<googleSuggest.length; j++) {
					tmi.getMI(spokenWords[i], googleSuggest[j]);
				}
			}
		}
		*/
		
		tmi.testDictSrc(spokenWords);
		
		System.out.println("Done");
		tmi.closeWriter();
	}


    private String urlString = "http://10.123.4.210:8080/solr/termrelated/";
    private HttpSolrServer solr = null;
    private long total = 0;
    private SolrQuery query = new SolrQuery();
    private BufferedWriter bw;
    private BufferedWriter goodBw;
    private BufferedWriter betterBw;
    private BufferedWriter zeroBw;
    private Map<String, Long> keywordHits = new HashMap<String, Long>();
	
    public TestMI() {
        solr = new HttpSolrServer(urlString);
        
		query.setStart(0).setRows(0);
		// get total doc
		query.setQuery("*:*");
		total = getNumFound(solr, query);
System.out.println(total);
		Writer w;
		try {
			w = new FileWriter("C:/project/Oct Release 2013/output.txt");
			bw = new BufferedWriter(w);
			bw.write(total+"\n");
			goodBw = new BufferedWriter(
					new FileWriter("C:/project/Oct Release 2013/goodmi.txt"));
			betterBw = new BufferedWriter(
					new FileWriter("C:/project/Oct Release 2013/bettermi.txt"));
			zeroBw = new BufferedWriter(
					new FileWriter("C:/project/Oct Release 2013/zeromi.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public long[] getMI(String t1, String t2) {
		long tn1 = 0, tn2 = 0, n12 = 0;
		float corr = 0.0f;
		String term1="", term2="";

		if (keywordHits.get(t1) != null) {
			tn1 = keywordHits.get(t1).longValue();
		} else {
			term1 = StringUtils.join(daelKeyword(t1), "\" AND \"");
	    	query.setQuery("text:\"" + term1 + "\"");
			tn1 = getNumFound(solr, query);
			keywordHits.put(t1, new Long(tn1));
		}

		if (keywordHits.get(t2) != null) {
			tn2 = keywordHits.get(t2).longValue();
		} else {
			term2 = StringUtils.join(daelKeyword(t2), "\" AND \"");
			query.setQuery("text:\"" + term2 + "\"");
			tn2 = getNumFound(solr, query);
			keywordHits.put(t2, new Long(tn2));
		}
		
		if (keywordHits.get(t1+t2) != null) {
			n12 = keywordHits.get(t1+t2).longValue();
		} else {
			query.setQuery("text:\"" + term1 + "\" AND \"" + term2 + "\"");
			n12 = getNumFound(solr, query);
			keywordHits.put(t1+t2, new Long(n12));
		}
		if (tn1>0 && tn2>0 && (tn1+tn2+n12)>0) {
			try {
			corr = (float) ((Math.log10(total/tn1) * Math.log10(total/tn2) * n12) 
							 / (tn1 + tn2 + n12));
			} catch (ArithmeticException e) {}
		}
		String outputLine = corr + " - " + t1+"/"+tn1 + " - " + t2+"/"+tn2 + " - n12=" + n12 + "\n";
		try {
			bw.write(outputLine);
			bw.flush();
			if (corr > 0.1) {
				goodBw.write(outputLine);
				goodBw.flush();
			}
			if (corr > 1.0) {
				betterBw.write(outputLine);
				betterBw.flush();
			}
			if (tn1<=0 || tn2<=0 || n12<=0) {
				zeroBw.write(outputLine);
				zeroBw.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
System.out.print(outputLine);
		long[] ll = {tn1, tn2};

    	return ll;
    }
    
    protected String[] daelKeyword(String word) {
    	String[] ss = word.split(" ");
    	String[] ret = new String[ss.length];
    	for (int i=0; i<ss.length; i++) {
    		ret[i] = ss[i].replace("\"", "");
    	}
    	return ret;
    }
    
	private long getNumFound(HttpSolrServer server, SolrQuery query) {
		QueryResponse qrsp = null;
		SolrDocumentList docs = null;
		long num = 0;
		
		try {
			qrsp = server.query(query);
			docs = qrsp.getResults();
			num = docs.getNumFound();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return num;
	}
    
	/**
	 * 读取口语化的词
	 * @return
	 */
	public String[] getSpokenWords() {
		String[] ret = null;
		String excelFilename = "C:/project/Oct Release 2013/法律术语(20130716).xlsx";

		try {
			XSSFWorkbook xwb = new XSSFWorkbook(OPCPackage.open(excelFilename));
			XSSFRow row;
			
			//int sheetNum = xwb.getNumberOfSheets();
			// 暂时读取第一个sheet
			for (int i=0; i<1; i++) {
				XSSFSheet sheet = xwb.getSheetAt(i);
				int rowNum = sheet.getPhysicalNumberOfRows();
				ret = new String[rowNum];
				
				// 循环每一个sheet的每一行
				for (int j=sheet.getFirstRowNum(); j<sheet.getPhysicalNumberOfRows(); j++) {
					row = sheet.getRow(j);
					ret[j] = row.getCell(0).toString();
				}
			}
			
		} catch (InvalidFormatException | IOException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 * 读取数据库里面客户搜索过的关键词
	 * @return
	 */
	public String[] getCustomerSearchedKeywords() {
		String[] ret = null;
		String filename = "C:/project/Oct Release 2013/top_greater_5_keywords.txt";
		
		BufferedReader br = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (null != br) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		ret = new String[list.size()];
		list.toArray(ret);
		
		return ret;
	}
	
	public Map<String, String[]> getGoogleSuggestion() {
		BufferedReader br = null;
		String filename = "C:/project/Oct Release 2013/google_did_you_mean.txt";
		
		Map<String, String[]> googleSuggest = new HashMap<String, String[]>();
		try {
			br = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = br.readLine()) != null) {
				String[] key = line.split("\\|");
				if (key.length > 1 && !key[1].trim().equals("")) {
					String[] ss = key[1].split(",");
					String[] pureString = new String[ss.length];
					for (int i=0; i<ss.length; i++) {
						pureString[i] = ss[i].trim();
					}
					googleSuggest.put(key[0].trim(), pureString);
				} else {
					continue;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return googleSuggest;
	}
	
	public void closeWriter() {
		try {
			bw.close();
			goodBw.close();
			betterBw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 读取在spellchecker里面用到的那8万多的词语
	 * @return
	 */
	public String[] getDictSrc() {
		String filename = "c:/project/Oct Release 2013/dictsrc.txt";
		String[] ret = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = br.readLine()) != null) {
				list.add(line.trim());
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ret = new String[list.size()];
		list.toArray(ret);
		
		return ret;
	}
	public void testDictSrc(String[] verbals) {
		String[] dict = getDictSrc();
		for (int i=0; i<verbals.length; i++) {
			for (int j=0; j<dict.length; j++) {
				getMI(verbals[i], dict[j]);
			}
			break;
		}
	}
}
