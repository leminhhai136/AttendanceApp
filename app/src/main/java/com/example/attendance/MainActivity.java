package com.example.attendance;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    RecyclerView recyclerView;
    ClassAdapter classAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ClassItem> classItems = new ArrayList<>();
    Toolbar toolbar ;
    DbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper=new DbHelper(this);

        fab =findViewById(R.id.fab_main);
        fab.setOnClickListener( view ->  showDialog());

        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        String u = sharedPreferences.getString("username", "");
        if (!isLoggedIn()) {
            Intent intent;
            if(u.equals("admin")) {
                intent = new Intent(this, AdminActivity.class);
            } else {
                intent = new Intent(this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }


        loadData();

        recyclerView=findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        classAdapter= new ClassAdapter(this,classItems);
        recyclerView.setAdapter(classAdapter);
        classAdapter.setOnItemClickListener(position -> gotoItemActivity(position));

        setToolbar();
    }
    private boolean isLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    private void loadData() {
        Cursor cursor= dbHelper.getClassTable();
        classItems.clear();
        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(DbHelper.C_ID));
            String className=cursor.getString(cursor.getColumnIndex(DbHelper.CLASS_NAME_KEY));
            String subjectName=cursor.getString(cursor.getColumnIndex(DbHelper.SUBJECT_NAME_KEY));
            classItems.add(new ClassItem(id,className,subjectName));
        }
    }

    private void setToolbar() {
        toolbar=findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save=toolbar.findViewById(R.id.save);
        title.setText("Quản lí lớp học");
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

    private void gotoItemActivity(int position) {
        Intent intent=new Intent(MainActivity.this,StudentActivity.class);
        intent.putExtra("className",classItems.get(position).getClassName());
        intent.putExtra("subjectName",classItems.get(position).getSubjectName());
        intent.putExtra("position",position);
        intent.putExtra("cid",classItems.get(position).getCid());
        startActivity(intent);
    }

    private void showDialog(){
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(),MyDialog.CLASS_ADD_DIALOG);
        dialog.setListener((className,subjectName)->addClass(className,subjectName));
    }
    private void addClass(String className, String subjectName){
        long cid= dbHelper.addClass(className,subjectName);
        ClassItem classItem =new ClassItem(cid,className,subjectName);
        classItems.add(classItem);
        classAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 0:
                showUpdateDialog(item.getGroupId());
                break;
            case 1:
                deleteClass(item.getGroupId());

        }
        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(int position) {
        MyDialog dialog= new MyDialog();
        dialog.show(getSupportFragmentManager(),MyDialog.CLASS_UPDATE_DIALOG);
        dialog.setListener((className,subjectName)->updateClass(position,className,subjectName));
    }

    private void updateClass(int position, String className, String subjectName) {
        if (-1==dbHelper.updateClass(classItems.get(position).getCid(),className,subjectName));
        classItems.get(position).setClassName(className);
        classItems.get(position).setSubjectName(subjectName);
        classAdapter.notifyItemChanged(position);
    }

    private void deleteClass(int position) {
        dbHelper.deleteClass(classItems.get(position).getCid());
        classItems.remove(position);
        classAdapter.notifyItemRemoved(position);
    }
    private void changePassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Password");

        View view = LayoutInflater.from(this).inflate(R.layout.change_password_dialog, null);
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