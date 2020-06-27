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
