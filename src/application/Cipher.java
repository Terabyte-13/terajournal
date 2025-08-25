package application;

public class Cipher {
	
	String cipher(String key, String string, int direction) {
		//direction = 0: encrypt     direction = 1: decrypt
		char[] chars = string.toCharArray(); //caratteri da cifrare
		char[] keypins = key.toCharArray(); //caratteri della chiave

		for(int i = 0; i < chars.length-1; i++) {
			int j = i % keypins.length; //riporta j a 0 se eccede keypins.
			if(direction == 0) chars[i] += keypins[j];
			else if(direction == 1) chars[i] -= keypins[j];
		}
		
		return new String(chars);
	}
	
}
