package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContestListAdapter extends RecyclerView.Adapter<ContestListAdapter.ViewHolder>
{
    private ArrayList<ContestCard> contestsData;
    ItemClicked activity;

    public interface ItemClicked
    {
        void onItemClicked(int index);
    }
    public void clear(){

    }
    public ContestListAdapter (Context context, ArrayList<ContestCard> list)
    {
        contestsData = list;
        activity = (ItemClicked) context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView divImageId;
        TextView contestNameId;
        CheckBox checkBoxId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            divImageId = itemView.findViewById(R.id.divImageId);
            contestNameId = itemView.findViewById(R.id.contestNameId);
            checkBoxId = itemView.findViewById(R.id.checkBoxId);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    activity.onItemClicked(contestsData.indexOf((ContestCard) view.getTag()));
                }
            });
        }
    }

    @NonNull
    @Override
    public ContestListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contests_list_layout, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ContestListAdapter.ViewHolder viewHolder, int i) {

        viewHolder.itemView.setTag(contestsData.get(i));

        viewHolder.contestNameId.setText(contestsData.get(i).getContestName());

        if (contestsData.get(i).getDivision()==1)
        {
            viewHolder.divImageId.setImageResource(R.drawable.div_1);
        }
        else if (contestsData.get(i).getDivision()==2)
        {
            viewHolder.divImageId.setImageResource(R.drawable.div_2);
        }else if (contestsData.get(i).getDivision()==3)
        {
            viewHolder.divImageId.setImageResource(R.drawable.div_3);
        }else
        {
            viewHolder.divImageId.setImageResource(R.drawable.div_unknown);
        }
        viewHolder.checkBoxId.setChecked(contestsData.get(i).isRegistered());

    }

    @Override
    public int getItemCount() {
        return contestsData.size();
    }
}
