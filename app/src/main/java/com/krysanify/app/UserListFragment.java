package com.krysanify.app;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.krysanify.lib.User;
import com.krysanify.lib.UserGen;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import static com.krysanify.app.MainActivity.executor;

/**
 *
 */
public class UserListFragment extends Fragment implements UserGen.Callback, View.OnClickListener {

    private static final String LIMIT = "limit";
    private RecyclerView recyclerView;
    private UserListAdapter listAdapter;
    private boolean isLocal;

    static Bundle args(int limit) {
        Bundle args = new Bundle();
        args.putInt(LIMIT, limit);
        return args;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        recyclerView = view.findViewById(R.id.list);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (null == args) return;

        if (null != listAdapter) {
            recyclerView.setAdapter(listAdapter);
            return;
        }

        int limit = args.getInt(LIMIT);
        if (limit > 0) {
            UserGen.get().getList(limit, this);
            return;
        }

        isLocal = true;
        getLocalList(view.getHandler());
    }

    private void getLocalList(final Handler uiHandler) {
        final UserGen service = UserGen.init(getContext());
        executor().submit(new Runnable() {
            @Override
            public void run() {
                List<User> list = service.getLocal(0, 20);
                uiHandler.post(asyncList.apply(list));
            }
        });
    }

    @Override
    public void onGenerated(List<User> list) {
        if (isDetached()) return;
        listAdapter = new UserListAdapter(list, this);
        recyclerView.setAdapter(listAdapter);
    }

    @Override
    public void onClick(View v) {
        User user = (User) v.getTag();
        FragmentActivity parent = getActivity();
        if (null == user || null == parent) return;

        Bundle args = UserInfoFragment.args(user, isLocal);
        Navigation.findNavController(parent, R.id.nav_host_fragment)
                .navigate(R.id.user_info_fragment, args);
    }

    private final AsyncList asyncList = new AsyncList() {
        private List<User> _list;

        @Override
        public Runnable apply(List<User> list) {
            _list = list;
            return this;
        }

        @Override
        public void run() {
            onGenerated(_list);
        }
    };

    interface AsyncList extends Runnable {
        Runnable apply(List<User> list);
    }
}
