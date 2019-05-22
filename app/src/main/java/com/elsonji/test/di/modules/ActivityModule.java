package com.elsonji.test.di.modules;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import com.elsonji.test.di.scopes.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    Context context;

    public ActivityModule(Context context) {
        this.context = context;
    }

    @PerActivity
    @Provides
    LinearLayoutManager provideLinearLayoutManager() {
        return new LinearLayoutManager(context);
    }

    @PerActivity
    @Provides
    Context provideContext() {
        return context;
    }
}
