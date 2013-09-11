package com.lexiscn;

import com.lexiscn.search.AdvancedSearch;

public class TestAdvancedSearch {

	public static void main(String[] args) {
		String a = "s";
		System.out.println(a);
		a = "aaaa";
		System.out.println(a);
		
		AdvancedSearch as = new AdvancedSearch();
		System.out.println(as.getDefaultSort());
	}

}
