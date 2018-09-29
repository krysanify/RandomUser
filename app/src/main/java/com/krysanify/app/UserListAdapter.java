package com.krysanify.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.krysanify.lib.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static java.util.Locale.ENGLISH;

/**
 *
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private final List<User> mValues;
    private final View.OnClickListener mListener;

    UserListAdapter(List<User> items, View.OnClickListener listener) {
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.bind(mValues.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View root;
        final TextView nameText;
        final TextView genderText;
        final TextView emailText;

        ViewHolder(View view) {
            super(view);
            root = view;
            nameText = view.findViewById(R.id.item_name);
            genderText = view.findViewById(R.id.item_gender);
            emailText = view.findViewById(R.id.item_email);
        }

        @NonNull
        @Override
        public String toString() {
            return String.format(ENGLISH, "%s > %s", super.toString(), emailText.getText());
        }

        void bind(User user, View.OnClickListener listener) {
            nameText.setText(user.name);
            genderText.setText(user.gender);
            emailText.setText(user.getEmail());

            root.setTag(user);
            root.setOnClickListener(listener);
        }
    }
}
