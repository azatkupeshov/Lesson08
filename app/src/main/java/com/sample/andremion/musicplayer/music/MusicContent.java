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

package com.sample.andremion.musicplayer.music;

import java.io.Serializable;
import java.util.ArrayList;

public interface MusicContent {

    ArrayList<MusicItem> getAllSongs();
    ArrayList<MusicItem> getAllFavoriteSongs();
    ArrayList<MusicItem> getAllTopSongs();

    void markAsFavorite(MusicItem musicItem);
    void increaseRating(MusicItem musicItem);
    void putSong(MusicItem musicItem);

    class MusicItem implements Serializable {

        private final long mID;
        private final String mSongName;
        private final String mArtisName;
        private final long mDuration;
        private final String path;

        public MusicItem(long id, String songName, String artistName, long duration, String path) {
            mID = id;
            mSongName = songName;
            mArtisName = artistName;
            mDuration = duration;
            this.path = path;
        }

        public long getID() {
            return mID;
        }
        public String getSongName() {
            return mSongName;
        }

        public String getArtistName() {
            return mArtisName;
        }

        public long getDuration() {
            return mDuration;
        }

        public String getPath() {
            return path;
        }
    }
}
