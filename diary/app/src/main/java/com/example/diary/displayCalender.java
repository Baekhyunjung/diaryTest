package com.example.diary;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class displayCalender extends AppCompatActivity {

    String fileName = "diary.txt";
    String dFileName = "dTitle.txt";
    ArrayList<CalendarDay> dates;
    CalendarDay myDate;
    String userid = "flysamsung";
    Map<String, String> diaryAll;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cal);

        dates = new ArrayList<>();

        MaterialCalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setSelectedDate(CalendarDay.today());

        OneDayDecorator oneDayDecorator = new OneDayDecorator();
        calendarView.addDecorators(oneDayDecorator);

        calendarView.setSelectedDate(CalendarDay.today());
        calendarView.addDecorators(new SaturdayDecorator());
        calendarView.addDecorators(new SundayDecorator());

        try {
            FileInputStream fis = openFileInput(fileName);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));
            String str = buffer.readLine();

            while (str != null) {
                String[] date = str.split("/");
                String stryear = date[0];
                String strmonth = date[1];
                String strday = date[2];

                int year = Integer.parseInt(stryear);
                int month = Integer.parseInt(strmonth) - 1;
                int day = Integer.parseInt(strday);

                myDate = CalendarDay.from(year, month, day);
                dates.add(myDate);

                str = buffer.readLine();
            }
            buffer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        diaryAll = (Map<String, String>) new diaryRead().execute();
        calendarView.addDecorator(new EventDecorator(Color.RED, dates));


        LinearLayout layout = (LinearLayout)findViewById(R.id.container);
        LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        try {
            FileInputStream fis = openFileInput(dFileName);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));
            String str = buffer.readLine();

            //TextView titleList = (TextView)findViewById(R.id.dTitle);
            while (str != null) {
                TextView tv = new TextView(this);  // 새로 추가할 textView 생성
                tv.setText(str);  // textView에 내용 추가
                tv.setLayoutParams(layoutParam);  // textView layout 설정
                tv.setGravity(Gravity.CENTER);
                layout.addView(tv); // 기존 linearLayout에 textView 추가

                str = buffer.readLine();
            }
            buffer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Button btnModify = (Button) findViewById(R.id.modify);
        btnModify.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new diaryModify().execute();
            }

        });

        Button btnDelete = (Button) findViewById(R.id.delete);
        btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new diaryDelete().execute();
            }
        });
    }

    public class diaryRead extends AsyncTask<Void, Void, Map<String, String>> {

        @Override
        protected Map<String, String> doInBackground(Void... params) {
            Map<String, String> dAll = new HashMap<String, String>();
            try{
                ArrayList<DiaryDTO> diaryInfo = DiaryDAO.read(userid);
                for(int i=0; i<diaryInfo.size(); i++){
                    DiaryDTO dInfo = diaryInfo.get(i);
                    String day = dInfo.get_cdate();
                    int id = dInfo.get_id();
                    String writeDay = day.substring(0,4) + "/" + day.substring(5,7) + "/" + day.substring(8,10);
                    dAll.put(writeDay, dInfo.get_contents());

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dAll;
        }
    }

    public class diaryModify extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {



            return null;
        }
    }

    public class diaryDelete extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {



            return null;
        }
    }

    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) throws IOException {

        TextView content = (TextView)findViewById(R.id.content);
        String d = date.toString();
        String day = d.substring(0,4) + d.substring(5,7) + d.substring(8,10);
        String ctt = diaryAll.get(day);

        content.setText(ctt);
        
    }

}