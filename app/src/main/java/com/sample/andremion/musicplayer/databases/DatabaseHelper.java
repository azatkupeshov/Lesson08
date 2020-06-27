package com.sample.andremion.musicplayer.databases;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.sample.andremion.musicplayer.music.MusicContent;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper implements MusicContent {
    private final static String TAG = DatabaseHelper.class.getSimpleName();
    private final Context mContext;
    private final static int DB_VER = 1;
    private final static String DB_NAME = "database.db";
    private final String TABLE_NAME = "music";

    private final String COLUMN_ID = MediaStore.Audio.Media._ID;
    private final String COLUMN_ARTIST_NAME = MediaStore.Audio.Media.ARTIST;
    private final String COLUMN_SONG_NAME = MediaStore.Audio.Media.TITLE;
    private final String COLUMN_DURATION = MediaStore.Audio.Media.DURATION;

    private final String COLUMN_SONG_ID = "song_id";
    private final String COLUMN_IS_FAVORITE = "is_favorite";
    private final String COLUMN_RATE = "rate";
    private final String COLUMN_PATH = "path";

    private final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SONG_ID + " INTEGER UNIQUE, " +
            COLUMN_ARTIST_NAME + " TEXT, " +
            COLUMN_SONG_NAME + " TEXT, " +
            COLUMN_DURATION + " INTEGER, " +
            COLUMN_IS_FAVORITE + " INTEGER, " +
            COLUMN_RATE + " INTEGER, " +
            COLUMN_PATH + " TEXT " +
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
            contentValues.put(COLUMN_SONG_ID, musicItem.getID());
            contentValues.put(COLUMN_ARTIST_NAME, musicItem.getArtistName());
            contentValues.put(COLUMN_SONG_NAME, musicItem.getSongName());
            contentValues.put(COLUMN_DURATION, musicItem.getDuration());
            contentValues.put(COLUMN_PATH, musicItem.getPath());
            contentValues.put(COLUMN_IS_FAVORITE, 0);
            contentValues.put(COLUMN_RATE, 0);
            database.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<MusicItem> getAllSongs() {
        ArrayList<MusicItem> musicItems = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            try (Cursor cursor = mContext.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER)) {

                if (cursor != null) {
                    for (int i = 0; i < cursor.getCount(); i++) {
                        cursor.moveToNext();
                        int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
                        if (isMusic != 0) {
                            long songId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                            String songTitle = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                            String songDisplayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                            String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                            String songArtist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                            long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                            MusicItem musicItem = new MusicItem(songId, songTitle, songArtist, duration / 1000, path);
                            putSong(musicItem);
                            musicItems.add(musicItem);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return musicItems;
    }

    @Override
    public ArrayList<MusicItem> getAllFavoriteSongs() {
        ArrayList<MusicContent.MusicItem> musicItems = new ArrayList<>();
        try (Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_IS_FAVORITE + "=?", new String[]{"1"})) {
            while (cursor.moveToNext()) {
                long song_id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SONG_ID));
                String songTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_NAME));
                String songArtist = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST_NAME));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DURATION));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATH));
                MusicItem musicItem = new MusicItem(song_id, songTitle, songArtist, duration, path);
                musicItems.add(musicItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return musicItems;
    }

    @Override
    public void markAsFavorite(MusicItem musicItem) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_IS_FAVORITE, 1);
            database.updateWithOnConflict(TABLE_NAME,
                    contentValues,
                    COLUMN_SONG_ID + "=" + musicItem.getID(),
                    null,
                    SQLiteDatabase.CONFLICT_IGNORE);
            Log.d(TAG, "Song: " + musicItem.getSongName() + " marked as favorite");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<MusicItem> getAllTopSongs() {
        ArrayList<MusicContent.MusicItem> musicItems = new ArrayList<>();
        try (Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, COLUMN_RATE + " DESC")) {
            while (cursor.moveToNext()) {
                int rate = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RATE));
                if (rate > 0) {
                    long song_id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SONG_ID));
                    String songTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_NAME));
                    String songArtist = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST_NAME));
                    long duration = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DURATION));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATH));
                    MusicItem musicItem = new MusicItem(song_id, songTitle, songArtist, duration, path);
                    musicItems.add(musicItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return musicItems;
    }

    @Override
    public void increaseRating(MusicItem musicItem) {
        try {
            Cursor cursor = database.query(TABLE_NAME,
                    null,
                    COLUMN_SONG_ID + "=?",
                    new String[]{String.valueOf(musicItem.getID())},
                    null,
                    null,
                    null);
            if (cursor == null) {
                return;
            }
            cursor.moveToFirst();
            int rate = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RATE));
            Log.d(TAG, "Song: " + musicItem.getSongName() + " Rate: " + rate);
            rate++;
            cursor.close();

            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_RATE, rate);
            database.updateWithOnConflict(TABLE_NAME,
                    contentValues,
                    COLUMN_SONG_ID + "=" + musicItem.getID(),
                    null,
                    SQLiteDatabase.CONFLICT_IGNORE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
