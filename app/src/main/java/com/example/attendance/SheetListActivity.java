package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.ArrayList;

public class SheetListActivity extends AppCompatActivity {
    private String className;
    private String subjectName;
    private TextView subtitle;
    private ListView sheetList;
    private ArrayAdapter adapter;
    private ArrayList<String> listItems=new ArrayList();
    private long cid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_list);
        Intent intent = getIntent();
        className = intent.getStringExtra("className");
        subjectName = intent.getStringExtra("subjectName");
        setToolbar();
        cid= getIntent().getLongExtra("cid",-1);
        Log.i("1234567890","onCreate: "+cid);
        loadListItems();
        sheetList=findViewById(R.id.sheetList);
        adapter=new ArrayAdapter(this,R.layout.sheed_list,R.id.date_list_item,listItems);
        sheetList.setAdapter(adapter);
        sheetList.setOnItemClickListener(((parent, view, position, id) -> openSheetActivity(position) ));

    }

    private void openSheetActivity(int position) {
        long[] idArray = getIntent().getLongArrayExtra("idArray");
        int [] rollArray=getIntent().getIntArrayExtra("rollArray");
        String[] nameArray=getIntent().getStringArrayExtra("nameArray");
        Intent intent= new Intent(this,SheetActivity.class);
        intent.putExtra("idArray",idArray);
        intent.putExtra("rollArray",rollArray);
        intent.putExtra("nameArray",nameArray);
        intent.putExtra("month",listItems.get(position));
        intent.putExtra("className",className);
        intent.putExtra("subjectName",subjectName);
        startActivity(intent);
    }

    private void loadListItems() {
        Cursor cursor=new DbHelper(this).getDistinctMonths(cid);
        while(cursor.moveToNext()){
            String date = cursor.getString(cursor.getColumnIndex(DbHelper.DATE_KEY));//01.05.2023
            listItems.add(date.substring(3));
        }
    }
    private void setToolbar(){
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);
        title.setText(className);
        subtitle.setText(subjectName);
        back.setOnClickListener(view -> onBackPressed());
    }

}