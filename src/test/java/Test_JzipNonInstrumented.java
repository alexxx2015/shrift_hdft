import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import test.JZip2;

import edu.tum.uc.jvm.UcTransformer;
import edu.tum.uc.jvm.asm.MyClassAdapter;
import edu.tum.uc.jvm.asm.MyClassWriter;
import edu.tum.uc.jvm.utility.ConfigProperties;
import edu.tum.uc.jvm.utility.StatisticsWriter;
import edu.tum.uc.jvm.utility.Utility;

public class Test_JzipNonInstrumented extends AbstractTest{

	@Before
	public void init() throws Exception{
		init("/uc_jzip.config");
	}
	
	@Test
	public void testNoninstrumentation(){		
		String instruction = "";
		URL url = this.getClass().getResource("/toBeZippedFiles");
		if(url != null){
			File f = new File(url.getFile());
			instruction = "zip "+f.getParent()+"/toBeZipped.zip "+f.getAbsolutePath()+"/";
		}
		
		instruction += "\n exit";
		InputStream is2 = System.in;
		System.setIn(new ByteArrayInputStream(instruction.getBytes()));			
		JZip2.main(new String[0]);
		System.setIn(is2);
	}
}