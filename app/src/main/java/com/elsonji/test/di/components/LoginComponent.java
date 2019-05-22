package com.elsonji.test.di.components;

import com.elsonji.test.di.modules.LoginModule;
import com.elsonji.test.di.scopes.PerUser;
import com.elsonji.test.ui.login.LoginActivity;

import dagger.Component;

@PerUser
@Component(dependencies = AppComponent.class, modules = LoginModule.class)
public interface LoginComponent {
    void inject(LoginActivity loginActivity);
}
