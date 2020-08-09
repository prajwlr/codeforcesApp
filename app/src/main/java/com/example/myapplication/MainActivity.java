package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.example.myapplication.Extraction.*;
import com.google.android.material.navigation.NavigationView;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.example.myapplication.login.CREDENTIALS_PREF_FILENAME;

public class MainActivity extends AppCompatActivity implements ContestListAdapter.ItemClicked {
    private static final String location = "contestList.json";
    public static boolean islogin=false;
    RecyclerView recyclerView;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ContestCard> contestsData;
    SwipeRefreshLayout swipeRefreshLayout;
    DrawerLayout dl;
    ActionBarDrawerToggle actionBarDrawerToggle;
    TextView userName;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        islogin = getSharedPreferences(CREDENTIALS_PREF_FILENAME,MODE_PRIVATE).getBoolean("loginStatus",false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRecyclerView();
        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                setRecyclerView();
            }
        });
        dl=(DrawerLayout)findViewById(R.id.dl);
        actionBarDrawerToggle=new ActionBarDrawerToggle(this,dl,R.string.Open,R.string.Close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView nav_view = (NavigationView)findViewById(R.id.nav_view);
        Menu nav_Menu=nav_view.getMenu();
        if(islogin==false){
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
            headerView= nav_view.getHeaderView(0);
            userName= (TextView)headerView.findViewById(R.id.userName);
            userName.setText("Guest");
        }
        if(islogin==true){
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            headerView= nav_view.getHeaderView(0);
            userName= (TextView)headerView.findViewById(R.id.userName);
            userName.setText(getSharedPreferences(CREDENTIALS_PREF_FILENAME, MODE_PRIVATE).getString("handle","Guest"));
        }

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id==R.id.nav_login){
                    Intent intent = new Intent(MainActivity.this, login.class);
                    startActivity(intent);
                    return true;
                }

                if(id==R.id.nav_logout){
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    islogin=false;
                    startActivity(intent);

                    SharedPreferences.Editor deleteCredentials = getSharedPreferences(CREDENTIALS_PREF_FILENAME, MODE_PRIVATE).edit();
                    deleteCredentials.putString("handle","");
                    deleteCredentials.putString("password","");
                    deleteCredentials.putBoolean("loginStatus",false);
                    deleteCredentials.commit();

                    return true;
                }
                if(id==R.id.nav_profile){
                    Intent intent = new Intent(MainActivity.this, InfoPersonal.class);
                    startActivity(intent);
                    return true;
                }
                return true;
            }
        });

     }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(int index) {

        Toast.makeText(this, "Status: " + contestsData.get(index).isRegistered(), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed() {
        if(dl.isDrawerOpen(GravityCompat.START)){
            dl.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
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

