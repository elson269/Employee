package com.elsonji.test.ui.employee;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.elsonji.test.R;
import com.squareup.picasso.Picasso;

import static com.elsonji.test.utils.Constants.FIRST_NAME;
import static com.elsonji.test.utils.Constants.ID;
import static com.elsonji.test.utils.Constants.LAST_NAME;
import static com.elsonji.test.utils.Constants.ROLE;

public class EmployeeFragment extends Fragment {
    private String mId;
    private String mFirstName;
    private String mLastName;
    private String mRole;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_employee, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mId = bundle.getString(ID);
            mFirstName = bundle.getString(FIRST_NAME);
            mLastName = bundle.getString(LAST_NAME);
            mRole = bundle.getString(ROLE);
        }

        TextView idTextView = rootView.findViewById(R.id.id_textView);
        TextView firstNameTextView = rootView.findViewById(R.id.first_name_textView);
        TextView lastNameTextView = rootView.findViewById(R.id.last_name_textView);
        TextView roleTextView = rootView.findViewById(R.id.role_textView);

        idTextView.setText(mId);
        firstNameTextView.setText(mFirstName);
        lastNameTextView.setText(mLastName);
        roleTextView.setText(mRole);
        Picasso.get().load(R.drawable.avatar).into((ImageView) rootView.findViewById(R.id.profile_image_view));

        return rootView;
    }
}
