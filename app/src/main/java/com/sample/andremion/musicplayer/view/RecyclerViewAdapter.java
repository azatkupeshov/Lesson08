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

package com.sample.andremion.musicplayer.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sample.andremion.musicplayer.R;
import com.sample.andremion.musicplayer.activities.DetailActivity;
import com.sample.andremion.musicplayer.databases.DatabaseHelper;
import com.sample.andremion.musicplayer.music.MusicContent;
import com.sample.andremion.musicplayer.music.MusicContent.MusicItem;
import com.sample.andremion.musicplayer.view.ItemTouchHelper.ItemTouchHelperAdapter;

import java.util.Collections;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    //Массив элементов MusicItem
    private final List<MusicItem> mValues;
    private final Context context;
    private final MusicContent musicContent;
    public RecyclerViewAdapter(List<MusicItem> items, Context context) {
        mValues = items;
        this.context = context;
        musicContent = new DatabaseHelper(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MusicItem musicItem = mValues.get(position);
        holder.mTitleView.setText(musicItem.getSongName());
        holder.mArtistView.setText(musicItem.getArtistName());
        holder.mDurationView.setText(DateUtils.formatElapsedTime(musicItem.getDuration()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicContent.increaseRating(musicItem);
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("musicItem", musicItem);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mValues, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }


    @Override
    public void onItemDismiss(int position) {
        final MusicItem musicItem = mValues.get(position);
        musicContent.markAsFavorite(musicItem);
        Toast.makeText(context, "Песня " + musicItem.getSongName() + " добавлена в избранное!", Toast.LENGTH_LONG).show();
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView mCoverView;
        final TextView mTitleView;
        final TextView mArtistView;
        final TextView mDurationView;
        MusicItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCoverView = view.findViewById(R.id.cover);
            mTitleView = view.findViewById(R.id.title);
            mArtistView = view.findViewById(R.id.artist);
            mDurationView = view.findViewById(R.id.duration);
        }
    }

}
