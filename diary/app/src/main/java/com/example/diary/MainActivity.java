package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diary.interfaces.BotReply;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import androidx.recyclerview.widget.RecyclerView;
import com.example.diary.interfaces.BotReply;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.common.collect.Lists;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements BotReply {

    private TextView textView_Date;
    private DatePickerDialog.OnDateSetListener callbackMethod;
    public String fileName = "diary.txt";
    public String dFileName = "dTitle.txt";
    String Year;
    String month;
    String Day;
    String userid = "flysamsung";

    RecyclerView chatView;
    ChatAdapter chatAdapter;
    List<Message> messageList = new ArrayList<>();
    EditText editMessage;
    Button btnSend;

    private SessionsClient sessionsClient;
    private SessionName sessionName;
    private String uuid = UUID.randomUUID().toString();
    private String TAG = "mainactivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.InitializeView();
        this.InitializeListener();

        chatView = findViewById(R.id.chatView);
        editMessage = findViewById(R.id.content);
        btnSend = findViewById(R.id.btnSend);

        chatAdapter = new ChatAdapter(messageList, this);
        chatView.setAdapter(chatAdapter);

        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View view) {
                String message = editMessage.getText().toString();
                if(!message.isEmpty()) {
                    messageList.add(new Message(message, false));
                    editMessage.setText("");
                    sendMessageToBot(message);
                    Objects.requireNonNull(chatView.getAdapter()).notifyDataSetChanged();
                    Objects.requireNonNull(chatView.getLayoutManager()).scrollToPosition(messageList.size()-1);
                } else{
                    Toast.makeText(MainActivity.this, "Please enter text!", Toast.LENGTH_LONG).show();
                }
            }
        });
        setUpBot();
    }


    private void setUpBot() {
        try{
            InputStream stream = this.getResources().openRawResource(R.raw.credential);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            String projectId = ((ServiceAccountCredentials) credentials).getProjectId();

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(
                    FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            sessionName = SessionName.of(projectId, uuid);

            Log.d(TAG, "projectId : " + projectId);
        } catch (Exception e) {
            Log.d(TAG, "setUpBot: " + e.getMessage());
        }
    }

    private void sendMessageToBot(String message) {
        QueryInput input = QueryInput.newBuilder()
                .setText(TextInput.newBuilder().setText(message).setLanguageCode("en-US")).build();
        new SendMessageInBg(this, sessionName, sessionsClient, input).execute();
    }


    @Override
    public void callback(DetectIntentResponse returnResponse) {
        if(returnResponse!=null) {
            String botReply = returnResponse.getQueryResult().getFulfillmentText();
            if(!botReply.isEmpty()){
                messageList.add(new Message(botReply, true));
                chatAdapter.notifyDataSetChanged();
                Objects.requireNonNull(chatView.getLayoutManager()).scrollToPosition(messageList.size() - 1);
            }else {
                Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "failed to connect!", Toast.LENGTH_SHORT).show();
        }

    }

    public class diaryCreate extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try{
                FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
                PrintWriter out = new PrintWriter(fos, true);

                TextView cttTextView = findViewById(R.id.content);
                String contents = (String) cttTextView.getText();

                DiaryDAO doi = new DiaryDAO();
                doi.create(contents, userid);

                ArrayList<DiaryDTO> diaryInfo = DiaryDAO.read(userid);
                for(int i=0; i<diaryInfo.size(); i++){
                    DiaryDTO dInfo = diaryInfo.get(i);
                    String day = dInfo.get_cdate();
                    int id = dInfo.get_id();
                    String writeDay = day.substring(0,4) + "/" + day.substring(5,7) + "/" + day.substring(8,10);
                    out.println(writeDay + " " + id);
                }
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //select date
    public void InitializeView()
    {
        textView_Date = (TextView)findViewById(R.id.textView);
    }
    public void InitializeListener() {
        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Year = Integer.toString(year);
                month = Integer.toString(monthOfYear+1);
                Day = Integer.toString(dayOfMonth);

                textView_Date.setText(Year + "/" + month + "/" + Day);

            }
        };

        //click '날짜입력' button
        Button btnDate = findViewById(R.id.btnDate);
        btnDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, callbackMethod, 2021,1,1);
                dialog.show();
            }
        });

        //click '저장' button
        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 파일에 날짜 저장

                try {

                    new diaryCreate().execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        Button bJoin = (Button) findViewById(R.id.btnJoin);
        bJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 공유일기 제목 저장

                try {
                    FileOutputStream fos = openFileOutput(dFileName, Context.MODE_APPEND);
                    PrintWriter out = new PrintWriter(fos, true);

                    EditText name = (EditText) findViewById(R.id.shareDiary);
                    out.println(name.getText());
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        Button bCal = (Button) findViewById(R.id.btnCal);
        bCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Calender로 넘어가기

                Intent intent = new Intent(getApplicationContext(), displayCalender.class);
                startActivity(intent);

            }
        });

    }
}
