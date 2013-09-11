package com.lexiscn;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class TestDataProvider {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<Object> d = new ArrayList<Object>();
		Map<Object, Object[]> a = new Hashtable<Object, Object[]>();
		a.put("公司法", new Object[] {
				"最高人民法院关于适用《中华人民共和国公司法》若干问题的规定（一）", 
				"中华人民共和国公司法（2005年修订）", 
				"中华人民共和国公司法（2004年修订）", 
			});
		d.add(a);
		System.out.println(d);
		System.out.println(a.keySet().toArray()[0]);
	}

}
