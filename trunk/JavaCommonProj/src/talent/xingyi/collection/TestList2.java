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
class PP {
	public boolean equals(Object obj) {
		return true;
	}
}

public class TestList2 {
	public static void main(String[] args) {
		List books = new ArrayList();
		books.add(new String("轻量级J2EE企业应用实战"));
		books.add(new String("Struts2权威指南"));
		books.add(new String("基于J2EE的Ajax宝典"));
		System.out.println(books);
		// 删除集合中A对象，将导致第一个元素被删除
		books.remove(new PP());
		System.out.println(books);
		// 删除集合中A对象，再次删除集合中第一个元素
		books.remove(new PP());
		System.out.println(books);
	}
}
