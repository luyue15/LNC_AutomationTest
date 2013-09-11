package com.lexiscn.search;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import org.testng.Assert;

import com.lexiscn.LexiscnWebDriver;
import com.lexiscn.SearchDataProvider;

public class HomepageSearch extends LexiscnWebDriver {
	
	/**
	 *  data provider for search
	 */
	private SearchDataProvider sdp = null;
	
	/**
	 * data provider for did you mean
	 */
	private SearchDataProvider dymdp = null;
	
	/**
	 *  data provider for article number recognition
	 */
	private SearchDataProvider anrdp = null;
	
	/**
	 * data provider for promulgator
	 */
	private SearchDataProvider promulgatordp = null;

	/**
	 * data provider for search should be zero result
	 */
	private SearchDataProvider ssbzdp = null;
	
	public HomepageSearch() {
		sdp = new SearchDataProvider(System.getProperty("user.dir") + 
				"/data/" + config.getEnvironment() + "/homepage_search_data.xml");
		dymdp = new SearchDataProvider(System.getProperty("user.dir") + 
				"/data/" + config.getEnvironment() + "/did_you_mean_data.xml");
		anrdp = new SearchDataProvider(System.getProperty("user.dir") + 
				"/data/" + config.getEnvironment() + "/article_number_recognition_homepage_data.xml");
		promulgatordp = new SearchDataProvider(System.getProperty("user.dir") + 
				"/data/" + config.getEnvironment() + "/promulgator_recognition_data.xml");
		ssbzdp = new SearchDataProvider(System.getProperty("user.dir") + 
				"/data/" + config.getEnvironment() + "/homepage_search_should_be_zero_data.xml");
	}
	
	
	@BeforeClass
	public void before() {
		goToHomepage();
	}
	
	@Test(dataProvider = "searchDP")
	public void searchTopIsExpect(String keyword, String drop) {
		Assert.assertTrue(search(keyword));
		String[] res = getResults();
		String[] expect = sdp.getExpect(keyword);
		if ("exact" == sdp.getMatch(keyword)) {
			Assert.assertEquals(res, expect);
		} else {
			for (int i=0; i<expect.length; i++) {
				Assert.assertEquals(res[i], expect[i]);
			}
		}
	}
	
	@Test(dataProvider = "dymDP")
	public void didYouMean(String keyword, String drop) {
		Assert.assertTrue(search(keyword));
		String[] suggestion = getDidYouMeanSuggestion();
		String[] expect = dymdp.getExpect(keyword);
		if ("exact" == dymdp.getMatch(keyword)) {
			Assert.assertEquals(suggestion, expect);
		} else {
			for (int i=0; i<expect.length; i++) {
				Assert.assertEquals(suggestion[i], expect[i]);
			}
		}
	}
	
	/**
	 * 测试文号的识别
	 * @param keyword
	 * @param drop
	 */
	@Test(dataProvider = "anrDP")
	public void articleNumberRecognition(String keyword, String drop) {
		Assert.assertTrue(search(keyword));
		String[] res = getResults();
		String[] expect = anrdp.getExpect(keyword);
		if ("exact" == anrdp.getMatch(keyword)) {
			Assert.assertEquals(res, expect);
		} else {
			for (int i=0; i<expect.length; i++) {
				Assert.assertEquals(res[i], expect[i]);
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
	 * 测试那些搜索结果应该为零的关键词或者链接
	 * @param keyword
	 * @param drop
	 */
	@Test(dataProvider = "ssbzDP")
	public void searchShouldBeZero(String keyword, String drop) {
		if (ssbzdp.getSearchWay(keyword).equals("url")) {
			driver.get(config.getHost()+keyword);
			Assert.assertTrue(0 == getResults().length);
		} else {
			Assert.assertTrue(search(keyword));
			String[] res = getResults();
			String[] expect = ssbzdp.getExpect(keyword);
			if ("exact" == ssbzdp.getMatch(keyword)) {
				Assert.assertEquals(res, expect);
			} else {
				for (int i=0; i<expect.length; i++) {
					Assert.assertEquals(res[i], expect[i]);
				}
			}
		}
	}
	
	
	/**
	 * 在homepage的搜索框里面输入关键词
	 * @param keyword
	 * @return
	 */
	public Boolean search(String keyword) {
		keyword = keyword.trim();
		if (0 == keyword.length()) {
			return false;
		}
		WebElement searchBox = null;
		try {
			searchBox = driver.findElement(By.id("search_keyword"));
			searchBox.clear();
			searchBox.click();
			searchBox.sendKeys(keyword+Keys.ENTER);
			// 使用wait来等页面的加载完成
			wait.until(ExpectedConditions.presenceOfElementLocated(
					By.id("search_result_wrapper")));
		} catch (UnhandledAlertException | TimeoutException | NoSuchElementException e) {
			e.printStackTrace();
			logger.error(e.getStackTrace().toString());
			return false;
		}
		return true;
	}
	
	/**
	 * 返回搜索结果，调用前需要调用search
	 * @return String[]
	 */
	public String[] getResults() {
		String[] titles = null;
		try {
			WebElement searchResultWrapper = driver.findElement(
					By.id("search_result_wrapper"));
			List<WebElement> links = searchResultWrapper.findElements(
					By.cssSelector("ul.legal_list div.hl_area_1 a"));
			
			titles = new String[links.size()];
			for (int i=0; i<links.size(); i++) {
				titles[i] = links.get(i).getText();
			}
		} catch (NoSuchElementException e) {}
		
		return titles;
	}
	
	public String[] getDidYouMeanSuggestion() {
		String[] suggestion = null;
		
		try {
			WebElement suggestionEle = driver.findElement(By.id("suggestion"));
			List<WebElement> links = suggestionEle.findElements(
					By.cssSelector("a.suggest-item"));
			
			suggestion = new String[links.size()];
			for (int i=0; i<suggestion.length; i++) {
				suggestion[i] = links.get(i).getText().trim();
			}
		} catch (NoSuchElementException e) {
		} catch (ElementNotVisibleException e) {}
		
		return suggestion;
	}

	
	@DataProvider(name = "searchDP")
	public Object[][] searchTopIsExpectDataProvider() {
		return sdp.getDataProvider();
	}

	@DataProvider(name = "dymDP")
	public Object[][] didYouMeanDataProvider() {
		return dymdp.getDataProvider();
	}
	
	@DataProvider(name = "anrDP")
	public Object[][] articleNumberRecognitionDataProvider() {
		return anrdp.getDataProvider();
	}

	@DataProvider(name = "promulgatorDP")
	public Object[][] promulgatorRecognitionDataProvider() {
		return promulgatordp.getDataProvider();
	}
	
	@DataProvider(name = "ssbzDP")
	public Object[][] searchShouldBeZeroDataProvider() {
		return ssbzdp.getDataProvider();
	}

}
