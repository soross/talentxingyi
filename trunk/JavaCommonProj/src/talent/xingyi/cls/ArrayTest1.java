package talent.xingyi.cls;

import java.lang.reflect.*;

/**
 * Description: <br/>
 * Copyright (C), 2008-2010, Yeeku.H.Lee <br/>
 * This program is protected by copyright laws. <br/>
 * Program Name: <br/>
 * Date:
 * 
 * @author Yeeku.H.Lee kongyeeku@163.com
 * @version 1.0
 */

public class ArrayTest1 {
	public static void main(String args[]) {
		try {
			// 创建一个元素类型为String ，长度为10的数组
			Object arr = Array.newInstance(String.class, 10);
			// 依次为arr数组中index为5、6的元素赋值
			Array.set(arr, 5, "Struts2权威指南");
			Array.set(arr, 6, "ROR敏捷开发最佳实践");
			// 依次取出arr数组中index为5、6的元素的值
			Object book1 = Array.get(arr, 5);
			Object book2 = Array.get(arr, 6);
			// 输出arr数组中index为5、6的元素
			System.out.println(book1);
			System.out.println(book2);
		} catch (Throwable e) {
			System.err.println(e);
		}
	}
}