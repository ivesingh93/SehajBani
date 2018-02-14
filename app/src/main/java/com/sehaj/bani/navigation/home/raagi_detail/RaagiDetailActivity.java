package com.sehaj.bani.navigation.home.raagi_detail;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sehaj.bani.R;
import com.sehaj.bani.navigation.home.raagi_detail.adapter.ShabadAdapter;
import com.sehaj.bani.navigation.home.raagi_detail.presenter.RaagiPresenterImpl;
import com.sehaj.bani.navigation.home.raagi_detail.view.RaagiView;

public class RaagiDetailActivity extends AppCompatActivity implements RaagiView {


    private RaagiPresenterImpl raagiPresenterImpl;
    private ActionBar raagi_detail_AB;
    private RecyclerView shabad_RV;
    private ImageView raagi_thumbnail_IV, shabad_menu_IV;
    private TextView raagi_name_TV, shabads_count_TV;
    private RecyclerView.LayoutManager layoutManager;
    private String raagi_image_url, raagi_name;
    private int num_of_shabads, total_shabads_length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raagi_detail);

        raagi_image_url = getIntent().getStringExtra("raagi_image_url");
        raagi_name = getIntent().getStringExtra("raagi_name");
        num_of_shabads = getIntent().getIntExtra("num_of_shabads", 0);
        total_shabads_length = getIntent().getIntExtra("total_shabads_length", 0);

        raagiPresenterImpl = new RaagiPresenterImpl(this, RaagiDetailActivity.this);
        raagiPresenterImpl.init();
        raagiPresenterImpl.prepareRaagiInfo();
        raagiPresenterImpl.prepareShabads(raagi_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.shabad_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return true;
    }

    @Override
    public void init() {
        raagi_detail_AB = getSupportActionBar();
        layoutManager = new GridLayoutManager(this, 1);
        shabad_RV = findViewById(R.id.shabad_RV);
        raagi_thumbnail_IV = findViewById(R.id.raagi_thumbnail_IV);
        shabad_menu_IV = findViewById(R.id.shabad_menu_IV);
        raagi_name_TV = findViewById(R.id.raagi_name_TV);
        shabads_count_TV = findViewById(R.id.shabads_count_TV);
    }

    @Override
    public void showCustomTitleBar() {
        raagi_detail_AB.setTitle(getIntent().getStringExtra("raagi_name"));
        raagi_detail_AB.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void showRaagiInfo() {
        Glide.with(getApplicationContext()).load(raagi_image_url).into(raagi_thumbnail_IV);
        raagi_name_TV.setText(raagi_name);
        shabads_count_TV.setText(num_of_shabads + " shabads - " + total_shabads_length + " minutes");
    }

    @Override
    public void showShabads(ShabadAdapter shabadAdapter) {
        shabad_RV.setLayoutManager(layoutManager);
        //raagi_RV.addItemDecoration(new HomeFragment.GridSpacingItemDecoration(1, dpToPx(8), true));
        shabad_RV.setItemAnimator(new DefaultItemAnimator());
        shabad_RV.setAdapter(shabadAdapter);
        shabad_RV.setNestedScrollingEnabled(false);
    }
}
