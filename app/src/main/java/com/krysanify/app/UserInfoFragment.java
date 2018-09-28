package com.krysanify.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.krysanify.lib.User;
import com.krysanify.lib.UserGen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static java.util.Locale.ENGLISH;

public class UserInfoFragment extends Fragment implements View.OnClickListener {

    private static final String USER = "user";
    private User userInfo;

    static Bundle args(@NonNull User user) {
        Bundle args = new Bundle();
        args.putParcelable(USER, user);
        return args;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        Bundle args = getArguments();
        User user = null == args ? null : (User) args.getParcelable(USER);
        if (null == user) {
            return view;
        }

        TextView text;
        text = view.findViewById(R.id.edit_seed);
        text.setText(user.seed);

        text = view.findViewById(R.id.edit_name);
        text.setText(user.name);

        text = view.findViewById(R.id.edit_gender);
        text.setText(user.gender);

        text = view.findViewById(R.id.edit_age);
        text.setText(String.format(ENGLISH, "%d", user.getAge()));

        text = view.findViewById(R.id.edit_dob);
        text.setText(user.dob);

        text = view.findViewById(R.id.edit_email);
        text.setText(user.getEmail());

        view.findViewById(R.id.btn_store).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        UserGen.init(getContext()).insert(userInfo);
    }
}
