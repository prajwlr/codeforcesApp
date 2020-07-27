package com.example.myapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.example.myapplication.Extraction.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ContestListAdapter.ItemClicked {
    private static final String location = "contestList.json";

    RecyclerView recyclerView;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ContestCard> contestsData;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setRecyclerView();
//        setRecyclerView();
        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                setRecyclerView();
            }
        });
    }

    @Override
    public void onItemClicked(int index) {

        Toast.makeText(this, "Status: " + contestsData.get(index).isRegistered(), Toast.LENGTH_SHORT).show();
    }

    public JSONArray readFile() {
        FileInputStream fis = null;
        try {
            fis = openFileInput(location);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String content;
            while ((content = br.readLine()) != null) {
                sb.append(content).append("\n");
            }
            JSONParser parser = new JSONParser();
            return (JSONArray) parser.parse(sb.toString());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new JSONArray();
    }

    public void writeFile(JSONArray jsonArray) {
        String content = jsonArray.toJSONString();
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(location, MODE_PRIVATE);
            fos.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setRecyclerView() {
        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        contestsData = new ArrayList<ContestCard>();
        JSONObject contestListWrapper = new JSONObject();
        JSONArray contestList = new JSONArray();
        contestListWrapper.put("status", "false");
        contestListWrapper.put("result", contestList);
        GetOngoingContest getContestData = new GetOngoingContest(contestListWrapper);
        getContestData.start();
        try {
            getContestData.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String status = (String) contestListWrapper.get("status");
        if (status.equals("false")) {
            contestList = readFile();
        } else {
            writeFile(contestList);
        }
        for (int i = 0; i < contestList.size(); i++) {
            contestsData.add(new ContestCard((Long) ((JSONObject) contestList.get(i)).get("divId"), false, (String) ((JSONObject) contestList.get(i)).get("contestName")));
        }
        myAdapter = new ContestListAdapter(this, contestsData);
        myAdapter = new ContestListAdapter(this, contestsData);
        recyclerView.setAdapter(myAdapter);
    }

    public boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager=(ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo !=null;
    }
}

