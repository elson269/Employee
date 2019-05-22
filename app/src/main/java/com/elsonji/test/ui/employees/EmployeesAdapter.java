package com.elsonji.test.ui.employees;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.elsonji.test.R;
import com.elsonji.test.app.TestApplication;
import com.elsonji.test.data.network.ServiceGenerator;
import com.elsonji.test.entity.Employee;
import com.elsonji.test.ui.update.UpdateActivity;
import com.elsonji.test.utils.EmployeeUid;
import com.elsonji.test.utils.InternetConnection;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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

public class EmployeesAdapter extends RecyclerView.Adapter<EmployeesAdapter.EmployeeViewHolder> {
    private Context mContext;
    private List<Employee> mEmployeeList;
    private OnEmployeeItemClickListener mListener;
    private String mToken;
    @Inject
    CompositeDisposable mCompositeDisposables;
    @Inject
    SharedPreferences mSharedPref;
    private String mId, mFirstName, mLastName, mRole, mPhoneNumber;
    private RefreshOnDeleteListener mRefreshListener;

    EmployeesAdapter(Context context, OnEmployeeItemClickListener listener, String token, RefreshOnDeleteListener reFreshListener) {
        mContext = context;
        mListener = listener;
        mToken = token;
        mRefreshListener = reFreshListener;
        TestApplication.getActivityComponent().inject(this); }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        ConstraintLayout itemView = (ConstraintLayout) LayoutInflater.from(mContext)
                .inflate(R.layout.employee_item_view, viewGroup, false);
        final EmployeeViewHolder viewHolder = new EmployeeViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final EmployeeViewHolder employeeViewHolder, int i) {

        mId = String.valueOf(mEmployeeList.get(i).getId());
        mFirstName = mEmployeeList.get(i).getFirstName();
        mLastName = mEmployeeList.get(i).getLastName();
        mRole = mEmployeeList.get(i).getRole();

        if (hasPhoneNumberLocally(mFirstName, mLastName, mRole)) {
            //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            mPhoneNumber = mSharedPref.getString(EmployeeUid.getEmployeeUid(mFirstName, mLastName, mRole), "");
        }

        Picasso.get().load(R.drawable.avatar).into(employeeViewHolder.profileImageView);
        employeeViewHolder.idTextView.setText(mId);
        employeeViewHolder.firstNameTextView.setText(mFirstName);
        employeeViewHolder.lastNameTextView.setText(mLastName);
        employeeViewHolder.roleTextView.setText(mRole);
        employeeViewHolder.phoneTextView.setText(mPhoneNumber);


        employeeViewHolder.deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetConnection.isOnline(mContext)) {
                    Observable<Employee> employeeObservable = ServiceGenerator
                            .getEmployeeService(mToken).deleteEmployee(mEmployeeList.get(employeeViewHolder.getAdapterPosition()).getId());

                    mCompositeDisposables.add(employeeObservable
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribeWith(new DisposableObserver<Employee>() {
                                @Override
                                public void onNext(Employee employee) {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                    mRefreshListener.onRefresh();
                                }

                                @Override
                                public void onComplete() {
                                    //SharedPreferences refreshSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
                                    SharedPreferences.Editor editor = mSharedPref.edit();
                                    editor.putString(EmployeeUid.getEmployeeUid(mFirstName, mLastName, mRole), "");
                                    editor.apply();
                                }
                            }));
                }

            }
        });

        employeeViewHolder.updateImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mId = String.valueOf(mEmployeeList.get(employeeViewHolder.getAdapterPosition()).getId());
                mFirstName = mEmployeeList.get(employeeViewHolder.getAdapterPosition()).getFirstName();
                mLastName = mEmployeeList.get(employeeViewHolder.getAdapterPosition()).getLastName();
                mRole = mEmployeeList.get(employeeViewHolder.getAdapterPosition()).getRole();
                Intent intent = new Intent(mContext, UpdateActivity.class);
                intent.putExtra(TOKEN, mToken);
                intent.putExtra(ID, mId);
                intent.putExtra(FIRST_NAME, mFirstName);
                intent.putExtra(LAST_NAME, mLastName);
                intent.putExtra(ROLE, mRole);
                mContext.startActivity(intent);
            }
        });

        employeeViewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(employeeViewHolder.getAdapterPosition(), employeeViewHolder.profileImageView);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mEmployeeList != null) {
            return mEmployeeList.size();
        } else {
            return 0;
        }
    }

    private boolean hasPhoneNumberLocally(String firstName, String lastName, String role) {
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String uid = EmployeeUid.getEmployeeUid(firstName, lastName, role);
        return mSharedPref.contains(uid);
    }

    public void setEmployeeList(ArrayList<Employee> employeeList) {
        mEmployeeList = employeeList;
        notifyDataSetChanged();
    }

    public class EmployeeViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView idTextView;
        TextView firstNameTextView;
        TextView lastNameTextView;
        TextView roleTextView;
        TextView phoneTextView;
        ConstraintLayout constraintLayout;
        ImageButton deleteImageButton;
        ImageButton updateImageButton;


        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.employee_item_holder);
            profileImageView = itemView.findViewById(R.id.profile_image_view);
            idTextView = itemView.findViewById(R.id.id_textView);
            firstNameTextView = itemView.findViewById(R.id.first_name_textView);
            lastNameTextView = itemView.findViewById(R.id.last_name_textView);
            roleTextView = itemView.findViewById(R.id.role_textView);
            phoneTextView = itemView.findViewById(R.id.phone_textView);
            deleteImageButton = itemView.findViewById(R.id.delete_imageButton);
            updateImageButton = itemView.findViewById(R.id.update_imageButton);
        }
    }

    public interface OnEmployeeItemClickListener {
        void onItemClick(int position, ImageView imageView);
    }

    public interface RefreshOnDeleteListener {
        void onRefresh();
    }

}
