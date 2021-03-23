package rip.lazze.libraries.utilities;

public class NumberUtils {
    public static boolean isInteger(String s) {
        int radix = 10;
        int result = 0;
        int i = 0;
        int len = s.length();
        int limit = -2147483647;
        if (len > 0) {
            char firstChar = s.charAt(0);
            if (firstChar < '0') {
                if (firstChar == '-') {
                    limit = Integer.MIN_VALUE;
                } else if (firstChar != '+') {
                    return false;
                }
                if (len == 1)
                    return false;
                i++;
            }
            int multmin = limit / 10;
            while (i < len) {
                int digit = Character.digit(s.charAt(i++), 10);
                if (digit < 0)
                    return false;
                if (result < multmin)
                    return false;
                result *= 10;
                if (result < limit + digit)
                    return false;
                result -= digit;
            }
            return true;
        }
        return false;
    }

    public static boolean isShort(String input) {
        if (!isInteger(input))
            return false;
        int value = Integer.parseInt(input);
        return (value > -32768 && value < 32767);
    }
}

