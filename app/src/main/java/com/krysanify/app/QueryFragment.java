package com.krysanify.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.krysanify.lib.Strings;
import com.krysanify.lib.User;
import com.krysanify.lib.UserGen;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;


/**
 * Display fragment to show query form.
 */
public class QueryFragment extends Fragment implements View.OnClickListener, UserGen.Callback {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query, container, false);
        view.findViewById(R.id.btn_query).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        EditText editSeed = getView().findViewById(R.id.edit_seed);
        EditText editLimit = getView().findViewById(R.id.edit_limit);
        Spinner pickGender = getView().findViewById(R.id.pick_gender);

        String seed = editSeed.getText().toString();
        if (!Strings.isBlank(seed)) {
            UserGen.get().getBySeed(seed, this);
            return;
        }

        String limit = editLimit.getText().toString();
        if (Strings.isNumber(limit)) {
            UserGen.get().getList(Integer.parseInt(limit), this);
            return;
        }

        String gender = pickGender.getSelectedItem().toString();
        UserGen.get().getByGender(gender, this);
    }

    @Override
    public void onGenerated(List<User> list) {
        FragmentActivity parent = getActivity();
        if (null == parent) return;
        if (list.isEmpty()) throw new IllegalArgumentException("Empty list!");
        else if (1 == list.size()) {
            Bundle args = UserInfoFragment.args(list.get(0));
            Navigation.findNavController(parent, R.id.nav_fragment)
                .navigate(R.id.user_info_fragment, args);
        }
    }
}
