package stink5.oauth2.lab2.util;

import java.util.Random;

public class CodeGenerator {

    private static Random rnd = new Random();
    private static final char[] CHARS = "АБВГДЕЖЗИЙКЛНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".toCharArray();

    public static String generateCode() {
        int charsLength = 3;
        char[] chars = new char[charsLength];
        for (int i = 0; i < charsLength; i++) {
            chars[i] = CHARS[rnd.nextInt(charsLength)];
        }
        return new StringBuilder(6)
            .append(chars[0])
            .append(rnd.nextInt(1_000))
            .append(chars[1])
            .append(chars[2])
            .toString();
    }

}
