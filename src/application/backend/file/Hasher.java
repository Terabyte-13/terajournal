package application.backend.file;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {
	
	public String getHash(String string, String algorithm) {
		try {
		MessageDigest d = MessageDigest.getInstance(algorithm);
		byte[] stringBytes = d.digest(string.getBytes(StandardCharsets.UTF_16));
		string = new String(stringBytes, StandardCharsets.UTF_16);
	
		return string;
		} 	
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
}
