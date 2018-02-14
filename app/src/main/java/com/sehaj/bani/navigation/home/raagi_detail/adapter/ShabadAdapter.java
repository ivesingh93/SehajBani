package com.sehaj.bani.navigation.home.raagi_detail.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sehaj.bani.R;
import com.sehaj.bani.player.ShabadPlayerActivity;
import com.sehaj.bani.rest.model.raagi.Shabad;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivesingh on 2/3/18.
 */

public class ShabadAdapter extends RecyclerView.Adapter<ShabadAdapter.ShabadViewHolder>{

    private Context context;
    private ArrayList<Shabad> shabadList;

    public ShabadAdapter(Context context, ArrayList<Shabad> shabadList){
        this.context = context;
        this.shabadList = shabadList;
    }

    @Override
    public ShabadAdapter.ShabadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_shabad, parent, false);

        return new ShabadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ShabadAdapter.ShabadViewHolder holder, final int position) {
        final Shabad shabad = shabadList.get(position);
        holder.shabad_title_TV.setText(shabad.getShabadEnglishTitle());
        holder.shabads_length_TV.setText(shabad.getShabadLength());

        holder.shabad_thumbnail_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create_intent(holder, shabad);
            }
        });

        holder.shabad_title_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create_intent(holder, shabad);
            }
        });

        holder.shabads_length_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create_intent(holder, shabad);
            }
        });

        holder.shabad_menu_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.shabad_menu_IV);
            }
        });

    }

    private void create_intent(final ShabadAdapter.ShabadViewHolder holder, Shabad shabad){
        Intent intent = new Intent(context, ShabadPlayerActivity.class);
        intent.putParcelableArrayListExtra("shabads", shabadList);
        intent.putExtra("current_shabad", shabad);
//        intent.putExtra("shabad_english_title", shabad.getShabadEnglishTitle());
//        intent.putExtra("shabad_length", shabad.getShabadLength());
//        intent.putExtra("sathaayi_id", shabad.getSathaayiId());
//        intent.putExtra("starting_id", shabad.getStartingId());
//        intent.putExtra("ending_id", shabad.getEndingId());
//        intent.putExtra("shabad_url", shabad.getShabadUrl());
//        intent.putExtra("raagi_name", shabad.getRaagiName());

        //TODO - Animate shared elements

        context.startActivity(intent);
    }

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.raagi_card, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.add_favorite:
                        Toast.makeText(context, "Add Favorite", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.play_now:
                        Toast.makeText(context, "Play now", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });
        popup.show();
    }

    @Override
    public int getItemCount() {
        return shabadList.size();
    }

    class ShabadViewHolder extends RecyclerView.ViewHolder{

        private ImageView shabad_thumbnail_IV, shabad_menu_IV;
        private TextView shabad_title_TV, shabads_length_TV;


        public ShabadViewHolder(View itemView) {
            super(itemView);
            shabad_thumbnail_IV = itemView.findViewById(R.id.shabad_thumbnail_IV);
            shabad_title_TV = itemView.findViewById(R.id.shabad_title_TV);
            shabads_length_TV = itemView.findViewById(R.id.shabads_length_TV);
            shabad_menu_IV = itemView.findViewById(R.id.shabad_menu_IV);
        }
    }
}