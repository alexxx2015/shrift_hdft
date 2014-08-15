package de.tum.in.i22.policyeditor.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PublicMethods {

	public static String readFile(String path, Charset encoding) throws IOException {
		  byte[] encoded = Files.readAllBytes(Paths.get(path));
		  return encoding.decode(ByteBuffer.wrap(encoded)).toString();
		}
		
	
}
