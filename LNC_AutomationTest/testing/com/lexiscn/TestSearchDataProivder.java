package com.lexiscn;

public class TestSearchDataProivder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Config config = new Config();
//		SearchDataProvider sdp = new SearchDataProvider(
//				System.getProperty("user.dir") + 
//				"/data/" + config.getEnvironment() + 
//				"/homepage_search_data.xml");
//		SearchDataProvider dymdp = new SearchDataProvider(
//				System.getProperty("user.dir") + 
//				"/data/" + config.getEnvironment() + 
//				"/did_you_mean_data.xml");
//		SearchDataProvider anrdp = new SearchDataProvider(
//				System.getProperty("user.dir") + 
//				"/data/" + config.getEnvironment() + 
//				"/homepage_article_number_recognition_data.xml");
		SearchDataProvider ssbzdp = new SearchDataProvider(
				System.getProperty("user.dir") + 
				"/data/" + config.getEnvironment() + 
				"/homepage_search_should_be_zero_data.xml");
		
		System.out.println(ssbzdp.getDataProvider());
		Object[][] a = ssbzdp.getDataProvider();
		for (int i=0; i<a.length; i++) {
			for (int j=0; j<a[i].length; j++) {
				System.out.println(a[i][j]);
			}
		}
	}

}

