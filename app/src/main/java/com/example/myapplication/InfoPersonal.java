package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.Extraction.getPersonalInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class InfoPersonal extends AppCompatActivity {
    TextView info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_personal);
        String userName = "abcdlgf";
        Map<String, Integer> finalOutput= new HashMap<>();
        getPersonalInfo getInfo = new getPersonalInfo(userName, finalOutput);
        getInfo.start();
        try {
            getInfo.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        info = findViewById(R.id.personalInfo);
        String temp= "";
        if(finalOutput.get("status")==0){
            temp="Some error occurred. Try again";
        }
        else if(finalOutput.get("status")==-1){
            temp="Wrong Username";
        }
        else {
            temp = temp + "Username" + "----->" + userName + "\n";
            for (Map.Entry<String, Integer> entry : finalOutput.entrySet()) {
                temp = temp + entry.getKey() + "-->" + entry.getValue() + "\n";
            }
        }
        info.setText(temp);
    }
}