package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.Calendar;

public class SheetActivity extends AppCompatActivity {
    TextView subtitle;
    String className;
    String subjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet);
        Intent intent = getIntent();
        className = intent.getStringExtra("className");
        subjectName = intent.getStringExtra("subjectName");
        setToolbar();
        showTable();

    }

    private void showTable() {
        DbHelper dbHelper = new DbHelper(this);
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        long[] idArray = getIntent().getLongArrayExtra("idArray");
        int[] rollArray = getIntent().getIntArrayExtra("rollArray");
        String[] nameArray = getIntent().getStringArrayExtra("nameArray");
        String month = getIntent().getStringExtra("month");
        int DAY_IN_MONTH = getDayInMonth(month);
        int rowSize = idArray.length + 1;

        TableRow[] rows = new TableRow[rowSize];
        TextView[] roll_tvs = new TextView[rowSize];
        TextView[] name_tvs = new TextView[rowSize];
        TextView[][] status_tvs = new TextView[rowSize][DAY_IN_MONTH + 1];

        for (int i = 0; i < rowSize; i++) {
            roll_tvs[i] = new TextView(this);
            name_tvs[i] = new TextView(this);
            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                status_tvs[i][j] = new TextView(this);
            }
        }

        // Header
        roll_tvs[0].setText("STT");
        roll_tvs[0].setTypeface(roll_tvs[0].getTypeface(), Typeface.BOLD);
        name_tvs[0].setText("TÊN");
        name_tvs[0].setTypeface(roll_tvs[0].getTypeface(), Typeface.BOLD);
        for (int i = 1; i <= DAY_IN_MONTH; i++) {
            status_tvs[0][i].setText(String.valueOf(i));
            status_tvs[0][i].setTypeface(status_tvs[0][i].getTypeface(), Typeface.BOLD);
        }

        for (int i = 1; i < rowSize; i++) {
            roll_tvs[i].setText(String.valueOf(rollArray[i - 1]));
            name_tvs[i].setText(nameArray[i - 1]);
            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                String day = String.valueOf(j);
                if (day.length() == 1) {
                    day = "0" + day;
                }
                String date = day + "." +month;
                String status = dbHelper.getStatus(idArray[i - 1], date);
                status_tvs[i][j].setText(status);
            }
        }

        for (int i = 0; i < rowSize; i++) {
            rows[i] = new TableRow(this);
            if (i % 2 == 0) {
                rows[i].setBackgroundColor(Color.parseColor("#EEEEEE"));
            } else {
                rows[i].setBackgroundColor(Color.parseColor("#E4E4E4"));
            }

            roll_tvs[i].setPadding(16, 16, 16, 16);
            name_tvs[i].setPadding(16, 16, 16, 16);

            rows[i].addView(roll_tvs[i]);
            rows[i].addView(name_tvs[i]);

            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                status_tvs[i][j].setPadding(16, 16, 16, 16);
                rows[i].addView(status_tvs[i][j]);
            }

            tableLayout.addView(rows[i]);
        }
    }

    private int getDayInMonth(String month) {
        String[] parts = month.split("\\.");
        if (parts.length == 2) {
            int monthIndex = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, monthIndex - 1);
            calendar.set(Calendar.YEAR, year);

            return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            return 0;
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
