package com.mitchtalmadge.cryptex.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for working with Strings.
 */
public class StringUtils {

    /**
     * Splits a given string so that the first part ends on a space, and is up to maxLength long.
     * The second part is the remainder that was cut off.
     * <p>
     * In the event that the string cannot be split on a space, it will be split normally.
     *
     * @param str       The string that was split.
     * @param maxLength The maximum length of the first section.
     * @return An array containing the two split sections.
     */
    public static String[] splitOnSpace(String str, int maxLength) {
        // Max length too long
        if (maxLength >= str.length())
            return new String[]{str, ""};

        // Max length too small
        if (maxLength <= 0)
            return new String[]{"", str};

        // Start at max length and work backwards.
        for (int i = maxLength - 1; i > 0; i--) {
            // Check if we found a space.
            if (str.charAt(i) == ' ') {
                return new String[]{str.substring(0, i), str.substring(i + 1)};
            }
        }

        // Didn't find space, so split normally.
        return new String[]{str.substring(0, maxLength - 1), str.substring(maxLength)};
    }

    /**
     * Breaks a string up into chunks that are at most maxLength long.
     * Attempts to break the string on spaces when possible.
     *
     * @param str       The string to split.
     * @param maxLength The maximum length of each chunk.
     * @return The split string.
     */
    public static String[] segmentString(String str, int maxLength) {
        // Temporary storage for parts.
        List<String> parts = new ArrayList<>();

        // The remaining message portion.
        String remainder = str;

        // Split string as long as necessary.
        while (remainder.length() > maxLength) {
            String[] splitParts = StringUtils.splitOnSpace(remainder, maxLength);
            parts.add(splitParts[0]);
            remainder = splitParts[1];
        }

        // Add last remainder.
        parts.add(remainder);

        // Convert list to array.
        return parts.toArray(new String[parts.size()]);
    }

    /**
     * Causes links to not be clickable in chatrooms.
     *
     * @param str The string that may contain links.
     * @return The string with links that have been suppressed.
     */
    public static String suppressLinks(String str) {
        return str.replaceAll("http://", "").replaceAll("https://", "");
    }

}
