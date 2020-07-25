package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ContestListAdapter.ItemClicked{

    RecyclerView recyclerView;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<ContestCard> contestsData;
    Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

//        btnAdd = findViewById(R.id.btnAdd);
//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                ContestCard.add(new Person("Susan", "Peters", "plane"));
//                myAdapter.notifyDataSetChanged();
//
//            }
//        });

        layoutManager = new LinearLayoutManager(this);
        //layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        layoutManager = new GridLayoutManager(this,2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        contestsData = new ArrayList<ContestCard>();
        contestsData.add(new ContestCard(1, true, "bus"));
        contestsData.add(new ContestCard(2, false, "bus"));
        contestsData.add(new ContestCard(3, true, "busdsfb"));
        contestsData.add(new ContestCard(0, true, "bus"));
        contestsData.add(new ContestCard(1, false, "busbdfv"));
        contestsData.add(new ContestCard(2, true, "bus"));
        contestsData.add(new ContestCard(2, true, "bus"));
        contestsData.add(new ContestCard(4, true, "bus"));
        contestsData.add(new ContestCard(5, true, "bus"));
        contestsData.add(new ContestCard(3, true, "bus"));


        myAdapter = new ContestListAdapter(this, contestsData);

        recyclerView.setAdapter(myAdapter);

    }

    @Override
    public void onItemClicked(int index) {

        Toast.makeText(this, "Status: " + contestsData.get(index).isRegistered(), Toast.LENGTH_SHORT).show();
//        contestsData.get(index).setRegistered(!(contestsData.get(index).isRegistered()));
//        myAdapter.notifyDataSetChanged();
    }
}