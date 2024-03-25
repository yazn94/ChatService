package com.chat.chatservice.util;

public class StringOperations {
    public static String removeQuotesIfExist(String s) {
        if (s.charAt(0) == '\"') {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static String removeBearerIfExist(String s) {
        if (s.startsWith("Bearer ")) {
            return s.substring(7);
        }
        return s;
    }
}