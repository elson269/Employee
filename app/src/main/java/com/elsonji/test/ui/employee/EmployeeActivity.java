package com.elsonji.test.ui.employee;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.elsonji.test.R;

import static com.elsonji.test.utils.Constants.FIRST_NAME;
import static com.elsonji.test.utils.Constants.ID;
import static com.elsonji.test.utils.Constants.LAST_NAME;
import static com.elsonji.test.utils.Constants.ROLE;

public class EmployeeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        String id = getIntent().getStringExtra(ID);
        String firstName = getIntent().getStringExtra(FIRST_NAME);
        String lastName = getIntent().getStringExtra(LAST_NAME);
        String role = getIntent().getStringExtra(ROLE);

        Bundle bundle = new Bundle();
        bundle.putString(ID, id);
        bundle.putString(FIRST_NAME, firstName);
        bundle.putString(LAST_NAME, lastName);
        bundle.putString(ROLE, role);

        EmployeeFragment fragment = new EmployeeFragment();
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.employee_fragment_holder, fragment);
        fragmentTransaction.commit();
    }
}
