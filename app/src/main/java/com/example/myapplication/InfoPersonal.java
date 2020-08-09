package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.myapplication.Extraction.getPersonalInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.myapplication.login.CREDENTIALS_PREF_FILENAME;

public class InfoPersonal extends AppCompatActivity {
    TextView info;
    AnyChartView anyChartView;
    List<String>tag=new ArrayList<>();
    List<Integer>value=new ArrayList<>();
  //  int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_personal);

        String userName = getSharedPreferences(CREDENTIALS_PREF_FILENAME, MODE_PRIVATE).getString("handle","");

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
            temp="Please Login";
        }
        else {
            temp = temp + "Username" + ": " + userName + "\n";
            for (Map.Entry<String, Integer> entry : finalOutput.entrySet()) {
               // temp = temp + entry.getKey() + "-->" + entry.getValue() + "\n";
                String g;
                int f;
                g = entry.getKey();
                f = entry.getValue();
                if(g=="unsuccessfulSubmissions"||g=="successfulSubmissions"){}
                else {tag.add(g);
                value.add(f);}
            }
        }
        info.setText(temp);
        //Pie chart info
        anyChartView=findViewById(R.id.any_chart_view);
        setupPieChart();
    }

    public void setupPieChart() {
        Pie pie= AnyChart.pie();
        List<DataEntry> dataEntries= new ArrayList<>();

        for(int i=0;i<tag.size();i++){
            dataEntries.add(new ValueDataEntry(tag.get(i),value.get(i)));
        }
        pie.data(dataEntries);
        anyChartView.setChart(pie);
    }
}