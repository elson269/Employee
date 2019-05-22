package com.elsonji.test.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.elsonji.test.R;
import com.elsonji.test.app.TestApplication;
import com.elsonji.test.data.network.ServiceGenerator;
import com.elsonji.test.entity.Token;
import com.elsonji.test.ui.employees.EmployeesActivity;
import com.elsonji.test.utils.InternetConnection;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.elsonji.test.utils.Constants.TOKEN;

public class LoginActivity extends AppCompatActivity {
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private ProgressBar mLoadingProgressBar;
    private static final String TAG = LoginActivity.class.getSimpleName();
    @Inject
    CompositeDisposable mCompositeDisposables;

    private Observable<Token> mTokenObservable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TestApplication.getLoginComponent().inject(this);

        mUsernameEditText = findViewById(R.id.username);
        mPasswordEditText = findViewById(R.id.password);
        mLoginButton = findViewById(R.id.login);
        mLoadingProgressBar = findViewById(R.id.loading);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetConnection.isOnline(getApplication())) {
                    login();
                }
            }
        });
    }

    private void login() {
        mUsernameEditText.setError(null);
        mPasswordEditText.setError(null);
        String username = mUsernameEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();
        int err = 0;
        if (!validateEntry(username)) {
            err++;
            mUsernameEditText.setError("Username should not be empty!");
        }

        if (!validateEntry(password)) {
            err++;
            mPasswordEditText.setError("Password should not be empty!");
        }

        if (err == 0) {
            loginProceed(username, password);
            mLoadingProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void loginProceed(final String username, final String password) {
        mTokenObservable =
                ServiceGenerator.getEmployeeService(username, password).userLogin(username, password);
        mCompositeDisposables.add(mTokenObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Token>() {
                    @Override
                    public void onNext(Token token) {
                        Log.i(TAG, token.getJwt());

                        mLoadingProgressBar.setVisibility(View.GONE);

                        Bundle bundle = new Bundle();
                        bundle.putString(TOKEN, token.getJwt());

                        mUsernameEditText.setText(null);
                        mPasswordEditText.setText(null);

                        Intent intent = new Intent(getApplicationContext(), EmployeesActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);

                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }


    boolean validateEntry(String entry) {
        if (TextUtils.isEmpty(entry)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposables.clear();
    }
}
