package com.elsonji.test.app;

import android.app.Application;

import com.elsonji.test.di.components.ActivityComponent;
import com.elsonji.test.di.components.AppComponent;
import com.elsonji.test.di.components.DaggerActivityComponent;
import com.elsonji.test.di.components.DaggerAppComponent;
import com.elsonji.test.di.components.DaggerLoginComponent;
import com.elsonji.test.di.components.LoginComponent;
import com.elsonji.test.di.modules.ActivityModule;
import com.elsonji.test.di.modules.AppModule;
import com.elsonji.test.di.modules.LoginModule;

import static com.elsonji.test.utils.Constants.BASE_URL;

public class TestApplication extends Application {
    private static AppComponent appComponent;
    private static LoginComponent loginComponent;
    private static ActivityComponent activityComponent;


    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder().
                appModule(new AppModule(this, BASE_URL)).build();

        loginComponent = DaggerLoginComponent.builder()
                .loginModule(new LoginModule())
                .appComponent(appComponent)
                .build();

        activityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .appComponent(appComponent)
                .build();
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    public static LoginComponent getLoginComponent() {
        return loginComponent;
    }

    public static ActivityComponent getActivityComponent() {
        return activityComponent;
    }
}
