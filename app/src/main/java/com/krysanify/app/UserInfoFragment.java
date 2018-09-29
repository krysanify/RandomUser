package com.krysanify.app;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.krysanify.lib.User;
import com.krysanify.lib.UserGen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.google.android.material.snackbar.Snackbar.LENGTH_SHORT;
import static com.krysanify.app.MainActivity.executor;
import static java.util.Locale.ENGLISH;

public class UserInfoFragment extends Fragment implements View.OnClickListener {

    private static final String LOCAL = "local";
    private static final String USER = "user";
    private User userInfo;

    private Button insert;
    private Button delete;

    static Bundle args(@NonNull User user, boolean local) {
        Bundle args = new Bundle();
        args.putBoolean(LOCAL, local);
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
        userInfo = user;

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

        insert = view.findViewById(R.id.btn_store);
        insert.setOnClickListener(this);

        delete = view.findViewById(R.id.btn_delete);
        delete.setOnClickListener(this);

        toggleAction(args.getBoolean(LOCAL));
        return view;
    }

    private void toggleAction(boolean isLocal) {
        if (isLocal) {
            insert.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
        } else {
            insert.setVisibility(View.VISIBLE);
            delete.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        final Handler uiHandler = v.getHandler();
        final UserGen service = UserGen.init(getContext());
        Runnable runnable = v == insert ? new Runnable() {
            @Override
            public void run() {
                service.insert(userInfo);
                uiHandler.post(inserted);
            }
        } : new Runnable() {
            @Override
            public void run() {
                service.delete(userInfo);
                uiHandler.post(deleted);
            }
        };
        executor().submit(runnable);
    }

    private final Runnable inserted = new Runnable() {
        @Override
        public void run() {
            if (null == getView()) return;
            Snackbar.make(getView(), R.string.snack_user_stored, LENGTH_SHORT).show();
            toggleAction(true);
        }
    };

    private final Runnable deleted = new Runnable() {
        @Override
        public void run() {
            if (null == getView()) return;
            Snackbar.make(getView(), R.string.snack_user_deleted, LENGTH_SHORT).show();
            toggleAction(false);
        }
    };
}
