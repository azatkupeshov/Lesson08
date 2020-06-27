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

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.sample.andremion.musicplayer.R;
import com.sample.andremion.musicplayer.fragments.FavoriteFragment;
import com.sample.andremion.musicplayer.fragments.MusicListFragment;
import com.sample.andremion.musicplayer.fragments.TopFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int ALL_PERMISSIONS = 1;
    //Такие же как и в AndroidManifest
    private final String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Проверка необходимых разрешений
        //Необходимо делать эту операции до того как потребуется доступ к файлам
        if (!hasPermissions(permissions)) {
            //Запрос необходимых разрешений если оно не доступно.
            ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allPermission = true;
        if (requestCode == ALL_PERMISSIONS) {
            if (grantResults.length > 0 && permissions.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        allPermission = false;
                }
            }
            if (allPermission) {
                loadUI();
            }
        }
    }

    private void loadUI() {
        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new MusicListFragment(), "Песни");
        viewPagerAdapter.addFragment(new FavoriteFragment(), "Избранное");
        viewPagerAdapter.addFragment(new TopFragment(), "Топ");


        final ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                SwipeRefreshLayout.OnRefreshListener onRefreshListener =
                        (SwipeRefreshLayout.OnRefreshListener) viewPagerAdapter.getItem(tab.getPosition());
                onRefreshListener.onRefresh();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_exit: {

                //Создаем диалог
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_exit)
                        .setIcon(R.drawable.album_cover_death_cab)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create()
                        .show();

                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //Проверка разрешенеий
    private boolean hasPermissions(String... permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private static class ViewPagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        public ViewPagerAdapter addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
            return this;
        }
    }


}
