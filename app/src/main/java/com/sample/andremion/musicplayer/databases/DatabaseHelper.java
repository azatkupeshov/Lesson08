package com.sample.andremion.musicplayer.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;

import com.sample.andremion.musicplayer.music.MusicContent;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper implements MusicContent {
    private final Context mContext;
    private final static int DB_VER = 1;
    private final static String DB_NAME = "database.db";
    private final String TABLE_NAME = "music";

    private final String COLUMN_ID = MediaStore.Audio.Media._ID;
    private final String COLUMN_ARTIST_NAME = MediaStore.Audio.Media.ARTIST;
    private final String COLUMN_SONG_NAME = MediaStore.Audio.Media.TITLE;
    private final String COLUMN_DURATION = MediaStore.Audio.Media.DURATION;

    private final String COLUMN_IS_FAVORITE = "is_favorite";
    private final String COLUMN_RATE = "rate";

    private final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ARTIST_NAME + " TEXT, " +
            COLUMN_SONG_NAME + " TEXT, " +
            COLUMN_DURATION + " INTEGER, " +
            COLUMN_IS_FAVORITE + " INTEGER, " +
            COLUMN_RATE + " INTEGER " +
            ")";
    private SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
        mContext = context;
        database = getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    @Override
    public void putSong(MusicItem musicItem) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_ARTIST_NAME, musicItem.getArtistName());
            contentValues.put(COLUMN_SONG_NAME, musicItem.getSongName());
            contentValues.put(COLUMN_DURATION, musicItem.getDuration());

            contentValues.put(COLUMN_IS_FAVORITE, "0");
            contentValues.put(COLUMN_RATE, "0");

            database.insertOrThrow(TABLE_NAME, null, contentValues);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<MusicItem> getAllSongs() {
        ArrayList<MusicContent.MusicItem> musicItems = new ArrayList<>();
        try (Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null)) {

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String songName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_NAME));
                String artistName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST_NAME));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DURATION));
                musicItems.add(new MusicContent.MusicItem(id, songName, artistName, duration / 1000));
            }
            return musicItems;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ArrayList<MusicItem> getAllFavoriteSongs() {
        ArrayList<MusicContent.MusicItem> musicItems = new ArrayList<>();
        try (Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +  COLUMN_IS_FAVORITE + "=?", new String[]{"1"})) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String songTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_NAME));
                String songArtist = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST_NAME));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DURATION));
                musicItems.add(new MusicContent.MusicItem(id, songTitle, songArtist, duration / 1000));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return musicItems;
    }

    @Override
    public ArrayList<MusicItem> getAllTopSongs() {
        ArrayList<MusicContent.MusicItem> musicItems = new ArrayList<>();
        try (Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, COLUMN_RATE + " DESC")) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String songTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_NAME));
                String songArtist = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST_NAME));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DURATION));
                musicItems.add(new MusicContent.MusicItem(id, songTitle, songArtist, duration / 1000));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return musicItems;
    }

    @Override
    public MusicItem getSong(long id) {
        try (Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null)) {
            cursor.moveToFirst();
            String songTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_NAME));
            String songArtist = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST_NAME));
            long duration = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DURATION));
            return new MusicContent.MusicItem(id, songTitle, songArtist, duration / 1000);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void markAsFavorite(long id) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_IS_FAVORITE, "1");
            database.update(TABLE_NAME, contentValues, COLUMN_ID + "=" + id, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
