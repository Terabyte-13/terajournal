package com.tera13.application.backend.file;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cipher {

	Logger logger = Logger.getLogger("Cipher");

	String cipher(String key, String input, int direction) {
		//direction = 0: encrypt     direction = 1: decrypt
		if(key == null || key.isEmpty() || input == null || input.isEmpty()){
			logger.log(Level.INFO, "cipher chiamato senza key o con input vuoto. restituisco l'input così com'è.");
			return input;
		}

		byte[] inputBytes;
		byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

		if (direction == 0) {
			// cifra: da stringa a byte utf-8
			inputBytes = input.getBytes(StandardCharsets.UTF_8);
		} else {
			// decifra: da byte base64 a byte cifrati (mimedecoder gestisce i caratteri speciali html senza dare problemi)
			inputBytes = Base64.getMimeDecoder().decode(input.trim());;
		}

		for (int i = 0; i < inputBytes.length; i++) {
			int j = i % keyBytes.length;
			if (direction == 0) {
				inputBytes[i] += keyBytes[j];
			} else if (direction == 1) {
				inputBytes[i] -= keyBytes[j];
			}
		}

		if (direction == 0) {
			// cifra: porto i byte in forma di una stringa da scrivere nel file
			return Base64.getMimeEncoder().encodeToString(inputBytes);
		} else {
			// decifra: riporto i byte in testo leggibile nell'editor
			return new String(inputBytes, StandardCharsets.UTF_8);
		}
	}
	
}
