package com.example.attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ArrayList<User> userList;
    private DbHelper dbHelper;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dbHelper = new DbHelper(this);

        loadData();

        initUI();
        setToolbar();
    }

    private void loadData() {
        userList = dbHelper.getAllUsers();

    }

    private void initUI() {
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        userAdapter = new UserAdapter(userList);
        recyclerView.setAdapter(userAdapter);
        userAdapter.setOnItemClickListener(this::showEditUserDialog);

        FloatingActionButton fabAdmin = findViewById(R.id.fab_admin);
        fabAdmin.setOnClickListener(v -> showAddUserDialog());
    }

    private void setToolbar() {
        toolbar=findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save=toolbar.findViewById(R.id.save);
        title.setText("Quản lí tài khoản");
        subtitle.setVisibility(View.GONE);
        back.setVisibility(View.INVISIBLE);
        save.setVisibility(View.INVISIBLE);
        toolbar.inflateMenu(R.menu.user_menu);
        toolbar.setOnMenuItemClickListener(menuItem -> onMenuItemClick(menuItem));
    }



    private boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.change_password) {
            changePassword();
        } else if (menuItem.getItemId() == R.id.logout) {
            SharedPreferences preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("is_logged_in");
            editor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 0:
                showEditUserDialog(item.getGroupId());
                break;
            case 1:
                deleteUser(item.getGroupId());
        }
        return super.onContextItemSelected(item);
    }
    private void deleteUser(int position) {
        User user = userList.get(position);
        long result = dbHelper.deleteUser(user.getId());

        if (result != -1) {
            userList.remove(position);
            userAdapter.notifyItemRemoved(position);
            Toast.makeText(this, "Đã xóa tài khoản!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Không thể xóa tài khoản!", Toast.LENGTH_SHORT).show();
        }
    }
    private void showEditUserDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa Tài Khoản");

        View view = getLayoutInflater().inflate(R.layout.edit_user_dialog, null);
        builder.setView(view);

        EditText usernameEditText = view.findViewById(R.id.editUsername);
        EditText passwordEditText = view.findViewById(R.id.editPassword);

        User user = userList.get(position);
        usernameEditText.setText(user.getUsername());
        passwordEditText.setText(user.getPassword());

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newUsername = usernameEditText.getText().toString();
            String newPassword = passwordEditText.getText().toString();

            if (!newUsername.isEmpty() && !newPassword.isEmpty()) {
                if (newUsername.equals(user.getUsername())) {
                    long result = dbHelper.updateUser(user.getId(), newUsername, newPassword);
                    if (result != -1) {
                        Toast.makeText(this, "Tài khoản đã được cập nhật!", Toast.LENGTH_SHORT).show();
                        userList.get(position).setUsername(newUsername);
                        userList.get(position).setPassword(newPassword);
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Không thể cập nhật tài khoản!", Toast.LENGTH_SHORT).show();
                    }
                } else if (dbHelper.isUsernameExists(newUsername)) {
                    Toast.makeText(this, "Tên đăng nhập đã tồn tại. Vui lòng chọn tên đăng nhập khác.", Toast.LENGTH_SHORT).show();
                } else {
                    long result = dbHelper.updateUser(user.getId(), newUsername, newPassword);
                    if (result != -1) {
                        Toast.makeText(this, "Tài khoản đã được cập nhật!", Toast.LENGTH_SHORT).show();
                        userList.get(position).setUsername(newUsername);
                        userList.get(position).setPassword(newPassword);
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Không thể cập nhật tài khoản!", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập tên đăng nhập và mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm Tài Khoản");

        View view = getLayoutInflater().inflate(R.layout.add_user_dialog, null);
        builder.setView(view);

        EditText usernameEditText = view.findViewById(R.id.username);
        EditText passwordEditText = view.findViewById(R.id.password);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String newUsername = usernameEditText.getText().toString();
            String newPassword = passwordEditText.getText().toString();

            if (!newUsername.isEmpty() && !newPassword.isEmpty()) {
                if (dbHelper.isUsernameExists(newUsername)) {
                    Toast.makeText(this, "Tên đăng nhập đã tồn tại. Vui lòng chọn tên đăng nhập khác.", Toast.LENGTH_SHORT).show();
                } else {
                    long result = dbHelper.addUser(newUsername, newPassword);
                    if (result != -1) {
                        User us = new User(newUsername, newPassword);
                        userList.add(us);
                        Toast.makeText(this, "Tài khoản đã được thêm!", Toast.LENGTH_SHORT).show();
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Không thể thêm tài khoản!", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập tên đăng nhập và mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }


    private void changePassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Password");

        View view = getLayoutInflater().inflate(R.layout.change_password_dialog, null);
        builder.setView(view);

        EditText currentPasswordEditText = view.findViewById(R.id.current_password);
        EditText newPasswordEditText = view.findViewById(R.id.new_password);
        EditText confirmPasswordEditText = view.findViewById(R.id.confirm_password);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String currentPassword = currentPasswordEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");

            if (newPassword.equals(confirmPassword)) {
                if (dbHelper.checkCurrentPassword(username, currentPassword)) {
                    dbHelper.updateUserPassword(username, newPassword);
                    Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
}
