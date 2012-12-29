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
public class TestSort {
	public static void main(String[] args) {
		ArrayList nums = new ArrayList();
		nums.add(2);
		nums.add(-5);
		nums.add(3);
		nums.add(0);
		// ���:[2, -5, 3, 0]
		System.out.println(nums);
		// ��List����Ԫ�صĴ���ת
		Collections.reverse(nums);
		// ���:[0, 3, -5, 2]
		System.out.println(nums);
		// ��List����Ԫ�صİ���Ȼ˳������
		Collections.sort(nums);
		// ���:[-5, 0, 2, 3]
		System.out.println(nums);
		// ��List����Ԫ�صİ����˳������
		Collections.shuffle(nums);
		// ÿ������Ĵ��򲻹̶�
		System.out.println(nums);
	}
}