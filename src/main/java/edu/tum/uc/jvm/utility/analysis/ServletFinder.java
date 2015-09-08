package edu.tum.uc.jvm.utility.analysis;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;

public class ServletFinder {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException {

	String jarPath = "/home/vladi/pebble-2.6.4.war";
	JarFile jarFile = new JarFile(jarPath);

	Collection<String> mainServletClasses = getServletClasses(jarFile);

	mainServletClasses.forEach(System.out::println);

	jarFile.close();
    }

    private static Collection<String> getServletClasses(JarFile jarFile) throws IOException {
	String jarName = jarFile.getName().substring(jarFile.getName().lastIndexOf(File.separatorChar) + 1,
		jarFile.getName().lastIndexOf('.'));
	Collection<String> mainServletClasses = new ArrayList<String>();

	Enumeration<JarEntry> entryEnum = jarFile.entries();

	while (entryEnum.hasMoreElements()) {
	    JarEntry jarEntry = entryEnum.nextElement();
	    String name = jarEntry.getName();
	    if (name.endsWith(".class")) {
		String className = checkJarEntry(jarFile.getInputStream(jarEntry));
		if (className != null) {
		    mainServletClasses.add(className);
		}

	    } else if (name.endsWith(".jar")) {
		// extract jarEntry
		BufferedInputStream jarIS = new BufferedInputStream(jarFile.getInputStream(jarEntry));
		File file = new File("/tmp/" + jarName + "/" + jarEntry.getName());
		file.getParentFile().mkdirs();
		FileOutputStream fos = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int len;
		while ((len = jarIS.read(buffer)) > 0) {
		    fos.write(buffer, 0, len);
		}
		fos.close();
		jarIS.close();

		// recursively traverse
		if (file.exists()) {
		    mainServletClasses.addAll(getServletClasses(new JarFile(file)));
		}
	    }
	}

	return mainServletClasses;
    }

    private static String checkJarEntry(InputStream entryInputStream) {
	try {
	    ClassReader cr = new ClassReader(entryInputStream);
	    ClassNode cn = new ClassNode();
	    cr.accept(cn, 0);

	    boolean hasDoGet = false, hasDoPost = false;

	    for (MethodNode methodNode : (List<MethodNode>) cn.methods) {
		// first check for params then for name
		Type retType = Type.getReturnType(methodNode.desc);
		Type[] paramTypes = Type.getArgumentTypes(methodNode.desc);
		if (retType.equals(Type.VOID_TYPE) && paramTypes.length == 2
			&& paramTypes[0].getClassName().equals(HttpServletRequest.class.getName())
			&& paramTypes[1].getClassName().equals(HttpServletResponse.class.getName())) {
		    if (methodNode.name.equals("doGet")) {
			hasDoGet = true;
		    } else if (methodNode.name.equals("doPost")) {
			hasDoPost = true;
		    }
		}
	    }
	    if (hasDoGet || hasDoPost) {
		return cn.name.replace("/", ".") + (hasDoGet ? " doGet" : "") + (hasDoPost ? " doPost" : "");
	    }

	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
    }
}
