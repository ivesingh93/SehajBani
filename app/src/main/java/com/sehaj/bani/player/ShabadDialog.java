package com.sehaj.bani.player;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.sehaj.bani.R;
import com.sehaj.bani.player.presenter.ShabadPlayerPresenterImpl;

/**
 * Created by ivesingh on 2/17/18.
 */

public class ShabadDialog  {

    private Activity activity;
    private ShabadPlayerPresenterImpl shabadPlayerPresenterImpl;
    private ArrayAdapter<CharSequence> color_S_AA;
    private ImageButton zoom_out_IB, zoom_in_IB;
    private Spinner color_S;
    private CheckBox teeka_CB, punjabi_CB, english_CB;
    private boolean teeka, punjabi, english;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public ShabadDialog(Activity activity, ShabadPlayerPresenterImpl shabadPlayerPresenterImpl){
        this.activity = activity;
        this.shabadPlayerPresenterImpl = shabadPlayerPresenterImpl;
        sharedPreferences = activity.getSharedPreferences("shabadSettingsData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        teeka = false;
        punjabi = false;
        english = false;
    }

    public void create_dialog_box(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialogCustom);
        View view = activity.getLayoutInflater().inflate(R.layout.settings_shabad, null);
        process_settings(view);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void process_settings(View view){
        zoom_out_IB = view.findViewById(R.id.zoom_out_IB);
        zoom_in_IB = view.findViewById(R.id.zoom_in_IB);
        color_S = view.findViewById(R.id.color_S);
        teeka_CB = view.findViewById(R.id.teeka_CB);
        punjabi_CB = view.findViewById(R.id.punjabi_CB);
        english_CB = view.findViewById(R.id.english_CB);
        teeka_CB.setChecked(teeka);
        punjabi_CB.setChecked(punjabi);
        english_CB.setChecked(english);
        color_S_AA = ArrayAdapter.createFromResource(activity, R.array.colors, R.layout.support_simple_spinner_dropdown_item);
        color_S_AA.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        color_S.setAdapter(color_S_AA);
        color_S.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                shabadPlayerPresenterImpl.changeShabadView(i);
                editor.putInt("background_color_position", i);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        color_S.setSelection(getSharedPreferences().getInt("background_color_position", 0));
    }

    public boolean isTeeka() {
        return teeka;
    }

    public void setTeeka(boolean teeka) {
        this.teeka = teeka;
    }

    public boolean isPunjabi() {
        return punjabi;
    }

    public void setPunjabi(boolean punjabi) {
        this.punjabi = punjabi;
    }

    public boolean isEnglish() {
        return english;
    }

    public void setEnglish(boolean english) {
        this.english = english;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }
}




