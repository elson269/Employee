package com.elsonji.test.data.network;

public class ServiceGenerator {
    private static EmployeeService employeeService;
    public static EmployeeService getEmployeeService(String userName, String password) {
        if (employeeService == null) {
            employeeService = RetrofitClientInstance.getTokenRetrofitInstance(userName, password).create(EmployeeService.class);
        }
        return employeeService;
    }

    public static EmployeeService getEmployeeService(String token) {
            employeeService = RetrofitClientInstance.getEmployeeRetrofitInstance(token).create(EmployeeService.class);
        return employeeService;
    }

}


