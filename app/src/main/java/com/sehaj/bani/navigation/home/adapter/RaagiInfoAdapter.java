package com.sehaj.bani.navigation.home.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
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
import android.support.v4.util.Pair;

import com.bumptech.glide.Glide;
import com.sehaj.bani.R;
import com.sehaj.bani.navigation.home.raagi_detail.RaagiDetailActivity;
import com.sehaj.bani.rest.model.raagi.RaagiInfo;

import java.util.List;

/**
 * Created by ivesingh on 2/2/18.
 */

public class RaagiInfoAdapter extends RecyclerView.Adapter<RaagiInfoAdapter.RaagiInfoViewHolder> {

    private Context context;
    private List<RaagiInfo> raagiInfoList;

    public RaagiInfoAdapter(Context context, List<RaagiInfo> raagiInfoList){
        this.context = context;
        this.raagiInfoList = raagiInfoList;
    }

    @Override
    public RaagiInfoAdapter.RaagiInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_raagi, parent, false);

        return new RaagiInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RaagiInfoAdapter.RaagiInfoViewHolder holder, final int position) {
        final RaagiInfo raagiInfo = raagiInfoList.get(position);

        Glide.with(context).load(raagiInfo.getRaagiImageURL()).into(holder.raagi_thumbnail_IV);
        holder.raagi_name_TV.setText(raagiInfo.getRaagiName());
        holder.shabads_count_TV.setText(raagiInfo.getShabadsCount() + " shabads");

        holder.raagi_thumbnail_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create_intent(holder, raagiInfo);
            }
        });

        holder.raagi_name_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create_intent(holder, raagiInfo);
            }
        });

        holder.shabads_count_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create_intent(holder, raagiInfo);
            }
        });

        holder.raagi_menu_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.raagi_menu_IV);
            }
        });
    }

    private void create_intent(final RaagiInfoAdapter.RaagiInfoViewHolder holder, RaagiInfo raagiInfo){
            Intent intent = new Intent(context, RaagiDetailActivity.class);
            intent.putExtra("raagi_image_url", raagiInfo.getRaagiImageURL());
            intent.putExtra("raagi_name", raagiInfo.getRaagiName());
            intent.putExtra("num_of_shabads", raagiInfo.getShabadsCount());
            intent.putExtra("total_shabads_length", raagiInfo.getMinutesOfShabads());

            Pair<View, String> p1 = Pair.create((View)holder.raagi_thumbnail_IV, "raagi_image");
            Pair<View, String> p2 = Pair.create((View)holder.raagi_name_TV, "raagi_name");
            Pair<View, String> p3 = Pair.create((View)holder.shabads_count_TV, "raagi_shabads_count");
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, p1, p2, p3);
            context.startActivity(intent, activityOptionsCompat.toBundle());
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
        return raagiInfoList.size();
    }

    class RaagiInfoViewHolder extends RecyclerView.ViewHolder{

        private TextView raagi_name_TV, shabads_count_TV;
        private ImageView raagi_thumbnail_IV, raagi_menu_IV;

        public RaagiInfoViewHolder(View itemView) {
            super(itemView);
            raagi_thumbnail_IV = itemView.findViewById(R.id.raagi_thumbnail_IV);
            raagi_menu_IV = itemView.findViewById(R.id.raagi_menu_IV);
            raagi_name_TV = itemView.findViewById(R.id.raagi_name_TV);
            shabads_count_TV = itemView.findViewById(R.id.shabads_count_TV);
        }
    }

}
