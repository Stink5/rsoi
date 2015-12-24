package stink5.oauth2.lab2.util;

public class Strings {

    public static boolean isBlank(final String input) {
        return input == null || input.chars().allMatch(Character::isWhitespace);
    }
}
