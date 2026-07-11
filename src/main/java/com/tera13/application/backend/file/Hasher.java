package com.tera13.application.backend.file;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class Hasher {
	
	public String getHash(String string, String algorithm) {
		try {
		MessageDigest d = MessageDigest.getInstance(algorithm);
		byte[] stringBytes = d.digest(string.getBytes());
		string = HexFormat.of().formatHex(stringBytes);
	
		return string;
		} 	
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

}
