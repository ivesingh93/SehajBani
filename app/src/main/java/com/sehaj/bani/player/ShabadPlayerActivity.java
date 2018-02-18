package com.sehaj.bani.player;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.sehaj.bani.R;
import com.sehaj.bani.player.presenter.ShabadPlayerPresenterImpl;
import com.sehaj.bani.player.service.MediaPlayerState;
import com.sehaj.bani.player.service.ShabadPlayerForegroundService;
import com.sehaj.bani.player.view.ShabadPlayerView;
import com.sehaj.bani.rest.model.raagi.Shabad;

import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

import static com.sehaj.bani.Constants.BIG_FONT_SINGLE_BREAK;
import static com.sehaj.bani.Constants.DOUBLE_BREAK;
import static com.sehaj.bani.Constants.ENGLISH_FONT;
import static com.sehaj.bani.Constants.GURBANI_FONT;
import static com.sehaj.bani.Constants.PUNJABI_FONT;
import static com.sehaj.bani.Constants.SINGLE_BREAK;
import static com.sehaj.bani.Constants.TEEKA_ARTH_FONT;
import static com.sehaj.bani.Constants.TEEKA_PAD_ARTH_FONT;

public class ShabadPlayerActivity extends AppCompatActivity implements ShabadPlayerView {

    private ShabadPlayerPresenterImpl shabadPlayerPresenterImpl;
    private ActionBar shabad_player_AB;
    private TextView gurbani_TV, raagi_name_TV, shabad_title_TV;
    private Typeface gurbani_lipi_face;
    private ArrayList<Shabad> shabadsList;
    private Shabad current_shabad;

