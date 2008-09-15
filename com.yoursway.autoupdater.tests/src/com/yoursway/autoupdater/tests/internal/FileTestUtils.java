package com.yoursway.autoupdater.tests.internal;

import java.util.GregorianCalendar;

import com.yoursway.utils.YsDigest;

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
    
    public static long lastModifiedOf(int i) {
        GregorianCalendar calendar = new GregorianCalendar(2008, 8, 28, 11, 26);
        return calendar.getTimeInMillis() - i * 100000;
    }
    
    public static String fileContentsOf(int i) {
        return fileContents(sizeOf(i));
    }
    
    public static String hashOf(int i) {
        return YsDigest.sha1(fileContentsOf(i));
    }
    
}
