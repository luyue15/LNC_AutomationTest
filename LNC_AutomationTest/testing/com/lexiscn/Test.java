package com.lexiscn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;

import com.lexiscn.LexiscnWebDriver;

public class Test extends LexiscnWebDriver {

	private Logger logger = LogManager.getLogger(Test.class.getName());
	
	/**
	 * @param args
	 * @throws DocumentException 
	 */
	public static void main(String[] args) {
		Test test = new Test();
		test.testDriverIntantial();
	}

	public void testDriverIntantial() {
		login();
		System.out.println("after login");
		System.out.println(driver.getCurrentUrl());
		goToHomepage();
	}
	
	public void hehe() {
		logger.error("呵呵");
	}
	
}
