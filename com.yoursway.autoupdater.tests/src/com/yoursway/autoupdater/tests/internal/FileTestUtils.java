package com.yoursway.autoupdater.tests.internal;

public class FileTestUtils {
    
    public static String fileContents(long length) {
        StringBuilder sb = new StringBuilder();
        long rest = length;
        while (rest > 20) {
            String s = "file contents ";
            sb.append(s);
            rest -= s.length();
        }
        while (rest > 0) {
            String s = "a";
            sb.append(s);
            rest -= s.length();
        }
        return sb.toString();
    }
    
    public static int sizeOf(int i) {
        return i * 100;
    }
    
}
