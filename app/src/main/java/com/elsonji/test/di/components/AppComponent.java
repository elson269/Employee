package com.elsonji.test.di.components;

import android.app.Application;
import android.content.SharedPreferences;

import com.elsonji.test.di.modules.AppModule;

import javax.inject.Singleton;

import dagger.Component;
import io.reactivex.disposables.CompositeDisposable;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    CompositeDisposable compositedisposable();

    SharedPreferences sharedPreferences();

    Application application();

}
