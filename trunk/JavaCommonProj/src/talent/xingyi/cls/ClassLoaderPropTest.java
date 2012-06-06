package talent.xingyi.cls;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class ClassLoaderPropTest {

	public static void main(String[] args) throws IOException {
		ClassLoader iSysLoader = ClassLoader.getSystemClassLoader();
		System.out.println("ϵͳ�������:" + iSysLoader);

		Enumeration<URL> iEnumeration = iSysLoader.getResources("");
		while (iEnumeration.hasMoreElements()) {
			URL url = (URL) iEnumeration.nextElement();
			System.out.println(url);
		}
		
		ClassLoader iExClassLoader = iSysLoader.getParent();
		System.out.println("��չ�������:" + iExClassLoader);
		System.out.println("��չ�������·��:" + System.getProperty("java.ext.dirs"));
		System.out.println("��չ���������parent:" + iExClassLoader.getParent());
	}

}
