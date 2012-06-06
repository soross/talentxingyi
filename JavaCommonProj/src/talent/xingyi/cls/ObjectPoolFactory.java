package talent.xingyi.cls;

import java.util.*;
import java.io.*;

public class ObjectPoolFactory {
	// ����һ�������,ǰ���Ƕ�������������ʵ�ʶ���
	private Map<String, Object> objectPool = new HashMap<String, Object>();

	// ����һ����������ķ�����
	// �÷���ֻҪ����һ���ַ���������������Ը��ݸ���������Java����
	private Object createObject(String clazzName)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		// �����ַ�������ȡ��Ӧ��Class����
		Class<?> clazz = Class.forName(clazzName);
		// ʹ��clazz��Ӧ���Ĭ�Ϲ���������ʵ��
		return clazz.newInstance();
	}

	// �÷�������ָ���ļ�����ʼ������أ�
	// ������������ļ�����������
	public void initPool(String fileName) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fileName);
			Properties props = new Properties();
			props.load(fis);
			for (String name : props.stringPropertyNames()) {
				// ÿȡ��һ��������������ֵ�ԣ��͸�������ֵ����һ������
				// ����createObject�������󣬲���������ӵ��������
				objectPool.put(name, createObject(props.getProperty(name)));
			}

		} catch (IOException ex) {
			System.out.println("��ȡ" + fileName + "�쳣");
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public Object getObject(String name) {
		// ��objectPool��ȡ��ָ��name��Ӧ�Ķ���
		return objectPool.get(name);
	}

	public static void main(String[] args) throws Exception {
		ObjectPoolFactory pf = new ObjectPoolFactory();
		pf.initPool("obj.txt");
		System.out.println(pf.getObject("a"));
	}
}
