package com.elsonji.test.ui.update;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import static com.elsonji.test.utils.Constants.FIRST_NAME;
import static com.elsonji.test.utils.Constants.ID;
import static com.elsonji.test.utils.Constants.LAST_NAME;
import static com.elsonji.test.utils.Constants.ROLE;
import static com.elsonji.test.utils.Constants.TOKEN;
import static com.elsonji.test.utils.Constants.UPDATE_KEY;

public class UpdateActivity extends AppCompatActivity {
    @BindView(R.id.id_edit_textView)
    TextView mIdTextView;

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
    private String mToken;
    private String mId, mFirstName, mLastName, mRole, mPhoneNumber;
    private String mEmployeeUid;
    private Observable<Employee> mEmployeeObservable;

    @Inject
    SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        TestApplication.getActivityComponent().inject(this);

        ButterKnife.bind(this);

        mToken = getIntent().getStringExtra(TOKEN);
        mId = getIntent().getStringExtra(ID);
        mFirstName = getIntent().getStringExtra(FIRST_NAME);
        mLastName = getIntent().getStringExtra(LAST_NAME);
        mRole = getIntent().getStringExtra(ROLE);

        populateData();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEmployeeObject(mToken, mId);
            }
        });
    }

    private void populateData() {
        mIdTextView.setText(String.valueOf(mId));
        mFirstNameTextView.setText(mFirstName);
        mLastNameTextView.setText(mLastName);
        mRoleTextView.setText(mRole);

        if (hasPhoneNumberLocally(mFirstName, mLastName, mRole)) {
            mPhoneNumber = mSharedPref.getString(EmployeeUid.getEmployeeUid(mFirstName, mLastName, mRole), "error");
            mPhoneEditTextView.setText(mPhoneNumber);
        }

    }

    private boolean hasPhoneNumberLocally(String firstName, String lastName, String role) {
        String uid = EmployeeUid.getEmployeeUid(firstName, lastName, role);
        return mSharedPref.contains(uid);
    }

    private void addPhoneNumberLocally(String phoneNumber, String firstName, String lastName, String role,
                                       String id, String token, Employee employee) {
        if (PhoneValidator.isPhoneNumberValid(phoneNumber)) {
            SharedPreferences.Editor phoneEditor = mSharedPref.edit();
            mEmployeeUid = EmployeeUid.getEmployeeUid(firstName, lastName, role);
            phoneEditor.putString(mEmployeeUid, phoneNumber);
            phoneEditor.apply();

            if (InternetConnection.isOnline(getApplication())) {
                updateEmployeeToCloud(employee, token, Integer.valueOf(id));
            }

        } else {
            Toast.makeText(this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEmployeeObject(String token, String id) {
        String firstName = mFirstNameTextView.getText().toString().trim();
        String lastName = mLastNameTextView.getText().toString().trim();
        String role = mRoleTextView.getText().toString().trim();
        String phoneNumber = mPhoneEditTextView.getText().toString().trim();

        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName) && !TextUtils.isEmpty(role) && !TextUtils.isEmpty(phoneNumber)) {
            Employee employee = new Employee();
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setRole(role);

            addPhoneNumberLocally(phoneNumber, firstName, lastName, role, id, token, employee);

        } else {
            Toast.makeText(this, "All fields cannot be empty.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEmployeeToCloud(Employee employee, String token, int id) {
        mEmployeeObservable = ServiceGenerator
                .getEmployeeService(token).updateEmployee(id, employee);
        mCompositeDisposable.add(mEmployeeObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Employee>() {
                    @Override
                    public void onNext(Employee employee) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getApplication(), "Internet error.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(getApplication(), "Successful.", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = mSharedPref.edit();
                        editor.putString(UPDATE_KEY, "update");
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
