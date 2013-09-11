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

public class ModuleSimpleSearch extends LexiscnWebDriver {

	/**
	 * data provider for article number recognition
	 */
	private SearchDataProvider anrdp = null;

	/**
	 * data provider for promulgator
	 */
	private SearchDataProvider promulgatordp = null;
	
	public ModuleSimpleSearch() {
		anrdp = new SearchDataProvider(System.getProperty("user.dir") + 
				"/data/" + config.getEnvironment() + 
				"/article_number_recognition_inside_data.xml");
		promulgatordp = new SearchDataProvider(System.getProperty("user.dir") + 
				"/data/" + config.getEnvironment() + 
				"/promulgator_recognition_data.xml");
	}
	
	@BeforeClass
	public void beforeClass() {
		login();
	}
	
	@BeforeMethod
	public void beforeMethod() {
		driver.get(config.getHost() + "/topic/simpleSearch.php?tps=tp");
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
	
	@Test(dataProvider = "promulgatorDP")
	public void promulgatorRecognition(String keyword, String drop) {
		Assert.assertTrue(search(keyword));
		String[] res = getResults();
		String[] expected = promulgatordp.getExpect(keyword);
		if ("exact" == promulgatordp.getMatch(keyword)) {
			Assert.assertEquals(res, expected);
		} else {
			for (int i=0; i<expected.length; i++) {
				Assert.assertEquals(res[i], expected[i]);
			}
		}
	}
	
	
	/**
	 * 执行搜索动作
	 * @param keyword
	 * @return Boolean
	 */
	public Boolean search(String keyword) {
		WebElement keywordBox = null;
		try {
			keywordBox = driver.findElement(By.id("keyword"));
			keywordBox.clear();
			keywordBox.sendKeys(keyword + Keys.ENTER);
			// 使用wait来等待搜索结果页面的加载完成
			wait.until(ExpectedConditions.presenceOfElementLocated(
					By.id("keyword")));
		} catch (UnhandledAlertException | TimeoutException | NoSuchElementException e) {
			e.printStackTrace();
			logger.error(e.getStackTrace().toString());
			return false;
		}
		
		return true;
	}
	
	/**
	 * 执行完搜索动作，获取搜索结果
	 * @return String[]
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
	
	@DataProvider(name = "anrDP")
	public Object[][] articleNumberRecognitionDataProvider() {
		return anrdp.getDataProvider();
	}
	
	@DataProvider(name = "promulgatorDP")
	public Object[][] promulgatorRecognitionDataProvider() {
		return promulgatordp.getDataProvider();
	}
	
}
