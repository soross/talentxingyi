package talent.xingyi.collection;

import java.util.*;

/**
 * Description: <br/>
 * Copyright (C), 2005-2008, Yeeku.H.Lee <br/>
 * This program is protected by copyright laws. <br/>
 * Program Name: <br/>
 * Date:
 * 
 * @author Yeeku.H.Lee kongyeeku@163.com
 * @version 1.0
 */
public class TestIteratorError {
	public static void main(String[] args) {
		// 创建一个集合
		Collection books = new HashSet();
		books.add("轻量级J2EE企业应用实战");
		books.add("Struts2权威指南");
		// 获取books集合对应的迭代器
		Iterator it = books.iterator();
		while (it.hasNext()) {
			String book = (String) it.next();
			System.out.println(book);
			if (book.equals("Struts2权威指南")) {
				// 使用Iterator迭代过程中，不可修改集合元素,下面代码引发异常
				books.remove(book);
			}
		}
	}
}
