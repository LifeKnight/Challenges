package com.lifeknight.challenges.utilities;

import net.minecraft.util.EnumChatFormatting;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Text {

    public static List<String> returnStartingEntries(String[] strings, String text, boolean ignoreCase) {
        if (text == null || text.isEmpty()) return Arrays.asList(strings);
        List<String> result = new ArrayList<>();
        if (ignoreCase) text = text.toLowerCase();
        for (String string : strings) {
            if (ignoreCase) string = string.toLowerCase();
            if (string.startsWith(text)) result.add(string);
        }
        return result;
    }

    public static List<String> returnStartingEntries(String text, List<String> strings, boolean ignoreCase) {
        if (text == null || text.isEmpty()) return strings;
        List<String> result = new ArrayList<>();
        if (ignoreCase) text = text.toLowerCase();
        for (String string : strings) {
            if (ignoreCase) string = string.toLowerCase();
            if (string.startsWith(text)) result.add(string);
        }
        return result;
    }

    public static String removeAllPunctuation(String text) {
        return text.replaceAll("\\W", "");
    }

    public static int countWords(String text) {
        int count = 0;
        for (int x = 0; x < text.length(); x++) {
            if (text.charAt(x) == ' ') {
                count++;
            }
        }
        return ++count;
    }

    public static String removeFormattingCodes(String text) {
        return EnumChatFormatting.getTextWithoutFormattingCodes(text);
    }

    public static String multiplyString(String text, int times) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < times; i++) {
            result.append(text);
        }
        return result.toString();
    }

    public static String formatCapitalization(String text, boolean keepFirstCapitalized) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = text.length() - 1; i > 0; i--) {
            char toInsert;
            char previousChar = text.charAt(i - 1);
            if (previousChar == Character.toUpperCase(previousChar)) {
                toInsert = Character.toLowerCase(text.charAt(i));
            } else {
                toInsert = text.charAt(i);
            }
            stringBuilder.insert(0, toInsert);
        }

        return stringBuilder.insert(0, keepFirstCapitalized ? text.charAt(0) : Character.toLowerCase(text.charAt(0))).toString();
    }

    public static String parseTextToIndexOfTextAfter(String text, String firstIndexText, String secondIndexText) {
        if (text.contains(firstIndexText) && text.contains(secondIndexText)) {
            return text.substring((firstIndexText.indexOf(firstIndexText) + firstIndexText.length() + 1), (text.indexOf(secondIndexText) - 1));
        }
        return null;
    }

    public static String shortenDouble(double value, int decimalDigits) {
        String asString = String.valueOf(value);
        int wholeDigits = asString.substring(0, asString.indexOf(".")).length();
        return new DecimalFormat(multiplyString("#", wholeDigits) + "." + multiplyString("#", decimalDigits)).format(value);
    }

    public static boolean equalsAny(String text, List<String> strings, boolean ignoreCase, boolean ignorePunctuation) {
        if (ignoreCase) text = text.toLowerCase();
        if (ignorePunctuation) text = removeAllPunctuation(text);
        for (String string : strings) {
            if (ignoreCase) string = string.toLowerCase();
            if (ignorePunctuation) string = removeAllPunctuation(text);
            if (text.equals(string)) return true;
        }
        return false;
    }

    public static boolean containsAny(String text, List<String> strings, boolean ignoreCase, boolean ignorePunctuation) {
        if (ignoreCase) text = text.toLowerCase();
        if (ignorePunctuation) text = removeAllPunctuation(text);
        for (String string : strings) {
            if (ignoreCase) string = string.toLowerCase();
            if (ignorePunctuation) string = removeAllPunctuation(text);
            if (text.contains(string)) return true;
        }
        return false;
    }

    public static boolean startsWithAny(String text, List<String> strings, boolean ignoreCase, boolean ignorePunctuation) {
        if (ignoreCase) text = text.toLowerCase();
        if (ignorePunctuation) text = removeAllPunctuation(text);
        for (String string : strings) {
            if (ignoreCase) string = string.toLowerCase();
            if (ignorePunctuation) string = removeAllPunctuation(text);
            if (text.startsWith(string)) return true;
        }
        return false;
    }

    public static boolean endsWithAny(String text, List<String> strings, boolean ignoreCase, boolean ignorePunctuation) {
        if (ignoreCase) text = text.toLowerCase();
        if (ignorePunctuation) text = removeAllPunctuation(text);
        for (String string : strings) {
            if (ignoreCase) string = string.toLowerCase();
            if (ignorePunctuation) string = removeAllPunctuation(string);
            if (text.endsWith(string)) return true;
        }
        return false;
    }

    public static boolean containsLetters(String input) {
        return Pattern.compile("[a-zA-Z]").matcher(input).find();
    }

    public static String formatTimeFromMilliseconds(long milliseconds) {
        return formatTimeFromMilliseconds(milliseconds, 2, false);
    }

    public static String formatTimeFromMilliseconds(long milliseconds, int count, boolean includeMilliseconds) {
        long days;
        long hours;
        long minutes;
        long seconds;
        long millisecondsLeft = milliseconds;
        days = millisecondsLeft / 86400000;
        millisecondsLeft %= 86400000;
        hours = millisecondsLeft / 3600000;
        millisecondsLeft %= 3600000;
        minutes = millisecondsLeft / 60000;
        millisecondsLeft %= 60000;
        seconds = millisecondsLeft / 1000;
        millisecondsLeft %= 1000;

        StringBuilder result = new StringBuilder();

        if (days > 0 && count >= 4) {
            result.append(days).append(":");
            result.append(appendTime(hours)).append(":");
        } else if (count >= 3) {
            result.append(hours).append(":");
        }


        if (count >= 2) result.append(appendTime(minutes)).append(":");

        if (count >= 1) result.append(appendTime(seconds)).append(".");

        if (includeMilliseconds) result.append(formatMilliseconds(millisecondsLeft));

        return result.toString();
    }

    private static String appendTime(long timeValue) {
        StringBuilder result = new StringBuilder();
        if (timeValue > 9) {
            result.append(timeValue);
        } else {
            result.append("0").append(timeValue);
        }
        return result.toString();
    }

    private static String formatMilliseconds(long milliseconds) {
        String asString = String.valueOf(milliseconds);

        if (asString.length() == 1) {
            return "00" + milliseconds;
        } else if (asString.length() == 2) {
            return "0" + milliseconds;
        }
        return asString;
    }

    public static String getCurrentDateString() {
        return new SimpleDateFormat("MM/dd/yyyy").format(System.currentTimeMillis());
    }

    public static String getCurrentTimeString() {
        return new SimpleDateFormat("hh:mm:ss a").format(System.currentTimeMillis());
    }

    public static String getDateString(long milliseconds) {
        return new SimpleDateFormat("MM/dd/yyyy").format(milliseconds);
    }

    public static String getTimeString(long milliseconds) {
        return new SimpleDateFormat("hh:mm:ss a").format(milliseconds);
    }

    public static String shortenText(String text, int maximumLength) {
        return text.substring(0, Math.min(text.length(), maximumLength) - 1);
    }

    public static String shortenText(String text) {
        return shortenText(text, 100);
    }
}
