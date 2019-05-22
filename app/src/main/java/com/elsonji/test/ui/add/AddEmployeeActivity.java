package com.elsonji.test.ui.add;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elsonji.test.R;
import com.elsonji.test.app.TestApplication;
import com.elsonji.test.data.network.ServiceGenerator;
import com.elsonji.test.entity.Employee;
import com.elsonji.test.utils.EmployeeUid;
import com.elsonji.test.utils.InternetConnection;
import com.elsonji.test.utils.PhoneValidator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.elsonji.test.utils.Constants.REFRESH_KEY;
import static com.elsonji.test.utils.Constants.TOKEN;

public class AddEmployeeActivity extends AppCompatActivity {

    @BindView(R.id.first_name_edit_textView)
    EditText mFirstNameTextView;

    @BindView(R.id.last_name_edit_textView)
    EditText mLastNameTextView;

    @BindView(R.id.role_edit_textView)
    EditText mRoleTextView;

    @BindView(R.id.button)
    Button mButton;

    @BindView(R.id.phone_edit_textView)
    EditText mPhoneEditTextView;

    @Inject
    CompositeDisposable mCompositeDisposable;
    private Observable<Employee> mEmployeeObservable;
    @Inject
    SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        ButterKnife.bind(this);
        TestApplication.getActivityComponent().inject(this);

        final String token = getIntent().getStringExtra(TOKEN);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEmployeeObject(token);
            }
        });
    }

    private void createEmployeeObject(String token) {
        String firstName = mFirstNameTextView.getText().toString().trim();
        String lastName = mLastNameTextView.getText().toString().trim();
        String role = mRoleTextView.getText().toString().trim();
        String phoneNumber = mPhoneEditTextView.getText().toString().trim();
        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)
                && !TextUtils.isEmpty(role) && !TextUtils.isEmpty(phoneNumber)) {
            Employee employee = new Employee();
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setRole(role);

            addPhoneNumberLocally(phoneNumber, firstName, lastName, role, employee, token);
        } else {
            Toast.makeText(this, "All fields cannot be empty.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addPhoneNumberLocally(String phoneNumber, String firstName, String lastName, String role,
                                       Employee employee, String token) {
        if (PhoneValidator.isPhoneNumberValid(phoneNumber)) {
            //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor phoneEditor = mSharedPref.edit();
            String employeeUid = EmployeeUid.getEmployeeUid(firstName, lastName, role);
            phoneEditor.putString(employeeUid, phoneNumber);
            phoneEditor.apply();
            if (InternetConnection.isOnline(getApplication())) {
                addEmployeeToCloud(employee, token);
            }

        } else {
            Toast.makeText(this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addEmployeeToCloud(Employee employee, String token) {

        mEmployeeObservable = ServiceGenerator
                .getEmployeeService(token).addEmployee(employee);
        mCompositeDisposable.add(mEmployeeObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Employee>() {
                    @Override
                    public void onNext(Employee employee) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(getApplication(), "Successful.", Toast.LENGTH_SHORT).show();
                        //SharedPreferences refreshSharedPref = PreferenceManager.getDefaultSharedPreferences(getApplication());
                        SharedPreferences.Editor editor = mSharedPref.edit();
                        editor.putString(REFRESH_KEY, "refresh");
                        editor.apply();
                        finish();
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }

}
