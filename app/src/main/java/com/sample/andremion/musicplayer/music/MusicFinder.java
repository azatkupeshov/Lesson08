package com.sample.andremion.musicplayer.music;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.sample.andremion.musicplayer.R;
import com.sample.andremion.musicplayer.databases.DatabaseHelper;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MusicFinder {

    private Context mContext;

    public MusicFinder(Context mContext) {
        this.mContext = mContext;
    }


    //Поиск музыки
    public ArrayList<MusicContent.MusicItem> findSongsImproved() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return null;
        }

        ArrayList<MusicContent.MusicItem> musicItems = new ArrayList<>();

        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);


        if (cursor == null) {
            return null;
        }

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
            if (isMusic != 0) {
                Long songId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String songTitle = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String songDisplayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                String songArtist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

                musicItems.add(new MusicContent.MusicItem(R.drawable.album_cover_death_cab, songTitle, songArtist, duration / 1000));
            }
        }
        cursor.close();

        return musicItems;
    }

    //Поиск музыки
    public void fillDatabase(MusicContent musicContent) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return;
        }

        try (Cursor cursor = mContext.getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER)) {

            if (cursor == null) {
                return;
            }

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
                if (isMusic != 0) {
                    long songId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    String songName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String artistName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    musicContent.putSong(new MusicContent.MusicItem(songId, songName, artistName, duration));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //Поиск mp3 файлов рекурсивно
    @Nullable
    public ArrayList<MusicContent.MusicItem> findSongsList() {
        //Проверка доступности разрешения
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return null;
        }

        ArrayList<MusicContent.MusicItem> musicItems = new ArrayList<>();
        //Получаем путь к внешнему хранилищу
        String path = Environment.getExternalStorageDirectory().getPath();

        //Поиск всех файлов mp3
        ArrayList<HashMap<String, String>> songList = findFileByMask(path, ".mp3");

        if (songList != null) {
            for (int i = 0; i < songList.size(); i++) {
                String fileName = songList.get(i).get("file_name");
                String filePath = songList.get(i).get("file_path");
                //Заполняем массив с названием файлов
                musicItems.add(new MusicContent.MusicItem(R.drawable.album_cover_death_cab, fileName, "", 100));
                Log.i("file details ", " name =" + fileName + " path = " + filePath);
            }
        }

        return musicItems;
    }


    //Рекурсивный поиск файлов по маске
    @Nullable
    private ArrayList<HashMap<String, String>> findFileByMask(String rootPath, String mask) {
        //Массив данных содержащий в себе путь и имя файла
        ArrayList<HashMap<String, String>> fileList = new ArrayList<>();

        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles();
            if (files == null)
                return null;
            for (File file : files) {
                if (file.isDirectory()) {
                    //Рекурсия
                    if (findFileByMask(file.getAbsolutePath(), mask) != null) {
                        fileList.addAll(findFileByMask(file.getAbsolutePath(), mask));
                    } else {
                        break;
                    }
                }
                //Сравнение найденного файла по маске
                else if (file.getName().endsWith(mask)) {
                    HashMap<String, String> song = new HashMap<>();
                    song.put("file_path", file.getAbsolutePath());
                    song.put("file_name", file.getName());
                    fileList.add(song);
                }
            }
            return fileList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
