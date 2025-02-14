package application;

public class Cipher {
	
	String cipher(String key, String string, int direction) {
		//direction = 0: encrypt     direction = 1: decrypt
		char[] chars = string.toCharArray(); //caratteri da criptare
		char[] keypins = key.toCharArray(); //caratteri della chiave
		System.out.printf("key: %s\n", key);

		for(int i = 0, j = 0; i < chars.length-1; i++, j++) {
			if(j >= keypins.length) j = 0;
			//System.out.printf("spiazzo %c con %c\n", chars[i], keypins[j]);
			if(direction == 0) chars[i] += keypins[j];
			else if(direction == 1) chars[i] -= keypins[j];
		}
		
		String estring = new String(chars);
		return estring;
	}
	
}
