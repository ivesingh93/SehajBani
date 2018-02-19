package com.sehaj.bani.player.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.Patterns;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.sehaj.bani.R;
import com.sehaj.bani.player.ShabadPlayerActivity;

/**
 * Created by DELL on 1/29/2018.
 */

public class ShabadPlayerForegroundService extends Service {

    private static final String CHANNEL_ID = "PlayerShabad";
    private static final long MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000;
    private final IBinder mBinder = new LocalBinder();
    private Context context;
    private Timeline.Window window;
    private HeadphoneReceiver headphoneReceiver;
    private NotificationManager notificationManager;
    private int notification_id;
    private RemoteViews remoteViews;
    private DefaultTrackSelector trackSelector;
    private SimpleExoPlayer player;
    private DefaultDataSourceFactory dataSourceFactory;
    private ShabadPlayerForegroundService instance;
    private static boolean headsetConnected = false;
    private String[] shabad_links, shabad_titles;
    private String raagi_name;
    private int original_shabad_ind = 0;
    private int lastWindowIndex = -1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        window = new Timeline.Window();
        headphoneReceiver = new HeadphoneReceiver();
        notificationManager = new NotificationManager(context);
        remoteViews = new RemoteViews(getPackageName(), R.layout.notification_player);
        TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
        trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        player.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                super.onPlayerError(error);
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playWhenReady && playbackState == Player.STATE_READY){
                    showNotification();
                }else{
                    showNotification();
                    stopForeground(false);
                }
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                int latestWindowsIndex = player.getCurrentWindowIndex();
                if(latestWindowsIndex != lastWindowIndex){
                    showNotification();
                    Log.i("Player", lastWindowIndex + " " + latestWindowsIndex );
                    showShabad(latestWindowsIndex);
                    lastWindowIndex = latestWindowsIndex;
                }

            }
        });

        dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), "Exoplayer01");
        //register for headphone plug, notification buttons broadcast
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(MediaPlayerState.Action_Next);
        filter.addAction(MediaPlayerState.Action_Stop);
        filter.addAction(MediaPlayerState.Action_Pause_play);
        filter.addAction(MediaPlayerState.Action_Previous);
        registerReceiver(headphoneReceiver, filter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra(MediaPlayerState.SHABAD_LINKS)) {
            if (player.getPlayWhenReady()) {
                initPlayer();
            }

            raagi_name = intent.getStringExtra(MediaPlayerState.RAAGI_NAME);
            shabad_links = intent.getExtras().getStringArray(MediaPlayerState.SHABAD_LINKS);
            shabad_titles = intent.getExtras().getStringArray(MediaPlayerState.SHABAD_TITLES);
            original_shabad_ind = intent.getExtras().getInt(MediaPlayerState.ORIGINAL_SHABAD);
            setPlaylist(shabad_links, original_shabad_ind);

        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(headphoneReceiver);
    }

    public void play() {
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }

    public void pause() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    public void next() {
        if (player != null) {
            Timeline timeline = player.getCurrentTimeline();
            if (timeline.isEmpty()) {
                return;
            }
            int windowIndex = player.getCurrentWindowIndex();
            int nextWindowIndex = player.getNextWindowIndex();
            if (nextWindowIndex != C.INDEX_UNSET) {
                player.seekTo(nextWindowIndex, C.TIME_UNSET);
            } else if (timeline.getWindow(windowIndex, window, false).isDynamic) {
                player.seekTo(windowIndex, C.TIME_UNSET);
            }

        }

    }

    public void previous() {

        if (player != null) {
            Timeline timeline = player.getCurrentTimeline();
            if (timeline.isEmpty()) {
                return;
            }
            int windowIndex = player.getCurrentWindowIndex();
            int previousWindowIndex = player.getPreviousWindowIndex();
            if (previousWindowIndex != C.INDEX_UNSET) {
                player.seekTo(previousWindowIndex, C.TIME_UNSET);
            } else {
                player.seekTo(windowIndex,C.TIME_UNSET);
            }
        }
    }

    private void showShabad(int showShabadIndex){
        Intent intent = new Intent(MediaPlayerState.SHOW_SHABAD);
        intent.putExtra(MediaPlayerState.SHOW_SHABAD, showShabadIndex);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void stop() {
        lastWindowIndex = 0;
        player.setPlayWhenReady(false);
        stopForeground(true);
        stopSelf();
        log("Service Stopped and exit");
    }

    public SimpleExoPlayer getPlayer() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        }
        return player;
    }

    private void showNotification() {
        Intent prevIntent = new Intent();
        prevIntent.setAction(MediaPlayerState.Action_Previous);
        PendingIntent previousPI = PendingIntent.getBroadcast(context, MediaPlayerState.PREVIOUS, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pausePlayIntent = new Intent();
        pausePlayIntent.setAction(MediaPlayerState.Action_Pause_play);
        PendingIntent pausePlayPI = PendingIntent.getBroadcast(context, MediaPlayerState.PAUSE, pausePlayIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent();
        stopIntent.setAction(MediaPlayerState.Action_Stop);
        PendingIntent stopPI = PendingIntent.getBroadcast(context, MediaPlayerState.STOP, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent();
        nextIntent.setAction(MediaPlayerState.Action_Next);
        PendingIntent nextPI = PendingIntent.getBroadcast(context, MediaPlayerState.NEXT, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ShabadPlayerActivity.class), 0);


        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context, notificationManager.getMainNotificationId());
        } else {
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        }

        int play_pause_id = R.drawable.ic_play_circle_filled;
        if(player.getPlayWhenReady()){
            play_pause_id = R.drawable.ic_pause_circle_outline;
        }

        remoteViews.setImageViewResource(R.id.play_pause_IB, play_pause_id);

        remoteViews.setOnClickPendingIntent(R.id.skip_previous_IB, previousPI);
        remoteViews.setOnClickPendingIntent(R.id.play_pause_IB, pausePlayPI);
        remoteViews.setOnClickPendingIntent(R.id.skip_next_IB, nextPI);
        remoteViews.setOnClickPendingIntent(R.id.stop_IB, stopPI);
        remoteViews.setTextViewText(R.id.raagi_name_TV, raagi_name);
        remoteViews.setTextViewText(R.id.shabad_title_TV, shabad_titles[player.getCurrentWindowIndex()] );

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setCustomBigContentView(remoteViews)
                .setContentIntent(contentIntent)
                .setChannelId(CHANNEL_ID)
                .build();

        startForeground(MediaPlayerState.NOTIF_ID, builder.build());
    }

    private void initPlayer() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        } else {
            player.release();

        }
    }

    public void setPlaylist(String[] songUrl, int currentInd) {
        if (player != null) {
            player.prepare(buildMediaSource(songUrl));
            player.seekTo(currentInd, C.TIME_UNSET);
            play();
        }
    }

    private ConcatenatingMediaSource buildMediaSource(String[] url) {

        ExtractorMediaSource[] mediaS = new ExtractorMediaSource[url.length];
        for (int i = 0; i < url.length; i++) {
            Log.i("Shabad", url[i] + " " + i);
            if (Patterns.WEB_URL.matcher(url[i]).matches()) {
                mediaS[i] = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url[i]));
            }
        }

        return new ConcatenatingMediaSource(mediaS);


    }

    public void log(String message) {
        Log.e("Player Service", message);
    }

    public class LocalBinder extends Binder {
        public ShabadPlayerForegroundService getService() {
            return ShabadPlayerForegroundService.this;
        }
    }

    public class HeadphoneReceiver extends BroadcastReceiver {

        public HeadphoneReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();


            ServiceConnection mConnection = new ServiceConnection() {
                public void onServiceConnected(ComponentName className, IBinder service) {

                    instance = ((ShabadPlayerForegroundService.LocalBinder) service).getService();

                    log("service connected");

                }

                public void onServiceDisconnected(ComponentName className) {
                    instance = null;
                    log("Service disconnected");
//
                }
            };
            bindService(new Intent(context,
                    ShabadPlayerForegroundService.class), mConnection, Context.BIND_AUTO_CREATE);
            if (s != null) {
                switch (s) {
                    case MediaPlayerState.Action_Pause_play:
                        if (instance != null) {
                            if (instance.player.getPlayWhenReady()){
                                pause();
                            }else{
                                play();
                            }
                        }
                        break;
                    case MediaPlayerState.Action_Next:
                        if (instance != null) {
                            instance.next();
                            showShabad(player.getCurrentWindowIndex());
                        }

                        break;
                    case MediaPlayerState.Action_Previous:
                        if (instance != null) {
                            instance.previous();
                            showShabad(player.getCurrentWindowIndex());
                        }
                        break;

                    case MediaPlayerState.Action_Stop:
                        if(instance != null){
                            instance.stop();
                        }
                    default:
                        break;
                }
            }
            if (intent.hasExtra("state")) {
                if (headsetConnected && intent.getIntExtra("state", 0) == 0) {
                    headsetConnected = false;
                    if (instance.player != null && instance.player.getPlayWhenReady()) {
                        instance.pause();
                    }
                } else if (!headsetConnected && intent.getIntExtra("state", 0) == 1) {
                    headsetConnected = true;
                }
            }
        }
    }
}
