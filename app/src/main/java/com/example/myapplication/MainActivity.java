package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.example.myapplication.Extraction.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ContestListAdapter.ItemClicked {

    RecyclerView recyclerView;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ContestCard> contestsData;
    JSONArray contestList = new JSONArray();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        contestsData = new ArrayList<ContestCard>();
        GetOngoingContest getContestData = new GetOngoingContest(contestList);
        getContestData.start();
        try {
            getContestData.join();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        for(int i=0;i<contestList.size();i++) {
            contestsData.add(new ContestCard((Integer) ((JSONObject)contestList.get(i)).get("divId"), false, (String) ((JSONObject)contestList.get(i)).get("contestName")));
        }
        myAdapter = new ContestListAdapter(this, contestsData);
        myAdapter = new ContestListAdapter(this, contestsData);
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    public void onItemClicked(int index) {

        Toast.makeText(this, "Status: " + contestsData.get(index).isRegistered(), Toast.LENGTH_SHORT).show();
    }


}
