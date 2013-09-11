package com.lexiscn;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.lexiscn.LexiscnWebDriver;

public class Homepage extends LexiscnWebDriver {
	
	@Test
	public void newLegislationExpress() {
		goToHomepage();
		WebElement newlawListWrapper = driver.findElement(By.id("newlaw_list_wrapper"));
		List<WebElement> legislation = newlawListWrapper.findElements(By.xpath("//ul/li/section/p/a"));
		/*
		for (WebElement e: legislation) {
			System.out.println(e.getText());
		}
		*/
		Assert.assertEquals(legislation.size(), 5);
	}
	
	@Test
	public void testLogin() {
		logout();
		login();
		driver.get(config.getHost()+"/lnc/landing.php");
		String curUrl = driver.getCurrentUrl();
		Assert.assertTrue(-1 != curUrl.indexOf("/lnc/landing.php"));
	}
}
