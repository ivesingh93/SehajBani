package com.sehaj.bani.player.model;

import com.sehaj.bani.rest.model.raagi.Shabad;

/**
 * Created by ivesingh on 2/4/18.
 */

public interface ShabadPlayerInteractor {

    interface onFetchFinishedListener{
        void onShabadFetched(Shabad fetchedShabad);
    }

    void fetchShabad(int startingId, int endingId, onFetchFinishedListener listener);

}
