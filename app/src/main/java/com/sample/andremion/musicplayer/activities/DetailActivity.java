/*
 * Copyright (c) 2016. André Mion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sample.andremion.musicplayer.activities;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.sample.andremion.musicplayer.MusicService;
import com.sample.andremion.musicplayer.R;
import com.sample.andremion.musicplayer.music.MusicContent;
import com.sample.andremion.musicplayer.view.ProgressView;

public class DetailActivity extends AppCompatActivity {

    MusicService musicService;
    private ImageView mCoverView;
    private TextView tvArtistName;
    private TextView tvSongName;
    private boolean isBind = false;
    private boolean isPlay = false;
    private FloatingActionButton fab;
    private ProgressView progressView;
    ObjectAnimator objectAnimator;


    @SuppressLint("HandlerLeak")
    private final Handler mUpdateProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final int position = musicService.getPosition();
            final int duration = (int) musicService.getDuration();
            if (duration > 0)
                progressView.setProgress(position * 100 / duration);
            sendEmptyMessage(0);
        }
    };


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.LocalBinder) service).getService();
            mUpdateProgressHandler.sendEmptyMessage(0);
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
            mUpdateProgressHandler.removeMessages(0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mCoverView = findViewById(R.id.cover);
        progressView = findViewById(R.id.progress);
        tvArtistName = findViewById(R.id.tv_artist_name);
        tvSongName = findViewById(R.id.tv_song_name);
        fab = findViewById(R.id.fab);


        Intent intent = getIntent();
        MusicContent.MusicItem musicItem = (MusicContent.MusicItem) intent.getSerializableExtra("musicItem");


        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("musicItem", musicItem);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

        tvArtistName.setText(musicItem.getArtistName());
        tvSongName.setText(musicItem.getSongName());

        objectAnimator = getObjectAnimator(mCoverView);
        objectAnimator.start();
        isPlay = true;
    }

    @Override
    public void onBackPressed() {
        if (isBind) {
            musicService.stop();
            unbindService(connection);
        }
        finish();
    }

    public void onFabClick(View view) {
        if (isPlay) {
            musicService.pause();
            isPlay = false;
            fab.setImageResource(R.drawable.ic_play_animatable);
            objectAnimator.pause();
        } else {
            musicService.play();
            isPlay = true;
            fab.setImageResource(R.drawable.ic_pause_animatable);
            objectAnimator.resume();
        }
    }

    private ObjectAnimator getObjectAnimator(View imageView) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0, 360);
        animator.setDuration(3000);
        animator.setRepeatCount(Animation.INFINITE);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.setInterpolator(new LinearInterpolator());
        return animator;
    }
}
