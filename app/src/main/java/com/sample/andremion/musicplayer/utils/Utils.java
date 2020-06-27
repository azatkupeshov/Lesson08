package com.sample.andremion.musicplayer.utils;

import android.content.Context;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Utils {

    private Context mContext;
    public Utils(Context context) {
        mContext = context;
    }

    //https://habr.com/ru/post/178405/

    /**
     * Чтение из локального файла
     * @param filename Имя файла в локальной директории
     * @param data Данные в текстовом формате
     */
    public void writeLocalFile(String filename, String data) {
        File file = new File(mContext.getFilesDir(), filename);

        //Открываем поток для записи в файл
        //Эта конструкция позволит правильно закрыть поток Java 7+
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Чтение из локального файла
     * @param filename Имя локального файла
     * @return возрщаемые данные в текстовом формате
     */
    @Nullable
    public String readLocalFile(String filename) {
        File file = null;
        FileInputStream fileInputStream = null;
        try {
            file = new File(mContext.getFilesDir(), filename);
            //Открываем поток для чтения файла
            fileInputStream = new FileInputStream(file);
            //Создаем буфер
            byte[] bytes = new byte[fileInputStream.available()];
            //Читаем данные в буфер
            fileInputStream.read(bytes);

            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            //Правильное закрытие ресурсов.
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
