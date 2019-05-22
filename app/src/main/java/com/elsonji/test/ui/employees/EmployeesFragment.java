package com.elsonji.test.ui.employees;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.elsonji.test.R;
import com.elsonji.test.app.TestApplication;
import com.elsonji.test.data.network.ServiceGenerator;
import com.elsonji.test.entity.Employee;
import com.elsonji.test.ui.add.AddEmployeeActivity;
import com.elsonji.test.ui.employee.EmployeeActivity;
import com.elsonji.test.utils.InternetConnection;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.elsonji.test.utils.Constants.FIRST_NAME;
import static com.elsonji.test.utils.Constants.ID;
import static com.elsonji.test.utils.Constants.LAST_NAME;
import static com.elsonji.test.utils.Constants.REFRESH_KEY;
import static com.elsonji.test.utils.Constants.ROLE;
import static com.elsonji.test.utils.Constants.TOKEN;
import static com.elsonji.test.utils.Constants.UPDATE_KEY;

public class EmployeesFragment extends Fragment implements EmployeesAdapter.OnEmployeeItemClickListener
        , EmployeesAdapter.RefreshOnDeleteListener {

    private EmployeesAdapter mEmployeeAdapter;
    private LinearLayoutManager mLayoutManager;
    @Inject
    CompositeDisposable mCompositeDisposable;
    private static final String TAG = EmployeesFragment.class.getSimpleName();

    private String mId;
    private String mFirstName;
    private String mLastName;
    private String mRole;
    private ArrayList<Employee> mEmployeeList;
    private String mToken;
    private FloatingActionButton mFab;
    private Observable<Employee> mEmployeeObservable;
    Observable<ArrayList<Employee>> mEmployeeListObservable;
    @Inject
    SharedPreferences mSharedPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        TestApplication.getActivityComponent().inject(this);
        View rootView = inflater.inflate(R.layout.fragment_employees, container, false);
        RecyclerView employeesRecycler = rootView.findViewById(R.id.employees_recycler);

        mEmployeeAdapter = new EmployeesAdapter(getContext(), this, mToken, this);
        String token;
        if (getArguments() != null) {
            token = getArguments().getString(TOKEN);
            mToken = token;
            getEmployeeList(token);
        }


        employeesRecycler.setAdapter(mEmployeeAdapter);
        mLayoutManager = new LinearLayoutManager(getContext());
        employeesRecycler.setLayoutManager(mLayoutManager);

        mFab = rootView.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddEmployeeActivity.class);
                intent.putExtra(TOKEN, mToken);
                startActivity(intent);
            }
        });
        return rootView;

    }

    private void getEmployeeList(String token) {
        Log.i(TAG, token);

        mEmployeeListObservable = ServiceGenerator
                .getEmployeeService(token).getEmployees();

        mCompositeDisposable.add(mEmployeeListObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<ArrayList<Employee>>() {
                    @Override
                    public void onNext(ArrayList<Employee> employeeList) {
                        mEmployeeList = employeeList;
                        mEmployeeAdapter.setEmployeeList(employeeList);
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

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String refreshContent = prefs.getString(REFRESH_KEY, "");
        if (refreshContent.equals("refresh")) {
            if (InternetConnection.isOnline(getContext())) {
                reloadEmployeeList();
            }

            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putString(REFRESH_KEY, "");
            editor.apply();
        }

        String updateString = prefs.getString(UPDATE_KEY, "");
        if (updateString.equals("update")) {
            if (InternetConnection.isOnline(getContext())) {
                reloadEmployeeList();
            }

            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putString(UPDATE_KEY, "");
            editor.apply();
        }
    }

    private void reloadEmployeeList() {
        if (InternetConnection.isOnline(getContext())) {
            mEmployeeListObservable = ServiceGenerator
                    .getEmployeeService(mToken).getEmployees();

            mCompositeDisposable.add(mEmployeeListObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<ArrayList<Employee>>() {
                        @Override
                        public void onNext(ArrayList<Employee> employeeList) {
                            mEmployeeList = employeeList;
                            mEmployeeAdapter.setEmployeeList(employeeList);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }

    @Override
    public void onItemClick(int i, final ImageView imageView) {
        if (InternetConnection.isOnline(getContext())) {
            mEmployeeObservable = ServiceGenerator
                    .getEmployeeService(mToken).getEmployee(mEmployeeList.get(i).getId());

            mCompositeDisposable.add(mEmployeeObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<Employee>() {
                        @Override
                        public void onNext(Employee employee) {
                            mId = String.valueOf(employee.getId());
                            mFirstName = employee.getFirstName();
                            mLastName = employee.getLastName();
                            mRole = employee.getRole();
                            Intent intent = new Intent(getContext(), EmployeeActivity.class);
                            Bundle bundle = new Bundle();

                            bundle.putString(ID, mId);
                            bundle.putString(FIRST_NAME, mFirstName);
                            bundle.putString(LAST_NAME, mLastName);
                            bundle.putString(ROLE, mRole);
                            intent.putExtras(bundle);

                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                                    .makeSceneTransitionAnimation(getActivity(), imageView, "profile");
                            getActivity().startActivity(intent, optionsCompat.toBundle());

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
    }

    @Override
    public void onRefresh() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
