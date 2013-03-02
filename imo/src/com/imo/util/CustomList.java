package com.imo.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * ���Ƶ�Ĭ�ϴ�СΪ10������������������10ʱ��ɾ����һ��Ԫ��
 */
public class CustomList {
	private Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	private LinkedList<Integer> link = new LinkedList<Integer>();

	private int capacity = 100;

	private int count = capacity / 2;

	public CustomList(int capacity) {
		this.capacity = capacity;
		this.count = capacity / 2;
	}

	public boolean contains(Integer obj) {
		return map.containsKey(obj);
	}

	public void add(Integer obj) {
		if (map.size() >= capacity + count)
			for (int i = 0; i < count; i++) {
				map.remove(link.removeFirst());
			}
		map.put(obj, obj);
		link.add(obj);
	}

	public void clear() {
		map.clear();
		link.clear();
	}

	@Override
	public String toString() {
		String result = "";
		for (int i = 0; i < map.size(); i++) {
			result = result + "  " + link.get(i);
		}
		return result;
	}

}
