package com.lexiscn;


import org.testng.annotations.Test;

public class Complete extends LexiscnWebDriver {
	@Test
	public void complete() {
		driver.close();
		driver.quit();
	}
	
	public static void main(String[] argv) {
		Complete complete = new Complete();
		complete.complete();
	}
}
