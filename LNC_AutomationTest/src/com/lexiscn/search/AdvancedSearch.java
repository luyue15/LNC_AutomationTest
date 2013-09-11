package com.lexiscn.search;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.lexiscn.LexiscnWebDriver;

public class AdvancedSearch extends LexiscnWebDriver {

	public String getDefaultSort() {
		String defaultSort = null;
		if (!isLogin()) {
			login();
		}
		driver.get(config.getHost()+"/customize/?s=0&a=all&m=landingSet&p=1#searchSet");
		List<WebElement> radios = driver.findElements(
				By.cssSelector("input[type=radio][name=set]"));
		for (WebElement radio: radios) {
			System.out.println(radio.isSelected());
			if (radio.isSelected()) {
				defaultSort = radio.getAttribute("value");
				break;
			}
		}
		return defaultSort;
	}
	
}
