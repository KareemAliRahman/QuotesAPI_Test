package com.QuotesAPI_Test;

import java.nio.charset.Charset;
import java.security.SecureRandom;

public class BaseTest {

    String url = "http://quotes.rest/";
    
    //Add token here  as String token = "<API-TOKEN>"
    String token = ""

    //method to generate random String
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();
    public static String generate(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; ++i) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}