package com.lexiscn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.lexiscn.Config;

public class LexiscnWebDriver extends RemoteWebDriver {

	private static WebDriver globalDriver = null;
	private static Config globalConfig = null;
	protected WebDriver driver = null;
	protected WebDriverWait wait = null;
	protected Config config = null;
	
	protected Logger logger = LogManager.getLogger();
	
	public LexiscnWebDriver() {
		config = getConfig();
		driver = getWebDriver(config);
		wait = new WebDriverWait(driver, config.getTimeout());
		goToHomepage();
	}
	
	public static WebDriver getWebDriver(Config config) {
		if (null == globalDriver) {
			String browserName = config.getBrowserName().toLowerCase();
			if (-1 != browserName.indexOf("firefox")) {
				globalDriver = new FirefoxDriver();
			} else if (-1 != browserName.indexOf("chrome")) {
				// TODO 还没有测试chrome是否这样做，也许需要一个chrome server
				globalDriver = new ChromeDriver();
			} else {
				globalDriver = new FirefoxDriver();
			}
			// Window window = globalDriver.manage().window();
			// window.setPosition(new Point(0, 0));
			// window.setSize(new Dimension(1024, 768));
			globalDriver.get(config.getHost());
			
			globalDriver.manage().addCookie(new Cookie("PHPSESSID", config.getPHPSESSID()));
			globalDriver.manage().addCookie(new Cookie("uuid", config.getUUID()));
		}
		return globalDriver;
	}
	
	public static Config getConfig() {
		if (null == globalConfig) {
			globalConfig = new Config();
		}
		return globalConfig;
	}
	
	public void goToHomepage() {
		driver.get(config.getHost());
	}
	
	/**
	 * 执行登录动作
	 */
	public void login() {
		goToHomepage();
		try {
			WebElement usernameBox = driver.findElement(By.id("username"));
			usernameBox.click();
			usernameBox.clear();
			usernameBox.sendKeys(config.getUsername() + Keys.TAB);
			
			WebElement passwordBox = driver.findElement(By.id("password"));
			passwordBox.clear();
			passwordBox.sendKeys(config.getPassword());
			
			WebElement submitBtn = driver.findElement(By.id("btn_submit"));
			submitBtn.click();
		} catch (NoSuchElementException e) {
			WebElement intoSystemBtn = driver.findElement(By.cssSelector("#into_system span"));
			intoSystemBtn.click();
		}
		// 等待页面加载完成
		if (driver.getCurrentUrl().indexOf("/lnc/getprofile.php")!=-1) {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("footer")));
			// 如果还是停留在/lnc/getprofile.php页面，点击里面的跳过按钮，或者填写表单
			if (driver.getCurrentUrl().indexOf("/lnc/getprofile.php")!=-1) {
				// TODO 如果所有环境都加上了skip按钮，就可以只使用try里面的逻辑
				try {
					// wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("skip_btn")));
					WebElement skipBtn = driver.findElement(By.id("skip_btn"));
					skipBtn.click();
				} catch (NoSuchElementException e) {
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("type_select")));
					
					Select typeSelect = new Select(driver.findElement(By.id("type_select")));
					typeSelect.selectByIndex(1);
					
					Select jobTitleSelect = new Select(driver.findElement(By.id("job_title_select")));
					jobTitleSelect.selectByIndex(1);
					
					WebElement pracEle = driver.findElement(By.className("all_1"));
					pracEle.click();
					
					WebElement submitEle = driver.findElement(By.id("submit"));
					submitEle.click();
				}
			}
		}
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("footer")));
	}
	
	/**
	 * 判断是否已经登录系统
	 * @return Boolean
	 */
	public Boolean isLogin() {
		String source = driver.getPageSource();
		// 只需要判断页面里面是否有退出的链接即可
		if (source.contains("/lnc/user.php?act=logout")) {
			return true;
		}
		return false;
	}
	
	public void logout() {
		driver.get(config.getHost() + "/lnc/user.php?act=logout");
	}
}
