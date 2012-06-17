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
class Test implements Comparable {
	int count;

	public Test(int count) {
		this.count = count;
	}

	public String toString() {
		return "Test(count属性:" + count + ")";
	}

	public boolean equals(Object obj) {
		if (obj instanceof R) {
			Test r = (Test) obj;
			if (r.count == this.count) {
				return true;
			}
		}
		return false;
	}

	public int compareTo(Object obj) {
		Test r = (Test) obj;
		if (this.count > r.count) {
			return 1;
		} else if (this.count == r.count) {
			return 0;
		} else {
			return -1;
		}
	}
}

public class TestTreeSet2 {
	public static void main(String[] args) {
		TreeSet ts = new TreeSet();
		ts.add(new Test(5));
		ts.add(new Test(-3));
		ts.add(new Test(9));
		ts.add(new Test(-2));
		// 打印TreeSet集合，集合元素是有序排列的
		System.out.println(ts);
		// 取出第一个元素
		Test first = (Test) ts.first();
		// 为第一个元素的count属性赋值
		first.count = 20;
		// 取出最后一个元素
		Test last = (Test) ts.last();
		// 为最后一个元素的count属性赋值，与倒数第二个元素count属性相同
		last.count = -2;
		// 再次输出count将看到TreeSet里的元素处于无序状态，且有重复元素
		System.out.println(ts);
		// 删除属性被改变的元素，删除失败
		ts.remove(new Test(-2));
		System.out.println(ts);
		// 删除属性没有改变的元素，删除成功
		ts.remove(new Test(5));
		System.out.println(ts);

		TreeSetTest();
	}

	private static void TreeSetTest() {
		TreeSet<Integer> ts = new TreeSet<Integer>();

		ts.add(6);
		ts.add(23);
		ts.add(-3);
		ts.add(50);
		ts.add(0);
		ts.add(-15);
		ts.add(66);
		System.out.println(ts);

		TreeSet<Integer> subts = new TreeSet<Integer>();
		ts.add(7);
		ts.add(22);
		ts.add(-100);
		ts.addAll(subts);
		System.out.println(ts);

		System.out.println(ts.ceiling(17));// >=

		System.out.println(ts.contains(78));
		System.out.println(ts.contains(50));

		System.out.println(ts.containsAll(subts));

		TreeSet<Integer> tscopy = new TreeSet<Integer>();
		tscopy.add(6);
		tscopy.add(23);
		tscopy.add(-3);
		tscopy.add(50);
		tscopy.add(0);
		tscopy.add(-15);
		tscopy.add(66);
		tscopy.add(7);
		tscopy.add(22);
		tscopy.add(-100);
		System.out.println(ts.equals(tscopy));

		// TreeSet<Integer> tscopy2 = new TreeSet<Integer>();
		// tscopy2.add(6);
		// ts.retainAll(tscopy2);
		// System.out.println(ts);

		System.out.println(ts.comparator());

		System.out.println("======Iterator=====");
		Iterator<Integer> iterator = ts.descendingIterator();
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}
		System.out.println("======Iterator=====");
		
		System.out.println(ts.descendingSet());
	}
}
