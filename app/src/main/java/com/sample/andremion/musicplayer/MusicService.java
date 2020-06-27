package com.sample.andremion.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.sample.andremion.musicplayer.music.MusicContent;

public class MusicService extends Service implements IMusicPlayer {
    private MediaPlayer mediaPlayer;
    private IBinder iBinder;
    private MusicContent.MusicItem musicItem;
    private Worker worker;

    public MusicService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        iBinder = new LocalBinder();
        mediaPlayer = new MediaPlayer();

    }

    @Override
    public IBinder onBind(Intent intent) {
        try {
            musicItem = (MusicContent.MusicItem) intent.getSerializableExtra("musicItem");
            String path = musicItem.getPath();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        super.onDestroy();
    }


    @Override
    public void play() {
        if (mediaPlayer != null)
            mediaPlayer.start();

        if (worker == null) {
            worker = new Worker();
            worker.start();
        } else {
            worker.doResume();
        }
    }

    @Override
    public void stop() {
        if (mediaPlayer != null)
            mediaPlayer.stop();
    }

    @Override
    public void pause() {
        if (mediaPlayer != null)
            mediaPlayer.pause();
        if (worker != null)
            worker.doPause();
    }

    @Override
    public void forward() {

    }

    @Override
    public void backward() {

    }

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public int getPosition() {
        if (worker != null) {
            return worker.getPosition();
        }
        return 0;
    }

    public long getDuration() {
        return musicItem.getDuration();
    }


    private class Worker extends Thread {

        boolean paused = false;
        int position = 0;

        @Override
        public void run() {
            try {
                while (position < musicItem.getDuration()) {
                    sleep(1000);
                    if (!paused) {
                        position++;
                    }
                }
            } catch (InterruptedException e) {
                Log.d("Worker", "Player unbounded");
            }
        }

        void doResume() {
            paused = false;
        }

        void doPause() {
            paused = true;
        }

        boolean isPlaying() {
            return !paused;
        }

        int getPosition() {
            return position;
        }
    }
}
