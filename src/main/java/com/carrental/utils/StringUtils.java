package com.carrental.utils;

public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isBlank();
    }

    public static boolean isNotNullOrEmpty(String value) {
        return !isNullOrEmpty(value);
    }

    public static String requireNotNullOrEmpty(String value, String fieldName) {
        if (isNullOrEmpty(value)) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
