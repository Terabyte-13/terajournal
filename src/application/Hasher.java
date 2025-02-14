package application;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {
	
	String getHash(String string, String algorithm) {
		System.out.println(string);
		try {
		MessageDigest d = MessageDigest.getInstance(algorithm);
		byte stringBytes[] = d.digest(string.getBytes(StandardCharsets.UTF_16));
		string = new String(stringBytes, StandardCharsets.UTF_16);
	
		return string;
		} 	
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
}
