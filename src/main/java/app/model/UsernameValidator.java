package app.model;

public class UsernameValidator {
    private static final int MIN = 3;
    private static final int MAX = 24;

    public static boolean isValid(String s) {
        if (s == null) return false;
        int n = s.length();
        if (n < MIN || n > MAX) return false;
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (!(c>='a'&&c<='z' || c>='A'&&c<='Z' || c>='0'&&c<='9' || c=='_'||c=='-')) {
                return false;
            }
        }
        return true;
    }
}
