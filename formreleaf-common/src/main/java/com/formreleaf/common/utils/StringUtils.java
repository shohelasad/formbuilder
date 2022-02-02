package com.formreleaf.common.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Bazlur Rahman Rokon
 * @date 4/27/15.
 */
public class StringUtils {
    //private static final Logger LOGGER = LoggerFactory.getLogger(StringUtils.class);

    public static boolean isEmpty(String str) {

        return ((str == null) || (str.trim().length() == 0));
    }

    public static boolean isNotEmpty(String str) {

        return !isEmpty(str);
    }

    private static String eligibleChars = "ABDEFGHJKLMRSTUVWXYabdefhjkmnrstuvwxy23456789";

    public static String generateRandomString(int stringLength) {
        char[] chars = eligibleChars.toCharArray();
        final StringBuffer finalString = new StringBuffer();

        for (int i = 0; i < stringLength; i++) {
            double randomValue = Math.random();
            int randomIndex = (int) Math.round(randomValue * (chars.length - 1));
            char characterToShow = chars[randomIndex];
            finalString.append(characterToShow);
        }

        return finalString.toString();
    }

    public static String getTrimmedString(String text, int lengthToTrim) {
        if (text.length() <= lengthToTrim) {
            return text;
        }

        return text.substring(0, lengthToTrim);
    }

    public static String slugify(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("[^ \\w]", "").trim()
                .replaceAll("\\s+", "-").toLowerCase(Locale.ENGLISH);
    }

    public static int compareNullableString(String lastName1, String lastName2, boolean caseInsensitive) {
        if (Objects.equals(lastName1, lastName2)) {
            return 0;
        } else if (StringUtils.isEmpty(lastName1)) {
            return -1;
        } else if (StringUtils.isEmpty(lastName2)) {
            return 1;
        } else {
            return caseInsensitive ? lastName1.compareToIgnoreCase(lastName2) : lastName1.compareTo(lastName2);
        }
    }

    public static String getTrimmedString(String q) {

        return q.trim().replaceAll("(\\s)+", "$1");

    }
    
    public static String replaceNonAplhaNumericCharWithSpace(String q) {

        return q.replaceAll("[^A-Za-z0-9]", " ");

    }
}
