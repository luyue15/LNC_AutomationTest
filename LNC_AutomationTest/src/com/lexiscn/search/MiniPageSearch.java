package com.lexiscn.search;

import org.testng.annotations.*;

public class MiniPageSearch extends AdvancedSearch {

	/**
	 * 高级搜索默认的排序方式
	 */
	private String defaultSort = null;
	
	@BeforeClass
	public void beforeClass() {
		defaultSort = getDefaultSort();
	}
	
	// TODO 写mini page搜索的测试方法
}
