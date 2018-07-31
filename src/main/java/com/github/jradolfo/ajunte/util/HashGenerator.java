package com.github.jradolfo.ajunte.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

public class HashGenerator {

	/**
	 * Computes the hash of the content using the selected algorithm.
	 * @param content The file's content
	 * @param hashAlgorithm The algorithm to be used to generate the hash
	 * @return The generated hash in hex format
	 */
    public static String computeHash(String content, String hashAlgorithm) {
        try {
            MessageDigest md5 = MessageDigest.getInstance(hashAlgorithm);
            byte[] digest = md5.digest(content.getBytes());
            return new HexBinaryAdapter().marshal(digest).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
	
}
