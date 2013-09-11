package com.lexiscn.search;

import java.util.List;
import java.util.Set;

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

public class LandingPageSearch extends LexiscnWebDriver {

	private String mainWindowHandle = null;
	private String newWindwHandle = null;
	
	/**
	 *  data provider for article number recognition
	 */
	private SearchDataProvider anrdp = null;

	/**
	 * data provider for promulgator recognition
	 */
	private SearchDataProvider promulgatordp = null;
	
	/**
	 * data provider for search performance
	 */
	private SearchDataProvider spdp = null;
	
	public LandingPageSearch() {
		mainWindowHandle = driver.getWindowHandle();
		anrdp = new SearchDataProvider(System.getProperty("user.dir") + 
				"/data/" + config.getEnvironment() + 
				"/article_number_recognition_inside_data.xml");
		promulgatordp = new SearchDataProvider(System.getProperty("user.dir") + 
				"/data/" + config.getEnvironment() + 
				"/promulgator_recognition_data.xml");
		spdp = new SearchDataProvider(System.getProperty("user.dir") + 
				"/data/" + config.getEnvironment() + 
				"/landing_page_search_performance_data.xml");
	}

	@BeforeClass
	public void beforeClass() {
		login();
	}
	@BeforeMethod
	public void beforeMethod() {
		driver.get(config.getHost()+"/lnc/landing.php");
	}
	@AfterClass
	public void afterClass() {
		Set<String> handles = driver.getWindowHandles();
		for (String handle: handles) {
			if (!mainWindowHandle.equals(handle)) {
				driver.switchTo().window(handle);
				driver.close();
			}
		}
		driver.switchTo().window(mainWindowHandle);
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
	 * 测试landing page搜索的性能
	 * @param keyword
	 * @param drop
	 */
	@Test(dataProvider = "spDP")
	public void searchPerformance(String keyword, String drop) {
		Assert.assertTrue(search(keyword));
		closePopupAndSwitchBackWindow();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 在landing page执行搜索动作
	 * @param keyword
	 * @return
	 */
	public Boolean search(String keyword) {
		driver.switchTo().window(mainWindowHandle);
		WebElement keywordBox = null;
		try {
			keywordBox = driver.findElement(By.id("headq"));
			keywordBox.clear();
			keywordBox.sendKeys(keyword + Keys.ENTER);

			Set<String> windowHandles = driver.getWindowHandles();
			for (String handle: windowHandles) {
				if (!mainWindowHandle.equals(handle)) {
					newWindwHandle = handle;
					driver.switchTo().window(handle);
					break;
				}
			}

			// 使用wait来等页面的加载完成
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
	 * 执行完搜索动作后，取结果列表
	 * @return
	 */
	public String[] getResults() {
		String[] titles = null;
		if (newWindwHandle.equals(null)) {
			return titles;
		}
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
		
		// 获取完搜索结果，把当前新打开的窗口关闭
		closePopupAndSwitchBackWindow();
		
		return titles;
	}
	
	protected void closePopupAndSwitchBackWindow() {
		// 获取完搜索结果，把当前新打开的窗口关闭
		driver.close();
		// 切换回landing page窗口
		driver.switchTo().window(mainWindowHandle);
		
	}

	@DataProvider(name = "anrDP")
	public Object[][] articleNumberRecognitionDataProvider() {
		return anrdp.getDataProvider();
	}

	@DataProvider(name = "promulgatorDP")
	public Object[][] promulgatorRecognitionDataProvider() {
		return promulgatordp.getDataProvider();
	}
	
	@DataProvider(name = "spDP")
	public Object[][] searchPerformanceDataProvider() {
		return spdp.getDataProvider();
	}
	
}
