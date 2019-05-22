package com.elsonji.test.data.network;

import com.elsonji.test.entity.Employee;
import com.elsonji.test.entity.Token;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EmployeeService {

    @FormUrlEncoded
    @POST("users/signin")
    Observable<Token> userLogin(@Field("username") String userName,
                                @Field("password") String password);

    @GET("api/employees")
    Observable<ArrayList<Employee>> getEmployees();

    @GET("api/employees/{id}")
    Observable<Employee> getEmployee(@Path("id") int id);

    @POST("api/employees")
    Observable<Employee> addEmployee(@Body Employee employee);

    @PUT("api/employees/{id}")
    Observable<Employee> updateEmployee(@Path("id") int id, @Body Employee employee);

    @DELETE("api/employees/{id}")
    Observable<Employee> deleteEmployee(@Path("id") int id);
}
