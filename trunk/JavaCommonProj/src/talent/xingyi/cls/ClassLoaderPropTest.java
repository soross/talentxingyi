package talent.xingyi.cls;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class ClassLoaderPropTest {

	public static void main(String[] args) throws IOException {
		ClassLoader iSysLoader = ClassLoader.getSystemClassLoader();
		System.out.println("系统类加载器:" + iSysLoader);

		Enumeration<URL> iEnumeration = iSysLoader.getResources("");
		while (iEnumeration.hasMoreElements()) {
			URL url = (URL) iEnumeration.nextElement();
			System.out.println(url);
		}
		
		ClassLoader iExClassLoader = iSysLoader.getParent();
		System.out.println("扩展类加载器:" + iExClassLoader);
		System.out.println("扩展类加载器路径:" + System.getProperty("java.ext.dirs"));
		System.out.println("扩展类加载器的parent:" + iExClassLoader.getParent());
	}

}
