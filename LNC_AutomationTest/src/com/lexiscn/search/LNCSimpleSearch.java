package com.lexiscn.search;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.*;

import com.lexiscn.LexiscnWebDriver;
import com.lexiscn.SearchDataProvider;

public class LNCSimpleSearch extends LexiscnWebDriver {

	/**
	 *  data provider for search
	 */
	private SearchDataProvider sdp = null;
	
	/**
	 *  data provider for article number recognition
	 */
	private SearchDataProvider anrdp = null;
	
	/**
	 * data provider for promulgator recognition
	 */
	private SearchDataProvider promulgatordp = null;
	
	public LNCSimpleSearch() {
		sdp = new SearchDataProvider(System.getProperty("user.dir") + 
				"/data/" + config.getEnvironment() + 
				"/simple_search_law_inside_data.xml");
		anrdp = new SearchDataProvider(System.getProperty("user.dir") + 
				"/data/" + config.getEnvironment() + 
				"/article_number_recognition_inside_data.xml");
		promulgatordp = new SearchDataProvider(System.getProperty("user.dir") + 
				"/data/" + config.getEnvironment() + 
				"/promulgator_recognition_data.xml");
	}
	
	@BeforeClass
	public void before() {
		login();
	}
	
	@BeforeMethod
	public void beforeMethod() {
		driver.get(config.getHost()+"/lnc/search/simple_search_form.php");
	}
	
	/**
	 * 测试一些普通的关键词，像公司法，刑法之类的。
	 * 主要为了测试这些关键词的搜索结果是否受到影响
	 * @param keyword
	 * @param drop
	 */
	@Test(dataProvider = "searchDP")
	public void testKeywords(String keyword, String drop) {
		Assert.assertTrue(search(keyword));
		String[] res = getResults();
		String[] expected = sdp.getExpect(keyword);
		if ("exact" == sdp.getMatch(keyword)) {
			Assert.assertEquals(res, expected);
		} else {
			for (int i=0; i<expected.length; i++) {
				Assert.assertEquals(res[i], expected[i]);
			}
		}
		
	}
	
	@Test(dataProvider = "anrDP")
	public void articleNumberRecognition(String keyword, String drop) {
		Assert.assertTrue(search(keyword));
		String[] res = getResults();
		String[] expected = anrdp.getExpect(keyword);
		if ("exact" == anrdp.getMatch(keyword)) {
			Assert.assertEquals(res, expected);
		} else {
			for (int i=0; i<expected.length; i++) {
				Assert.assertEquals(res[i], expected[i]);
			}
		}
	}
	
	/**
	 * 测试发文机关的识别
	 * @param keyword
	 * @param drop
	 */
	@Test(dataProvider = "promulgatorDP")
	public void promulgatorRecognition(String keyword, String drop) {
		Assert.assertTrue(search(keyword));
		String[] res = getResults();
		String[] expect = promulgatordp.getExpect(keyword);
		if ("exact" == promulgatordp.getMatch(keyword)) {
			Assert.assertEquals(res, expect);
		} else {
			for (int i=0; i<expect.length; i++) {
				Assert.assertEquals(res[i], expect[i]);
			}
		}
	}
	
	
	
	/**
	 * 输入关键词，执行搜索动作
	 * @param keyword
	 * @return Boolean
	 */
	public Boolean search(String keyword) {
		WebElement keywordBox = null;
		try {
			keywordBox = driver.findElement(By.id("keyword_dis"));
			keywordBox.clear();
			keywordBox.sendKeys(keyword + Keys.ENTER);
			// 使用wait来等页面的加载完成
			wait.until(ExpectedConditions.presenceOfElementLocated(
					By.id("keyword_dis")));
		} catch (UnhandledAlertException | TimeoutException | NoSuchElementException e) {
			e.printStackTrace();
			logger.error(e.getStackTrace().toString());
			return false;
		}
		
		return true;
	}

	/**
	 * 执行完搜索动作后，取结果列表
	 * @return
	 */
	public String[] getResults() {
		String[] titles = null;
		try {
			WebElement legalList = driver.findElement(
					By.id("legal_list"));
			List<WebElement> links = legalList.findElements(
					By.cssSelector("a.hl_area_1"));
			
			titles = new String[links.size()];
			for (int i=0; i<links.size(); i++) {
				titles[i] = links.get(i).getText();
			}
		} catch (NoSuchElementException e) {}
		
		return titles;
	}
	
	@DataProvider(name = "searchDP")
	public Object[][] searchDataProvider() {
		return sdp.getDataProvider();
	}

	@DataProvider(name = "anrDP")
	public Object[][] articleNumberRecognitionDataProvider() {
		return anrdp.getDataProvider();
	}

	@DataProvider(name = "promulgatorDP")
	public Object[][] promulgatorRecognitionDataProvider() {
		return promulgatordp.getDataProvider();
	}
	
}
