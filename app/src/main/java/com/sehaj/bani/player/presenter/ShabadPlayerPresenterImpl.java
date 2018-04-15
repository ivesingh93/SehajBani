package com.sehaj.bani.player.presenter;

import android.app.Activity;

import com.sehaj.bani.R;
import com.sehaj.bani.player.model.ShabadPlayerInteractor;
import com.sehaj.bani.player.model.ShabadPlayerInteractorImpl;
import com.sehaj.bani.player.view.ShabadPlayerView;
import com.sehaj.bani.rest.model.raagi.Shabad;

/**
 * Created by ivesingh on 2/4/18.
 */

public class ShabadPlayerPresenterImpl implements ShabadPlayerPresenter, ShabadPlayerInteractor.onFetchFinishedListener {

    private ShabadPlayerView shabadPlayerView;
    private ShabadPlayerInteractor shabadPlayerInteractor;

    public ShabadPlayerPresenterImpl(ShabadPlayerView shabadPlayerView, Activity context){
        this.shabadPlayerView = shabadPlayerView;
        this.shabadPlayerInteractor = new ShabadPlayerInteractorImpl(context);

    }

    @Override
    public void init() {
        shabadPlayerView.getIntentValues();
        shabadPlayerView.initUI();
        shabadPlayerView.showCustomAppbar();
        shabadPlayerView.generateShabadsData();
        shabadPlayerView.initPlayer();
    }

    @Override
    public void prepareTranslation(boolean teeka, boolean punjabi, boolean english) {

        if(!teeka && !punjabi && !english){
            shabadPlayerView.showGurmukhi();
        }else if(teeka && !punjabi && !english){
            shabadPlayerView.showGurmukhiTeeka();
        }else if(!teeka && punjabi && !english){
            shabadPlayerView.showGurmukhiPunjabi();
        }else if(!teeka && !punjabi && english){
            shabadPlayerView.showGurmukhiEnglish();
        }else if(teeka && punjabi && !english){
            shabadPlayerView.showGurmukhiTeekaPunjabi();
        }else if(teeka && !punjabi && english){
            shabadPlayerView.showGurmukhiTeekaEnglish();
        }else if(!teeka && punjabi && english) {
            shabadPlayerView.showGurmukhiPunjabiEnglish();
        }else if(teeka && punjabi && english){
            shabadPlayerView.showGurmukhiTeekaPunjabiEnglish();
        }
    }

    @Override
    public void prepareShabad(int startingId, int endingId) {
        shabadPlayerInteractor.fetchShabad(startingId, endingId, this);
    }

    @Override
    public void onShabadFetched(Shabad fetched_shabad) {
        shabadPlayerView.setFetchedShabadValues(fetched_shabad);
        prepareTranslation(false, false, false);
    }

    @Override
    public void changeShabadView(int color_position){
        int color_id = 0;
        if (color_position == 0){
            color_id = R.color.white;
        }else if(color_position == 1){
            color_id = R.color.black;
        }else if(color_position == 2){
            color_id = R.color.sepia;
        }else if(color_position == 3){
            color_id = R.color.green;
        }

        shabadPlayerView.changeShabadColor(color_id);
    }
}