    private ScrollView gurbani_SV;
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private boolean isBound;
    private ShabadPlayerForegroundService shabadPlayerForegroundService;
    private String[] shabadLinks, shabadTitles;
    private int originalShabadIndex = 0;
    public ShowShabadReceiver showShabadReceiver;
    private ShabadDialog shabadDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shabad_player);

        shabadPlayerPresenterImpl = new ShabadPlayerPresenterImpl(this, ShabadPlayerActivity.this);
        shabadPlayerPresenterImpl.init();
        LocalBroadcastManager.getInstance(this).registerReceiver(showShabadReceiver, new IntentFilter(MediaPlayerState.SHOW_SHABAD));

        //TODO - When back button is pressed from this page and a new shabad is clicked, it doesn't play the shabad.
        //TODO - App crashes when on raagiDetail page and notification is on top and next/prev button is pressed.
        //TODO - Set Volume to Mid High

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shabad_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.action_like:
                //TODO - LIKE BUTTON
                Toast.makeText(getApplicationContext(), "Like", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_font_settings:
                shabadDialog.create_dialog_box();
                break;

        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        doUnbindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(showShabadReceiver);
    }

    private void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(ShabadPlayerActivity.this,
                ShabadPlayerForegroundService.class), mConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    private void doUnbindService() {
        if (isBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            isBound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            shabadPlayerForegroundService = ((ShabadPlayerForegroundService.LocalBinder) service).getService();
            player = shabadPlayerForegroundService.getPlayer();
            simpleExoPlayerView.setPlayer(player);

        }

        public void onServiceDisconnected(ComponentName className) {
            shabadPlayerForegroundService = null;
        }
    };

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("domain.com.player.MyForegroundPlayerService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void changeShabadColor(int color){
        gurbani_SV.setBackgroundResource(color);

    }

    public void onTranslationSelected(View view){
        boolean checked = ((CheckBox) view).isChecked();

        switch(view.getId()){
            case R.id.teeka_CB:
                shabadDialog.setTeeka(checked);
                break;

            case R.id.punjabi_CB:
                shabadDialog.setPunjabi(checked);
                break;

            case R.id.english_CB:
                shabadDialog.setEnglish(checked);
                break;
        }
        shabadPlayerPresenterImpl.prepareTranslation(shabadDialog.isTeeka(), shabadDialog.isPunjabi(), shabadDialog.isEnglish());
    }

    private String teeka_null_case(String pad_arth, String arth){
        String teeka = "";
        if(pad_arth.equals("") || arth.equals("")){
            if(pad_arth.equals("") && !arth.equals(""))
                teeka = TEEKA_ARTH_FONT + arth + DOUBLE_BREAK;

            if(!pad_arth.equals("") && arth.equals(""))
                teeka = TEEKA_PAD_ARTH_FONT + pad_arth + DOUBLE_BREAK;

            if(pad_arth.equals("") && arth.equals(""))
                teeka = SINGLE_BREAK;

        }else{
            teeka = TEEKA_PAD_ARTH_FONT + pad_arth + SINGLE_BREAK + TEEKA_ARTH_FONT + arth + DOUBLE_BREAK;
        }

        return teeka;
    }

    @Override
    public void initUI() {
        shabad_player_AB = getSupportActionBar();
        gurbani_TV = findViewById(R.id.gurbani_TV);
        raagi_name_TV = findViewById(R.id.raagi_name_TV);
        shabad_title_TV = findViewById(R.id.shabad_title_TV);
        gurbani_lipi_face = Typeface.createFromAsset(getAssets(), "fonts/gurblipi_.ttf");
        gurbani_TV.setTypeface(gurbani_lipi_face);
        shabadLinks = new String[shabadsList.size()];
        shabadTitles = new String[shabadsList.size()];
        showShabadReceiver = new ShowShabadReceiver();
        gurbani_SV = findViewById(R.id.gurbani_SV);
        shabadDialog = new ShabadDialog(this, shabadPlayerPresenterImpl);
        shabadPlayerPresenterImpl.changeShabadView(shabadDialog.getSharedPreferences().getInt("background_color_position", 0));

    }

    @Override
    public void showCustomAppbar() {
        shabad_player_AB.setDisplayShowTitleEnabled(false);
        shabad_player_AB.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void generateShabadsData(){

        for(int i = 0; i < shabadsList.size(); i++){
            shabadLinks[i] = shabadsList.get(i).getShabadUrl().replace(" ", "+");
            if(shabadsList.get(i).getShabadUrl().equals(current_shabad.getShabadUrl())){
                originalShabadIndex = i;
            }
            shabadTitles[i] = shabadsList.get(i).getShabadEnglishTitle();
        }

//        int j = 0;
//        shabadLinks[j] = current_shabad.getShabadUrl().replace(" ", "+");
//        for(int i = 0; i < nextShabads().size(); i++){
//            shabadLinks[j++] = nextShabads().get(i).getShabadUrl().replace(" ", "+");
//        }
//        for(int i = 0; i < previousShabads().size(); i++){
//            if(j == shabadLinks.length){
//                break;
//            }else{
//                shabadLinks[j++] = previousShabads().get(i).getShabadUrl().replace(" ", "+");
//            }
//
//        }
    }

    @Override
    public void initPlayer() {
        simpleExoPlayerView = findViewById(R.id.player);
        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        simpleExoPlayerView.setPlayer(player);

        if(!isServiceRunning()){
            Intent intent = new Intent(this, ShabadPlayerForegroundService.class);
            intent.putExtra(MediaPlayerState.RAAGI_NAME, current_shabad.getRaagiName());
            intent.putExtra(MediaPlayerState.SHABAD_TITLES, shabadTitles);
            intent.putExtra(MediaPlayerState.SHABAD_LINKS, shabadLinks);
            intent.putExtra(MediaPlayerState.ORIGINAL_SHABAD, originalShabadIndex);
            startService(intent);
            doBindService();
        }
    }

    @Override
    public void setFetchedShabadValues(Shabad fetched_shabad) {
        current_shabad.setShabadSize(fetched_shabad.getShabadSize());
        current_shabad.setGurmukhiList(fetched_shabad.getGurmukhiList());
        current_shabad.setPunjabiList(fetched_shabad.getPunjabiList());
        current_shabad.setTeekaPadArthList(fetched_shabad.getTeekaPadArthList());
        current_shabad.setTeekaArthList(fetched_shabad.getTeekaArthList());
        current_shabad.setEnglishList(fetched_shabad.getEnglishList());
    }

    private void showCurrentShabad(int showShabadIndex){
        current_shabad = shabadsList.get(showShabadIndex);
        shabad_player_AB.setTitle(current_shabad.getShabadEnglishTitle());
        raagi_name_TV.setText(current_shabad.getRaagiName());
        shabad_title_TV.setText(current_shabad.getShabadEnglishTitle());
        shabadPlayerPresenterImpl.prepareShabad(current_shabad.getStartingId(), current_shabad.getEndingId());
        shabadPlayerPresenterImpl.prepareTranslation(shabadDialog.isTeeka(), shabadDialog.isPunjabi(), shabadDialog.isEnglish());
    }

    @Override
    public void getIntentValues() {
        shabadsList = getIntent().getExtras().getParcelableArrayList("shabads");
        current_shabad = getIntent().getExtras().getParcelable("current_shabad");
    }

    @Override
    public void showGurmukhi() {

        StringBuilder shabad_text = new StringBuilder();
        for(int i = 0; i < current_shabad.getShabadSize(); i++){
            shabad_text.append(GURBANI_FONT + current_shabad.getGurmukhiList().get(i) + BIG_FONT_SINGLE_BREAK);
            shabad_text.append("<br>");
        }
        gurbani_TV.setText(Html.fromHtml(shabad_text.toString().replace("<>", "&lt&gt") + "<br><br>"));
    }

    @Override
    public void showGurmukhiTeeka() {
        StringBuilder shabad_text = new StringBuilder();
        for(int i = 0; i < current_shabad.getShabadSize(); i++){
            shabad_text.append(GURBANI_FONT + current_shabad.getGurmukhiList().get(i) + BIG_FONT_SINGLE_BREAK);
            shabad_text.append(teeka_null_case(current_shabad.getTeekaPadArthList().get(i), current_shabad.getTeekaArthList().get(i)));
        }
        gurbani_TV.setText(Html.fromHtml(shabad_text.toString().replace("<>", "&lt&gt") + "<br><br>"));
    }

    @Override
    public void showGurmukhiPunjabi() {
        StringBuilder shabad_text = new StringBuilder();
        for(int i = 0; i < current_shabad.getShabadSize(); i++){
            shabad_text.append(GURBANI_FONT + current_shabad.getGurmukhiList().get(i) + BIG_FONT_SINGLE_BREAK);
            shabad_text.append(PUNJABI_FONT + current_shabad.getPunjabiList().get(i) + DOUBLE_BREAK);
        }
        gurbani_TV.setText(Html.fromHtml(shabad_text.toString().replace("<>", "&lt&gt") + "<br><br>"));
    }

    @Override
    public void showGurmukhiEnglish() {
        StringBuilder shabad_text = new StringBuilder();
        for(int i = 0; i < current_shabad.getShabadSize(); i++){
            shabad_text.append(GURBANI_FONT + current_shabad.getGurmukhiList().get(i) + BIG_FONT_SINGLE_BREAK);
            shabad_text.append(ENGLISH_FONT + current_shabad.getEnglishList().get(i) + DOUBLE_BREAK);
        }
        gurbani_TV.setText(Html.fromHtml(shabad_text.toString().replace("<>", "&lt&gt") + "<br><br>"));
    }

    @Override
    public void showGurmukhiTeekaPunjabi() {
        StringBuilder shabad_text = new StringBuilder();
        for(int i = 0; i < current_shabad.getShabadSize(); i++){
            shabad_text.append(GURBANI_FONT + current_shabad.getGurmukhiList().get(i) + BIG_FONT_SINGLE_BREAK);
            shabad_text.append(PUNJABI_FONT + current_shabad.getPunjabiList().get(i) + SINGLE_BREAK);
            shabad_text.append(teeka_null_case(current_shabad.getTeekaPadArthList().get(i), current_shabad.getTeekaArthList().get(i)));
        }
        gurbani_TV.setText(Html.fromHtml(shabad_text.toString().replace("<>", "&lt&gt") + "<br><br>"));
    }

    @Override
    public void showGurmukhiTeekaEnglish() {
        StringBuilder shabad_text = new StringBuilder();
        for(int i = 0; i < current_shabad.getShabadSize(); i++){
            shabad_text.append(GURBANI_FONT + current_shabad.getGurmukhiList().get(i) + BIG_FONT_SINGLE_BREAK);
            shabad_text.append(ENGLISH_FONT + current_shabad.getEnglishList().get(i) + SINGLE_BREAK);
            shabad_text.append(teeka_null_case(current_shabad.getTeekaPadArthList().get(i), current_shabad.getTeekaArthList().get(i)));
        }
        gurbani_TV.setText(Html.fromHtml(shabad_text.toString().replace("<>", "&lt&gt") + "<br><br>"));
    }

    @Override
    public void showGurmukhiPunjabiEnglish() {
        StringBuilder shabad_text = new StringBuilder();
        for(int i = 0; i < current_shabad.getShabadSize(); i++){
            shabad_text.append(GURBANI_FONT + current_shabad.getGurmukhiList().get(i) + BIG_FONT_SINGLE_BREAK);
            shabad_text.append(PUNJABI_FONT + current_shabad.getPunjabiList().get(i) + SINGLE_BREAK);
            shabad_text.append(ENGLISH_FONT + current_shabad.getEnglishList().get(i) + DOUBLE_BREAK);
        }
        gurbani_TV.setText(Html.fromHtml(shabad_text.toString().replace("<>", "&lt&gt") + "<br><br>"));
    }

    @Override
    public void showGurmukhiTeekaPunjabiEnglish() {
        StringBuilder shabad_text = new StringBuilder();
        for(int i = 0; i < current_shabad.getShabadSize(); i++){
            shabad_text.append(GURBANI_FONT + current_shabad.getGurmukhiList().get(i) + BIG_FONT_SINGLE_BREAK);
            shabad_text.append(PUNJABI_FONT + current_shabad.getPunjabiList().get(i) + SINGLE_BREAK);
            shabad_text.append(ENGLISH_FONT + current_shabad.getEnglishList().get(i) + SINGLE_BREAK);
            shabad_text.append(teeka_null_case(current_shabad.getTeekaPadArthList().get(i), current_shabad.getTeekaArthList().get(i)));
        }
        gurbani_TV.setText(Html.fromHtml(shabad_text.toString().replace("<>", "&lt&gt") + "<br><br>"));
    }

    public class ShowShabadReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction() != null){
                int showShabadIndex = intent.getIntExtra(MediaPlayerState.SHOW_SHABAD, 0);
                showCurrentShabad(showShabadIndex);
            }

        }
    }
}
