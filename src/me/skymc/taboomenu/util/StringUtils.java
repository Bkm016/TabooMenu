package me.skymc.taboomenu.util;

public class StringUtils {

    public static boolean isInt(String var) {
        try {
            Integer.valueOf(var);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    public static boolean isBlank(String var) {
        return var == null || var.trim().isEmpty();
    }

    public static boolean isEmpty(CharSequence var) {
        return var == null || var.length() == 0;
    }

    public static String replaceWithOrder(String template, Object... args) {
        if (args.length != 0 && template.length() != 0) {
            char[] arr = template.toCharArray();
            StringBuilder stringBuilder = new StringBuilder(template.length());

            for (int i = 0; i < arr.length; ++i) {
                if (arr[i] == '{' && Character.isDigit(arr[Math.min(i + 1, arr.length - 1)]) && arr[Math.min(i + 1, arr.length - 1)] - 48 < args.length && arr[Math.min(i + 2, arr.length - 1)] == '}') {
                    stringBuilder.append(args[arr[i + 1] - 48]);
                    i += 2;
                } else {
                    stringBuilder.append(arr[i]);
                }
            }

            return stringBuilder.toString();
        } else {
            return template;
        }
    }

    public static String stripChars(String input, String removed) {
        return removed == null || removed.isEmpty() ? input : stripChars(input, removed.toCharArray());
    }

    public static String getCleanCommand(String message) {
        char[] chars = message.toCharArray();
        if (chars.length <= 1) {
            return "";
        }
        int pos = 0;
        for (int i = 1; i < chars.length; i++) {
            if (chars[i] == ' ') {
                break;
            }
            chars[(pos++)] = chars[i];
        }
        return new String(chars, 0, pos);
    }

    public static String stripChars(String input, char... removed) {
        if (input == null || input.isEmpty() || removed.length == 0) {
            return input;
        }
        char[] chars = input.toCharArray();
        int pos = 0;
        for (int i = 0; i < chars.length; i++) {
            if (!arrayContains(removed, chars[i])) {
                chars[(pos++)] = chars[i];
            }
        }
        return new String(chars, 0, pos);
    }

    private static boolean arrayContains(char[] arr, char match) {
        for (char c : arr) {
            if (c == match) {
                return true;
            }
        }
        return false;
    }

    public static String capitalizeFully(String input) {
        if (input == null) {
            return null;
        }
        String s = input.toLowerCase();
        int strLen = s.length();
        StringBuilder buffer = new StringBuilder(strLen);
        boolean capitalizeNext = true;
        for (int i = 0; i < strLen; i++) {
            char ch = s.charAt(i);
            if (Character.isWhitespace(ch)) {
                buffer.append(ch);
                capitalizeNext = true;
            } else if (capitalizeNext) {
                buffer.append(Character.toTitleCase(ch));
                capitalizeNext = false;
            } else {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }

    public static double similarDegree(String strA, String strB) {
        String newStrA = removeSign(max(strA, strB));
        String newStrB = removeSign(min(strA, strB));
        int temp = Math.max(newStrA.length(), newStrB.length());
        try {
            return longestCommonSubstring(newStrA, newStrB).length() * 1.0 / temp;
        } catch (Exception ignored) {
            return 0;
        }
    }

    private static String max(String strA, String strB) {
        return strA.length() >= strB.length() ? strA : strB;
    }

    private static String min(String strA, String strB) {
        return strA.length() < strB.length() ? strA : strB;
    }

    private static String removeSign(String str) {
        StringBuilder sb = new StringBuilder();
        for (char item : str.toCharArray()) {
            if (charReg(item)) {
                sb.append(item);
            }
        }
        return sb.toString();
    }

    private static boolean charReg(char charValue) {
        return (charValue >= 0x4E00 && charValue <= 0X9FA5) || (charValue >= 'a' && charValue <= 'z') || (charValue >= 'A' && charValue <= 'Z') || (charValue >= '0' && charValue <= '9');
    }

    private static String longestCommonSubstring(String strA, String strB) {
        char[] charStrA = strA.toCharArray();
        char[] charStrB = strB.toCharArray();
        int m = charStrA.length;
        int n = charStrB.length;

        int[][] matrix = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (charStrA[i - 1] == charStrB[j - 1]) {
                    matrix[i][j] = matrix[i - 1][j - 1] + 1;
                } else {
                    matrix[i][j] = Math.max(matrix[i][j - 1], matrix[i - 1][j]);
                }
            }
        }

        char[] result = new char[matrix[m][n]];
        int currentIndex = result.length - 1;
        while (matrix[m][n] != 0) {
            if (matrix[n] == matrix[n - 1]) {
                n--;
            } else if (matrix[m][n] == matrix[m - 1][n]) {
                m--;
            } else {
                result[currentIndex] = charStrA[m - 1];
                currentIndex--;
                n--;
                m--;
            }
        }
        return new String(result);
    }

    public static Boolean toBooleanObject(String str) {
        if (str.equals("true")) {
            return Boolean.TRUE;
        } else if (str == null) {
            return null;
        } else {
            char ch0;
            char ch1;
            char ch2;
            char ch3;
            switch (str.length()) {
                case 1:
                    ch0 = str.charAt(0);
                    if (ch0 == 'y' || ch0 == 'Y' || ch0 == 't' || ch0 == 'T') {
                        return Boolean.TRUE;
                    }

                    if (ch0 != 'n' && ch0 != 'N' && ch0 != 'f' && ch0 != 'F') {
                        break;
                    }

                    return Boolean.FALSE;
                case 2:
                    ch0 = str.charAt(0);
                    ch1 = str.charAt(1);
                    if ((ch0 == 'o' || ch0 == 'O') && (ch1 == 'n' || ch1 == 'N')) {
                        return Boolean.TRUE;
                    }

                    if ((ch0 == 'n' || ch0 == 'N') && (ch1 == 'o' || ch1 == 'O')) {
                        return Boolean.FALSE;
                    }
                    break;
                case 3:
                    ch0 = str.charAt(0);
                    ch1 = str.charAt(1);
                    ch2 = str.charAt(2);
                    if ((ch0 == 'y' || ch0 == 'Y') && (ch1 == 'e' || ch1 == 'E') && (ch2 == 's' || ch2 == 'S')) {
                        return Boolean.TRUE;
                    }

                    if ((ch0 == 'o' || ch0 == 'O') && (ch1 == 'f' || ch1 == 'F') && (ch2 == 'f' || ch2 == 'F')) {
                        return Boolean.FALSE;
                    }
                    break;
                case 4:
                    ch0 = str.charAt(0);
                    ch1 = str.charAt(1);
                    ch2 = str.charAt(2);
                    ch3 = str.charAt(3);
                    if ((ch0 == 't' || ch0 == 'T') && (ch1 == 'r' || ch1 == 'R') && (ch2 == 'u' || ch2 == 'U') && (ch3 == 'e' || ch3 == 'E')) {
                        return Boolean.TRUE;
                    }
                    break;
                case 5:
                    ch0 = str.charAt(0);
                    ch1 = str.charAt(1);
                    ch2 = str.charAt(2);
                    ch3 = str.charAt(3);
                    char ch4 = str.charAt(4);
                    if ((ch0 == 'f' || ch0 == 'F') && (ch1 == 'a' || ch1 == 'A') && (ch2 == 'l' || ch2 == 'L') && (ch3 == 's' || ch3 == 'S') && (ch4 == 'e' || ch4 == 'E')) {
                        return Boolean.FALSE;
                    }
                default:
            }
            return null;
        }
    }
}
