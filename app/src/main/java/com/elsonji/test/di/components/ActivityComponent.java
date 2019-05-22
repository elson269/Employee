package com.elsonji.test.di.components;

import com.elsonji.test.di.modules.ActivityModule;
import com.elsonji.test.di.scopes.PerActivity;
import com.elsonji.test.ui.add.AddEmployeeActivity;
import com.elsonji.test.ui.employees.EmployeesAdapter;
import com.elsonji.test.ui.employees.EmployeesFragment;
import com.elsonji.test.ui.update.UpdateActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(EmployeesFragment employeesFragment);
    void inject(EmployeesAdapter employeesAdapter);
    void inject(AddEmployeeActivity addEmployeeActivity);
    void inject(UpdateActivity updateActivity);
}
