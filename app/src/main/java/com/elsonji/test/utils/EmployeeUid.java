package com.elsonji.test.utils;

public class EmployeeUid {
    public static String getEmployeeUid(String firstName, String lastName, String role) {
        return firstName+lastName+role;
    }
}
