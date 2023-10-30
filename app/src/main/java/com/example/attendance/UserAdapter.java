package com.example.attendance;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.AccountViewHolder> {
    private List<User> userList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView username;
        TextView password;

        public AccountViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            username = itemView.findViewById(R.id.tvAccountName);
            password = itemView.findViewById(R.id.tvAccountPassword);

            itemView.setOnClickListener(v -> onItemClickListener.onClick(getAdapterPosition()));
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            menu.add(getAdapterPosition(), 0, 0, "EDIT");
            menu.add(getAdapterPosition(), 1, 0, "DELETE");
        }
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_item, parent, false);
        return new AccountViewHolder(itemView, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        holder.username.setText(userList.get(position).getUsername());
        holder.password.setText(userList.get(position).getPassword());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
