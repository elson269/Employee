package com.elsonji.test.ui.employees;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.elsonji.test.R;

import static com.elsonji.test.utils.Constants.TOKEN;

public class EmployeesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees);

        String token = getIntent().getStringExtra(TOKEN);

        Bundle bundle = new Bundle();
        bundle.putString(TOKEN, token);

        EmployeesFragment fragment = new EmployeesFragment();
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.employees_fragment_holder, fragment);
        fragmentTransaction.commit();

    }

}
